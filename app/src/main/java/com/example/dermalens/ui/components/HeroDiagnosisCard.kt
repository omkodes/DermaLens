package com.example.dermalens.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dermalens.ui.theme.*

@Composable
fun HeroDiagnosisCard(
    code: String,
    diseaseName: String,
    confidence: Float,
    isMalignantRisk: Boolean,
    modifier: Modifier = Modifier,
    latencyMs: Long? = null
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = MedicalSurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(MedicalOutlineVariant)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Code badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isMalignantRisk) HighRiskRedContainer else MedicalPrimaryContainer)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = code,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (isMalignantRisk) HighRiskRed else MedicalPrimary
                    )
                }

                // Status chip
                val statusText = if (isMalignantRisk) "Risk Lesion" else "Benign"
                val statusColor = if (isMalignantRisk) HighRiskRed else LowRiskGreen
                val statusBg = if (isMalignantRisk) HighRiskRedContainer else LowRiskGreenContainer

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = statusText,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = diseaseName,
                style = MaterialTheme.typography.titleLarge,
                color = MedicalOnSurface,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            ConfidenceBar(
                confidence = confidence,
                label = "Diagnostic Confidence",
                isMalignantRisk = isMalignantRisk
            )

            if (latencyMs != null && latencyMs > 0) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Inference time: ${latencyMs}ms",
                    style = MaterialTheme.typography.bodySmall,
                    color = MedicalOnSurfaceVariant
                )
            }
        }
    }
}
