package com.example.dermalens.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dermalens.ui.theme.*

@Composable
fun WipeConfirmDialog(
    onConfirmWipe: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Warning",
                tint = HighRiskRed
            )
        },
        title = {
            Text(
                text = "Wipe All Data & Reset?",
                fontWeight = FontWeight.Bold,
                color = MedicalOnSurface
            )
        },
        text = {
            Text(
                text = "This will permanently delete your profile, all on-device screening records, and cached preferences. This action cannot be undone.",
                style = MaterialTheme.typography.bodyMedium,
                color = MedicalOnSurfaceVariant,
                lineHeight = 20.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirmWipe,
                colors = ButtonDefaults.buttonColors(
                    containerColor = HighRiskRed,
                    contentColor = OnHighRiskRed
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Logout & Wipe All", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MedicalPrimary)
            ) {
                Text("Cancel", fontWeight = FontWeight.SemiBold)
            }
        },
        containerColor = MedicalSurface,
        shape = RoundedCornerShape(16.dp)
    )
}
