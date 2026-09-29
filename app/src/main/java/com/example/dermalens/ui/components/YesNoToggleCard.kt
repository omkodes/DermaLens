package com.example.dermalens.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dermalens.ui.theme.*

@Composable
fun YesNoToggleCard(
    title: String,
    isSelected: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null
) {
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onToggle(!isSelected) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = if (isSelected) MedicalPrimaryContainer.copy(alpha = 0.55f) else MedicalSurface
        ),
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) MedicalPrimary else MedicalOutlineVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                    color = if (isSelected) MedicalPrimary else MedicalOnSurface,
                    lineHeight = 20.sp
                )
                if (!description.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSelected) MedicalOnSurfaceVariant else MedicalOnSurfaceVariant.copy(alpha = 0.85f),
                        lineHeight = 17.sp
                    )
                }
            }

            Switch(
                checked = isSelected,
                onCheckedChange = { onToggle(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MedicalOnPrimary,
                    checkedTrackColor = MedicalPrimary,
                    checkedBorderColor = MedicalPrimary,
                    uncheckedThumbColor = MedicalOutline,
                    uncheckedTrackColor = MedicalSurfaceVariant,
                    uncheckedBorderColor = MedicalOutlineVariant
                )
            )
        }
    }
}
