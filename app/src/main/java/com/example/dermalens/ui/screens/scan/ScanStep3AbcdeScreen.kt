package com.example.dermalens.ui.screens.scan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dermalens.ui.components.YesNoToggleCard
import com.example.dermalens.ui.theme.*

@Composable
fun ScanStep3AbcdeScreen(
    lesionHasChanged: Boolean,
    onHasChangedToggle: (Boolean) -> Unit,
    lesionBleeding: Boolean,
    onBleedingToggle: (Boolean) -> Unit,
    lesionUnevenShape: Boolean,
    onUnevenShapeToggle: (Boolean) -> Unit,
    lesionIrregularBorder: Boolean,
    onIrregularBorderToggle: (Boolean) -> Unit,
    lesionMultipleColors: Boolean,
    onMultipleColorsToggle: (Boolean) -> Unit,
    lesionLargerThan6mm: Boolean,
    onLargerThan6mmToggle: (Boolean) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit,
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
            text = "ABCDE Lesion Criteria",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MedicalPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Standard clinical criteria used by dermatologists to screen for melanoma.",
            style = MaterialTheme.typography.bodySmall,
            color = MedicalOnSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            YesNoToggleCard(
                title = "E — Evolving / Changing (+3 pts)",
                description = "Has this spot changed in size, shape, color, or elevation recently?",
                isSelected = lesionHasChanged,
                onToggle = onHasChangedToggle
            )

            YesNoToggleCard(
                title = "S — Bleeding or Oozing (+4 pts)",
                description = "Does it bleed, ooze, itch, flake, or persistently fail to heal?",
                isSelected = lesionBleeding,
                onToggle = onBleedingToggle
            )

            YesNoToggleCard(
                title = "A — Asymmetry (Qualitative)",
                description = "If you divide the spot in half, do the two sides look noticeably different?",
                isSelected = lesionUnevenShape,
                onToggle = onUnevenShapeToggle
            )

            YesNoToggleCard(
                title = "B — Border Irregularity (Qualitative)",
                description = "Are the edges uneven, scalloped, notched, or poorly defined?",
                isSelected = lesionIrregularBorder,
                onToggle = onIrregularBorderToggle
            )

            YesNoToggleCard(
                title = "C — Color Variation (+2 pts)",
                description = "Does the lesion have multiple colors (brown, black, tan, pink, or white)?",
                isSelected = lesionMultipleColors,
                onToggle = onMultipleColorsToggle
            )

            YesNoToggleCard(
                title = "D — Diameter > 6mm (+2 pts)",
                description = "Is the lesion larger than 6 millimeters (about the size of a pencil eraser)?",
                isSelected = lesionLargerThan6mm,
                onToggle = onLargerThan6mmToggle
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Navigation Row
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
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MedicalPrimary),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(MedicalOutlineVariant)
                )
            ) {
                Text("Back", fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick = onNext,
                modifier = Modifier
                    .weight(1.5f)
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MedicalPrimary,
                    contentColor = MedicalOnPrimary
                )
            ) {
                Text(
                    text = "Next: Review Inputs",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}
