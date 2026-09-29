package com.example.dermalens.ui.screens.scan

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dermalens.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun ScanStep5ProcessingScreen(
    currentStatus: String? = null,
    modifier: Modifier = Modifier
) {
    val defaultStages = listOf(
        "Analysing lesion patterns with Swin AI…",
        "Evaluating clinical symptom indicators…",
        "Calculating multimodal risk index…",
        "Finalizing diagnostic triage…"
    )

    var stageIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1300)
            stageIndex = (stageIndex + 1) % defaultStages.size
        }
    }

    val displayStatus = currentStatus ?: defaultStages[stageIndex]

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MedicalBackground)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Minimalist Circular Progress Halo
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(MedicalPrimaryContainer),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MedicalPrimary,
                    strokeWidth = 4.dp,
                    modifier = Modifier.size(54.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Processing Skin Triage",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MedicalOnSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            AnimatedContent(
                targetState = displayStatus,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "StatusTextTransition"
            ) { status ->
                Text(
                    text = status,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MedicalPrimary,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MedicalSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Zero Cloud Exposure\nYour lesion photo is processed in RAM and destroyed upon completion.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MedicalOnSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        }
    }
}
