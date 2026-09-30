package com.example.dermalens.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dermalens.data.local.entity.ScreeningHistoryEntity
import com.example.dermalens.ml.model.SkinDiagnosis
import com.example.dermalens.ui.components.ClinicalAlertCard
import com.example.dermalens.ui.components.DifferentialDrawer
import com.example.dermalens.ui.components.HeroDiagnosisCard
import com.example.dermalens.ui.components.RiskLevelBadge
import com.example.dermalens.ui.theme.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryDetailBottomSheet(
    screening: ScreeningHistoryEntity,
    onDismiss: () -> Unit,
    onNavigateToChat: (Long) -> Unit = {}
) {
    val dateStr = SimpleDateFormat("MMMM dd, yyyy • HH:mm", Locale.getDefault())
        .format(Date(screening.timestamp))

    val parsedDiagnoses: List<SkinDiagnosis> = try {
        val listType = object : TypeToken<List<SkinDiagnosis>>() {}.type
        Gson().fromJson(screening.imagePredictionsJson, listType) ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }

    val parsedFactors: List<String> = try {
        val listType = object : TypeToken<List<String>>() {}.type
        Gson().fromJson(screening.topFactorsJson, listType) ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }

    val isMalignant = screening.imageIsMalignantRisk && screening.imageTopConfidence >= 0.40f
    val isAK = screening.imageTopCode == "AK" && screening.imageTopConfidence >= 0.40f

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MedicalBackground,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = { BottomSheetDefaults.DragHandle(color = MedicalOutlineVariant) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Sheet Header with Date and Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Screening Details",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MedicalOnSurface
                    )
                    Text(
                        text = dateStr,
                        style = MaterialTheme.typography.bodySmall,
                        color = MedicalOnSurfaceVariant
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MedicalOnSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Risk Level Badge Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Risk Level:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MedicalOnSurface
                )
                RiskLevelBadge(riskLevel = screening.riskLevel)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Clinical Alert Card (if malignant or pre-cancerous)
            if (isMalignant) {
                ClinicalAlertCard(
                    alertMessage = "High Risk Finding: Top diagnosis is ${screening.imageTopDiseaseName} (${(screening.imageTopConfidence * 100).toInt()}%). Clinical evaluation recommended.",
                    isHighRisk = true
                )
                Spacer(modifier = Modifier.height(14.dp))
            } else if (isAK) {
                ClinicalAlertCard(
                    alertMessage = "Pre-Cancerous Finding: Lesion patterns suggest Actinic Keratosis (${(screening.imageTopConfidence * 100).toInt()}%). Clinical evaluation recommended.",
                    isHighRisk = false
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Primary Hero Diagnosis Card
            HeroDiagnosisCard(
                code = screening.imageTopCode,
                diseaseName = screening.imageTopDiseaseName,
                confidence = screening.imageTopConfidence,
                isMalignantRisk = screening.imageIsMalignantRisk,
                latencyMs = screening.imageLatencyMs
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Multimodal Risk Index Bar
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.outlinedCardColors(containerColor = MedicalSurface),
                border = BorderStroke(1.dp, MedicalOutlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Overall Multimodal Risk Index",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MedicalOnSurface
                        )
                        Text(
                            text = "${(screening.fusionScore * 100).toInt()} / 100",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = when (screening.riskLevel) {
                                "HIGH" -> HighRiskRed
                                "MEDIUM" -> MediumRiskOrange
                                else -> LowRiskGreen
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { screening.fusionScore.coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = when (screening.riskLevel) {
                            "HIGH" -> HighRiskRed
                            "MEDIUM" -> MediumRiskOrange
                            else -> LowRiskGreen
                        },
                        trackColor = MedicalSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Clinical Uncertainty Banner (if flagged)
            if (screening.isUncertain) {
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = MediumRiskOrangeContainer),
                    border = BorderStroke(1.dp, MediumRiskOrange)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MediumRiskOrange,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Clinical Uncertainty: Divergence detected between image features and symptoms.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MedicalOnSurface
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Top Contributing Factors
            if (parsedFactors.isNotEmpty()) {
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = MedicalSurface),
                    border = BorderStroke(1.dp, MedicalOutlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Contributing Risk Factors",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MedicalOnSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        parsedFactors.forEach { factor ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text("•", fontWeight = FontWeight.Bold, color = MedicalPrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = factor,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MedicalOnSurfaceVariant
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Expandable 8-Class Differential Drawer
            if (parsedDiagnoses.isNotEmpty()) {
                DifferentialDrawer(diagnoses = parsedDiagnoses)
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Discuss with AI Button
            Button(
                onClick = {
                    onDismiss()
                    onNavigateToChat(screening.id)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MedicalPrimary,
                    contentColor = MedicalOnPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ChatBubbleOutline,
                    contentDescription = "Discuss with AI",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Discuss with AI",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MedicalOnPrimary.copy(alpha = 0.22f)
                ) {
                    Text(
                        text = "Online feature",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MedicalOnPrimary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))

            // Disclaimer
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MedicalSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Medical Disclaimer: Historical screening records are informational. Always seek professional clinical evaluation for persistent or changing skin spots.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MedicalOnSurfaceVariant,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
