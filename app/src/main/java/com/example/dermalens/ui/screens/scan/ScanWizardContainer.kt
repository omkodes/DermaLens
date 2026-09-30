package com.example.dermalens.ui.screens.scan

import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.dermalens.ui.components.AbandonScanDialog
import com.example.dermalens.ui.theme.*
import com.example.dermalens.ui.viewmodel.ScreeningViewModel

@Composable
fun ScanWizardContainer(
    viewModel: ScreeningViewModel,
    onDismissWizard: () -> Unit,
    onNavigateToChat: (Long) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val currentStep by viewModel.currentStep.collectAsState()
    val photoBitmap by viewModel.capturedPhoto.collectAsState()
    val selectedSymptoms by viewModel.selectedSymptoms.collectAsState()

    val lesionHasChanged by viewModel.hasChanged.collectAsState()
    val lesionBleeding by viewModel.bleeding.collectAsState()
    val lesionUnevenShape by viewModel.asymmetry.collectAsState()
    val lesionIrregularBorder by viewModel.border.collectAsState()
    val lesionMultipleColors by viewModel.multipleColors.collectAsState()
    val lesionLargerThan6mm by viewModel.largerThan6mm.collectAsState()

    val resultEntity by viewModel.analysisResult.collectAsState()

    var showAbandonDialog by rememberSaveable { mutableStateOf(false) }

    // Intercept hardware and gesture back navigation
    BackHandler(enabled = true) {
        if (currentStep in 2..5) {
            showAbandonDialog = true
        } else {
            onDismissWizard()
        }
    }

    if (showAbandonDialog) {
        AbandonScanDialog(
            onConfirmAbandon = {
                showAbandonDialog = false
                onDismissWizard()
            },
            onDismiss = { showAbandonDialog = false }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MedicalBackground)
    ) {
        // Top Wizard Header with Close / Abandon button (Hidden on result step)
        if (currentStep < 6) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    if (currentStep > 1) {
                        showAbandonDialog = true
                    } else {
                        onDismissWizard()
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Scan",
                        tint = MedicalOnSurfaceVariant
                    )
                }

                Text(
                    text = "Screening Step $currentStep of 4",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MedicalOnSurfaceVariant
                )

                // Invisible spacer to balance layout
                Spacer(modifier = Modifier.size(48.dp))
            }

            LinearProgressIndicator(
                progress = { (currentStep.coerceAtMost(4)) / 4f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp),
                color = MedicalPrimary,
                trackColor = MedicalPrimaryContainer
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScanStepTransition"
            ) { step ->
                when (step) {
                    1 -> ScanStep1(
                        imageBitmap = photoBitmap,
                        onImageCaptured = { viewModel.setCapturedPhoto(it) },
                        onRetake = { viewModel.setCapturedPhoto(null) },
                        onNext = { viewModel.setStep(2) }
                    )

                    2 -> ScanStep2SymptomsScreen(
                        selectedSymptoms = selectedSymptoms,
                        onToggleSymptom = { key, _ ->
                            viewModel.toggleSymptom(key)
                        },
                        onBack = { viewModel.setStep(1) },
                        onNext = { viewModel.setStep(3) }
                    )

                    3 -> ScanStep3AbcdeScreen(
                        lesionHasChanged = lesionHasChanged,
                        onHasChangedToggle = { viewModel.setHasChanged(it) },
                        lesionBleeding = lesionBleeding,
                        onBleedingToggle = { viewModel.setBleeding(it) },
                        lesionUnevenShape = lesionUnevenShape,
                        onUnevenShapeToggle = { viewModel.setAsymmetry(it) },
                        lesionIrregularBorder = lesionIrregularBorder,
                        onIrregularBorderToggle = { viewModel.setBorder(it) },
                        lesionMultipleColors = lesionMultipleColors,
                        onMultipleColorsToggle = { viewModel.setMultipleColors(it) },
                        lesionLargerThan6mm = lesionLargerThan6mm,
                        onLargerThan6mmToggle = { viewModel.setLargerThan6mm(it) },
                        onBack = { viewModel.setStep(2) },
                        onNext = { viewModel.setStep(4) }
                    )

                    4 -> {
                        val positiveAbcdeCount = listOf(
                            lesionHasChanged,
                            lesionBleeding,
                            lesionUnevenShape,
                            lesionIrregularBorder,
                            lesionMultipleColors,
                            lesionLargerThan6mm
                        ).count { it }

                        ScanStep4ReviewScreen(
                            imageBitmap = photoBitmap,
                            selectedSymptomsCount = selectedSymptoms.size,
                            positiveAbcdeCount = positiveAbcdeCount,
                            onEditPhoto = { viewModel.setStep(1) },
                            onEditSymptoms = { viewModel.setStep(2) },
                            onEditAbcde = { viewModel.setStep(3) },
                            onStartAnalysis = {
                                val bitmap = photoBitmap
                                if (bitmap != null) {
                                    viewModel.analyzeImage(bitmap)
                                }
                            },
                            onBack = { viewModel.setStep(3) }
                        )
                    }

                    5 -> ScanStep5ProcessingScreen()

                    6 -> {
                        resultEntity?.let { entity ->
                            ScanStep6ResultScreen(
                                resultEntity = entity,
                                onDoneClick = onDismissWizard,
                                onChatClick = onNavigateToChat
                            )
                        } ?: Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = MedicalPrimary)
                        }
                    }
                }
            }
        }
    }
}
