package com.example.dermalens.ui.screens.scan

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dermalens.ui.theme.*

@Composable
fun ScanStep4ReviewScreen(
    imageBitmap: Bitmap?,
    selectedSymptomsCount: Int,
    positiveAbcdeCount: Int,
    onEditPhoto: () -> Unit,
    onEditSymptoms: () -> Unit,
    onEditAbcde: () -> Unit,
    onStartAnalysis: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MedicalBackground)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Review Clinical Inputs",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MedicalPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Verify your lesion photo and checklist answers before running local AI inference.",
            style = MaterialTheme.typography.bodySmall,
            color = MedicalOnSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Photo Preview Card
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.outlinedCardColors(containerColor = MedicalSurface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = SolidColor(MedicalOutlineVariant)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MedicalSurfaceVariant)
                            .border(1.dp, MedicalOutlineVariant, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageBitmap != null) {
                            Image(
                                bitmap = imageBitmap.asImageBitmap(),
                                contentDescription = "Cropped lesion preview",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text("No image", style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Lesion Photograph",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MedicalOnSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "1:1 square center crop ready",
                            style = MaterialTheme.typography.bodySmall,
                            color = MedicalOnSurfaceVariant
                        )
                    }

                    TextButton(onClick = onEditPhoto) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit photo",
                            tint = MedicalPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit", color = MedicalPrimary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Symptoms Summary Card
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.outlinedCardColors(containerColor = MedicalSurface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = SolidColor(MedicalOutlineVariant)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Associated Symptoms",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MedicalOnSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (selectedSymptomsCount == 0) {
                                "0 symptoms selected (all negative)"
                            } else {
                                "$selectedSymptomsCount symptom(s) flagged positive"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = if (selectedSymptomsCount > 0) MedicalPrimary else MedicalOnSurfaceVariant,
                            fontWeight = if (selectedSymptomsCount > 0) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }

                    TextButton(onClick = onEditSymptoms) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit symptoms",
                            tint = MedicalPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit", color = MedicalPrimary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // ABCDE Criteria Summary Card
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.outlinedCardColors(containerColor = MedicalSurface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = SolidColor(MedicalOutlineVariant)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ABCDE Lesion Criteria",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MedicalOnSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (positiveAbcdeCount == 0) {
                                "0 of 6 positive criteria"
                            } else {
                                "$positiveAbcdeCount of 6 criteria flagged positive"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = if (positiveAbcdeCount > 0) HighRiskRed else MedicalOnSurfaceVariant,
                            fontWeight = if (positiveAbcdeCount > 0) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }

                    TextButton(onClick = onEditAbcde) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit ABCDE",
                            tint = MedicalPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit", color = MedicalPrimary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Privacy & Processing Notice
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MedicalSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Analysis runs 100% on your device using Swin Transformer FP16 & Naive Bayes ONNX models. Your photo is securely discarded immediately after processing.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MedicalOnSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MedicalPrimary),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = SolidColor(MedicalOutlineVariant)
                )
            ) {
                Text("Back", fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick = onStartAnalysis,
                modifier = Modifier
                    .weight(1.8f)
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MedicalPrimary,
                    contentColor = MedicalOnPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Analyse Now",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}
