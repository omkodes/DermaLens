package com.example.dermalens.ui.screens.scan

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
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

@Composable
fun ScanStep6ResultScreen(
    resultEntity: ScreeningHistoryEntity,
    diagnoses: List<SkinDiagnosis> = emptyList(),
    topFactors: List<String> = emptyList(),
    onDoneClick: () -> Unit,
    onChatClick: (Long) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Parse diagnoses from JSON if not provided directly
    val parsedDiagnoses: List<SkinDiagnosis> = if (diagnoses.isNotEmpty()) {
        diagnoses
    } else {
        try {
            val listType = object : TypeToken<List<SkinDiagnosis>>() {}.type
            Gson().fromJson(resultEntity.imagePredictionsJson, listType) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Parse top factors from JSON if not provided directly
    val parsedFactors: List<String> = if (topFactors.isNotEmpty()) {
        topFactors
    } else {
        try {
            val listType = object : TypeToken<List<String>>() {}.type
            Gson().fromJson(resultEntity.topFactorsJson, listType) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    val isMalignant = resultEntity.imageIsMalignantRisk && resultEntity.imageTopConfidence >= 0.40f
    val isAK = resultEntity.imageTopCode == "AK" && resultEntity.imageTopConfidence >= 0.40f

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MedicalBackground)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Top Risk Level Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Triage Assessment",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MedicalOnSurface,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Text(
                    text = "Multimodal Clinical Analysis",
                    style = MaterialTheme.typography.bodySmall,
                    color = MedicalOnSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            RiskLevelBadge(riskLevel = resultEntity.riskLevel)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Scrollable Body
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Clinical Safety Override Alert Card (if malignant or pre-cancerous)
            if (isMalignant) {
                ClinicalAlertCard(
                    alertMessage = "High Risk Lesion: Top finding is ${resultEntity.imageTopDiseaseName} (${(resultEntity.imageTopConfidence * 100).toInt()}%). Prompt in-person evaluation by a dermatologist is recommended.",
                    isHighRisk = true
                )
            } else if (isAK) {
                ClinicalAlertCard(
                    alertMessage = "Pre-Cancerous Finding: Lesion patterns suggest Actinic Keratosis (${(resultEntity.imageTopConfidence * 100).toInt()}%). Clinical evaluation recommended.",
                    isHighRisk = false
                )
            }

            // 2. Primary Hero Diagnosis Card
            HeroDiagnosisCard(
                code = resultEntity.imageTopCode,
                diseaseName = resultEntity.imageTopDiseaseName,
                confidence = resultEntity.imageTopConfidence,
                isMalignantRisk = resultEntity.imageIsMalignantRisk,
                latencyMs = resultEntity.imageLatencyMs
            )

            // 3. Multimodal Risk Index Bar
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.outlinedCardColors(containerColor = MedicalSurface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = SolidColor(MedicalOutlineVariant)
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
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
                            text = "${(resultEntity.fusionScore * 100).toInt()} / 100",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = when (resultEntity.riskLevel) {
                                "HIGH" -> HighRiskRed
                                "MEDIUM" -> MediumRiskOrange
                                else -> LowRiskGreen
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { resultEntity.fusionScore.coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = when (resultEntity.riskLevel) {
                            "HIGH" -> HighRiskRed
                            "MEDIUM" -> MediumRiskOrange
                            else -> LowRiskGreen
                        },
                        trackColor = MedicalSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Synthesized from Swin FP16 (40%), Symptom Model (35%), and ABCDE/Profile risk (25%).",
                        style = MaterialTheme.typography.bodySmall,
                        color = MedicalOnSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }

            // 4. Uncertainty Banner (if discrepancy > 0.40)
            if (resultEntity.isUncertain) {
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = MediumRiskOrangeContainer),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = SolidColor(MediumRiskOrange)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MediumRiskOrange,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Clinical Uncertainty: Photographic patterns and reported clinical symptoms yielded diverging risk assessments. A physical clinical examination is strongly advised.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MedicalOnSurface,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // 5. Dynamic Top Factors List
            if (parsedFactors.isNotEmpty()) {
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = MedicalSurface),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = SolidColor(MedicalOutlineVariant)
                    )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Primary Contributing Factors",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MedicalOnSurface
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        parsedFactors.forEach { factor ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = "•",
                                    fontWeight = FontWeight.Bold,
                                    color = MedicalPrimary,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = factor,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MedicalOnSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // 6. Expandable 8-Class ISIC Differential Drawer
            if (parsedDiagnoses.isNotEmpty()) {
                DifferentialDrawer(diagnoses = parsedDiagnoses)
            }

            // 7. Medical Disclaimer
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MedicalSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Medical Disclaimer: DermaLens triage estimates are for informational guidance and do not constitute a formal diagnosis or medical advice. Consult a licensed dermatologist for full evaluation.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MedicalOnSurfaceVariant,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(14.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Action Buttons Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedButton(
                    onClick = {
                        onChatClick(resultEntity.id)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MedicalPrimary),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = SolidColor(MedicalOutlineVariant)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Chat AI", fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Requires Internet",
                    fontSize = 11.sp,
                    color = MedicalOutline
                )
            }

            Button(
                onClick = onDoneClick,
                modifier = Modifier
                    .weight(1.2f)
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MedicalPrimary,
                    contentColor = MedicalOnPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Done",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}
