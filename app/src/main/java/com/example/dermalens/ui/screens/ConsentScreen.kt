package com.example.dermalens.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dermalens.ui.theme.*

@Composable
fun ConsentScreen(
    onAgreeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MedicalBackground)
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Clinical Disclaimer & Terms",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MedicalPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Please review the intended use of DermaLens before continuing.",
            style = MaterialTheme.typography.bodyMedium,
            color = MedicalOnSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedCard(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.outlinedCardColors(containerColor = MedicalSurface),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(MedicalOutlineVariant)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Text(
                    text = "Medical Disclaimer",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MedicalOnSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "DermaLens is an on-device artificial intelligence triage support tool designed to assist individuals in assessing skin lesions and symptoms. It is NOT a substitute for professional medical diagnosis, advice, or treatment.\n\n" +
                            "Always seek the advice of a board-certified dermatologist or qualified health provider with any questions you may have regarding a medical condition or unusual skin spots.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MedicalOnSurfaceVariant,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "100% On-Device Privacy Guarantee",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MedicalOnSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "All image processing and symptom evaluations occur exclusively on your device using local neural network models. Photos are held in temporary memory only during analysis and are immediately deleted upon inference completion. Zero images are ever stored or uploaded to cloud servers.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MedicalOnSurfaceVariant,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Emergency Warning",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = HighRiskRed
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "If you observe rapidly growing, bleeding, or severely painful lesions, do not delay seeking immediate clinical examination.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MedicalOnSurfaceVariant,
                    lineHeight = 22.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onAgreeClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MedicalPrimary)
        ) {
            Text(
                text = "I Understand & Agree",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MedicalOnPrimary
            )
        }
    }
}
