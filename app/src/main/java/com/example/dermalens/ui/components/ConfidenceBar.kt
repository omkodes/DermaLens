package com.example.dermalens.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dermalens.ui.theme.*

@Composable
fun ConfidenceBar(
    confidence: Float,
    modifier: Modifier = Modifier,
    label: String? = null,
    isMalignantRisk: Boolean = false
) {
    val animatedProgress by animateFloatAsState(
        targetValue = confidence.coerceIn(0f, 1f),
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
        label = "ConfidenceProgress"
    )

    val barColor = if (isMalignantRisk && confidence >= 0.40f) {
        HighRiskRed
    } else {
        MedicalPrimary
    }

    val trackColor = if (isMalignantRisk && confidence >= 0.40f) {
        HighRiskRedContainer
    } else {
        MedicalSurfaceVariant
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!label.isNullOrBlank()) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MedicalOnSurfaceVariant
                )
            }
            Text(
                text = "${(confidence * 100).toInt()}%",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = barColor
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = barColor,
            trackColor = trackColor,
            strokeCap = StrokeCap.Round
        )
    }
}
