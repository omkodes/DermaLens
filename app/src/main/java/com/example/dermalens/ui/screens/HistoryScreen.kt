package com.example.dermalens.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dermalens.data.local.entity.ScreeningHistoryEntity
import com.example.dermalens.ui.components.ConfidenceBar
import com.example.dermalens.ui.components.RiskLevelBadge
import com.example.dermalens.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    historyList: List<ScreeningHistoryEntity>,
    onStartScanClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedScreening by remember { mutableStateOf<ScreeningHistoryEntity?>(null) }

    if (selectedScreening != null) {
        HistoryDetailBottomSheet(
            screening = selectedScreening!!,
            onDismiss = { selectedScreening = null }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MedicalBackground)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Title and Subtitle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Screening Archive",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MedicalPrimary
                )
                Text(
                    text = "Past on-device lesion evaluations and risk records.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MedicalOnSurfaceVariant
                )
            }

            if (historyList.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = MedicalPrimaryContainer,
                    border = BorderStroke(1.dp, MedicalPrimary)
                ) {
                    Text(
                        text = "${historyList.size} scans",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MedicalPrimary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (historyList.isEmpty()) {
            // Empty State
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = MedicalSurface),
                    border = BorderStroke(1.dp, MedicalOutlineVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No Screenings Yet",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MedicalOnSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "You haven't performed any skin triage scans yet. Tap the button below to start your first evaluation.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MedicalOnSurfaceVariant,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = onStartScanClick,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MedicalPrimary,
                                contentColor = MedicalOnPrimary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Start First Scan", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            // Reverse-Chronological Screening List
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(historyList, key = { it.id }) { scan ->
                    val dateStr = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault())
                        .format(Date(scan.timestamp))

                    OutlinedCard(
                        onClick = { selectedScreening = scan },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.outlinedCardColors(containerColor = MedicalSurface),
                        border = BorderStroke(1.dp, MedicalOutlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            // Top Row: Date and Risk Badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = dateStr,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MedicalOnSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                RiskLevelBadge(riskLevel = scan.riskLevel)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Disease Name
                            Text(
                                text = scan.imageTopDiseaseName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MedicalOnSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Confidence Bar
                            ConfidenceBar(
                                confidence = scan.imageTopConfidence,
                                label = "Model Confidence"
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Bottom Row: Risk Index and View Details Link
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Risk Index: ${(scan.fusionScore * 100).toInt()} / 100",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MedicalOnSurfaceVariant
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "View Breakdown",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MedicalPrimary
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = null,
                                        tint = MedicalPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
