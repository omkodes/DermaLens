package com.example.dermalens.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dermalens.ui.theme.*

@Composable
fun ClinicalAlertCard(
    alertMessage: String,
    modifier: Modifier = Modifier,
    isHighRisk: Boolean = true
) {
    val bgColor = if (isHighRisk) HighRiskRedContainer else MediumRiskOrangeContainer
    val borderColor = if (isHighRisk) HighRiskRed else MediumRiskOrange
    val iconColor = if (isHighRisk) HighRiskRed else MediumRiskOrange
    val headerText = if (isHighRisk) "HIGH RISK CLINICAL ALERT" else "PRE-CANCEROUS LESION ALERT"

    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = bgColor),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = SolidColor(borderColor)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Alert",
                tint = iconColor,
                modifier = Modifier
                    .size(24.dp)
                    .padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = headerText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = borderColor,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = alertMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MedicalOnSurface,
                    lineHeight = 20.sp
                )
            }
        }
    }
}
