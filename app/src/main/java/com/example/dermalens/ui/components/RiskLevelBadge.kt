package com.example.dermalens.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dermalens.ui.theme.*

@Composable
fun RiskLevelBadge(
    riskLevel: String,
    modifier: Modifier = Modifier
) {
    val (label, textColor, bgColor) = when (riskLevel.uppercase()) {
        "HIGH" -> Triple("🔴 HIGH RISK", HighRiskRed, HighRiskRedContainer)
        "MEDIUM" -> Triple("🟡 MEDIUM RISK", MediumRiskOrange, MediumRiskOrangeContainer)
        else -> Triple("🟢 LOW RISK", LowRiskGreen, LowRiskGreenContainer)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            letterSpacing = 0.5.sp
        )
    }
}
