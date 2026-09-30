package com.example.dermalens.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dermalens.data.local.entity.ScreeningHistoryEntity
import com.example.dermalens.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    userName: String,
    scanCount: Int,
    latestScan: ScreeningHistoryEntity?,
    onStartScanClick: () -> Unit,
    onViewLatestScanClick: ((ScreeningHistoryEntity) -> Unit)? = null,
    onViewHistoryClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MedicalBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(18.dp))

        // 1. Greeting Header
        Text(
            text = "Hello, $userName",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MedicalOnSurface,
            fontSize = 28.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Clinical Skin Triage Dashboard",
            style = MaterialTheme.typography.bodyMedium,
            color = MedicalOnSurfaceVariant,
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 2. Quick Stats Pill Card (Total scans & Offline status)
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFFE4F3EF),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Column: Total scans
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$scanCount",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF07483A)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Total scans",
                        style = MaterialTheme.typography.labelMedium,
                        color = MedicalOnSurfaceVariant,
                        fontSize = 13.sp
                    )
                }

                // Vertical Divider
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(38.dp)
                        .background(Color(0xFFC3DFD8))
                )

                // Right Column: Offline status
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "100%",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF07483A)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Offline",
                        style = MaterialTheme.typography.labelMedium,
                        color = MedicalOnSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // 3. Latest Screening Section
        if (latestScan != null) {
            val dateFormatted = remember(latestScan.timestamp) {
                SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(latestScan.timestamp))
            }
            val riskScoreInt = (latestScan.fusionScore * 100).toInt().coerceIn(0, 100)
            val riskLevelUpper = latestScan.riskLevel.uppercase()

            val (riskColor, riskBgColor, riskLabel) = when (riskLevelUpper) {
                "HIGH" -> Triple(HighRiskRed, HighRiskRedContainer, "High risk")
                "MEDIUM" -> Triple(MediumRiskOrange, MediumRiskOrangeContainer, "Medium risk")
                else -> Triple(Color(0xFF1B6B52), Color(0xFFE3F5E7), "Low risk")
            }

            // Subtitle Header
            Text(
                text = "LATEST SCREENING · ${dateFormatted.uppercase()}",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF7A8684),
                letterSpacing = 1.1.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Hero Metric Row: Big Score on Left + Circular Gauge on Right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Column: Score + Risk Pill
                Column(
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "$riskScoreInt",
                            fontSize = 62.sp,
                            fontWeight = FontWeight.Bold,
                            color = riskColor,
                            lineHeight = 62.sp
                        )
                        Text(
                            text = "/100",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF5A6664),
                            modifier = Modifier.padding(bottom = 10.dp, start = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = riskBgColor
                    ) {
                        Text(
                            text = riskLabel,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = riskColor,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Right Column: Circular Confidence Gauge
                Box(
                    modifier = Modifier.size(92.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { latestScan.imageTopConfidence.coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxSize(),
                        color = Color(0xFF00695C),
                        trackColor = Color(0xFFDCECE7),
                        strokeWidth = 8.dp,
                        strokeCap = StrokeCap.Round
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "${(latestScan.imageTopConfidence * 100).toInt()}%",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MedicalOnSurface
                        )
                        Text(
                            text = "confidence",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = MedicalOnSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Disease Finding Headline
            Text(
                text = latestScan.imageTopDiseaseName,
                style = MaterialTheme.typography.titleLarge,
                fontSize = 21.sp,
                fontWeight = FontWeight.SemiBold,
                color = MedicalOnSurface,
                lineHeight = 28.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            // "View details →" link
            Row(
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onViewLatestScanClick?.invoke(latestScan) ?: onViewHistoryClick()
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "View details",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF00695C),
                    textDecoration = TextDecoration.Underline
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = Color(0xFF00695C),
                    modifier = Modifier.size(16.dp)
                )
            }
        } else {
            // Friendly Empty State when no scans exist
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF7FBF9),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No Screenings Yet",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MedicalOnSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Take a photo of any suspicious skin mole or spot to get an instant on-device clinical checkup.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MedicalOnSurfaceVariant,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        // 4. Primary CTA: "Start new scan" pill button
        Button(
            onClick = onStartScanClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF073B30),
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Start new scan",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}
