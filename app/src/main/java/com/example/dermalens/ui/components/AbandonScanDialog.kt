package com.example.dermalens.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dermalens.ui.theme.*

@Composable
fun AbandonScanDialog(
    onConfirmAbandon: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Abandon Scan?",
                fontWeight = FontWeight.Bold,
                color = MedicalOnSurface
            )
        },
        text = {
            Text(
                text = "Are you sure you want to abandon the current screening? Your photo and answered symptoms will not be saved.",
                style = MaterialTheme.typography.bodyMedium,
                color = MedicalOnSurfaceVariant,
                lineHeight = 20.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirmAbandon,
                colors = ButtonDefaults.buttonColors(
                    containerColor = HighRiskRed,
                    contentColor = OnHighRiskRed
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Abandon", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MedicalPrimary)
            ) {
                Text("Continue Scan", fontWeight = FontWeight.SemiBold)
            }
        },
        containerColor = MedicalSurface,
        shape = RoundedCornerShape(16.dp)
    )
}
