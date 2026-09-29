package com.example.dermalens.ui.screens.scan

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dermalens.ui.theme.*

data class SymptomItem(
    val key: String,
    val title: String,
    val category: String
)

val CURATED_SKIN_SYMPTOMS = listOf(
    // Category 1: Skin Appearance
    SymptomItem("itching", "Itching", "Skin Appearance"),
    SymptomItem("skin_rash", "Skin Rash", "Skin Appearance"),
    SymptomItem("skin_peeling", "Skin Peeling", "Skin Appearance"),
    SymptomItem("silver_like_dusting", "Silvery Scales", "Skin Appearance"),
    SymptomItem("blister", "Blisters", "Skin Appearance"),
    SymptomItem("red_sore_around_nose", "Facial Sore", "Skin Appearance"),
    SymptomItem("yellow_crust_ooze", "Yellow Crust / Ooze", "Skin Appearance"),
    SymptomItem("pus_filled_pimples", "Pus-Filled Pimples", "Skin Appearance"),
    SymptomItem("blackheads", "Blackheads", "Skin Appearance"),
    SymptomItem("scurring", "Rough / Scurring", "Skin Appearance"),
    SymptomItem("dischromic_patches", "Discolored Patches", "Skin Appearance"),

    // Category 2: Skin Texture & Nails
    SymptomItem("nodal_skin_eruptions", "Nodal Eruptions", "Skin Texture & Nails"),
    SymptomItem("small_dents_in_nails", "Nail Dents / Pitting", "Skin Texture & Nails"),
    SymptomItem("inflammatory_nails", "Inflamed Nails", "Skin Texture & Nails"),
    SymptomItem("brittle_nails", "Brittle Nails", "Skin Texture & Nails"),

    // Category 3: Systemic Indicators
    SymptomItem("fatigue", "Fatigue", "Systemic Indicators"),
    SymptomItem("loss_of_appetite", "Loss of Appetite", "Systemic Indicators"),
    SymptomItem("weight_loss", "Weight Loss", "Systemic Indicators"),
    SymptomItem("high_fever", "High Fever", "Systemic Indicators"),
    SymptomItem("malaise", "General Malaise", "Systemic Indicators"),
    SymptomItem("yellowish_skin", "Yellowish Skin", "Systemic Indicators"),
    SymptomItem("swelled_lymph_nodes", "Swollen Lymph Nodes", "Systemic Indicators")
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ScanStep2SymptomsScreen(
    selectedSymptoms: Set<String>,
    onToggleSymptom: (String, Boolean) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedCount = selectedSymptoms.size
    val groupedSymptoms = CURATED_SKIN_SYMPTOMS.groupBy { it.category }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MedicalBackground)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Header & Live Counter Chip
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Associated Symptoms",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MedicalPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Tap capsule pills to select any co-occurring signs.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MedicalOnSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Surface(
                shape = RoundedCornerShape(50.dp),
                color = if (selectedCount > 0) MedicalPrimaryContainer else MedicalSurfaceVariant,
                border = BorderStroke(
                    width = 1.dp,
                    color = if (selectedCount > 0) MedicalPrimary else MedicalOutlineVariant
                )
            ) {
                Text(
                    text = if (selectedCount == 0) "None selected" else "$selectedCount selected",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedCount > 0) MedicalPrimary else MedicalOnSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Scrollable Capsule Pill Groups
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            groupedSymptoms.forEach { (category, items) ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = category.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MedicalPrimary,
                        letterSpacing = 0.5.sp
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items.forEach { symptom ->
                            val isSelected = selectedSymptoms.contains(symptom.key)

                            Surface(
                                onClick = { onToggleSymptom(symptom.key, !isSelected) },
                                shape = RoundedCornerShape(50.dp),
                                color = if (isSelected) MedicalPrimaryContainer else MedicalSurface,
                                border = BorderStroke(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) MedicalPrimary else MedicalOutlineVariant
                                ),
                                shadowElevation = if (isSelected) 1.dp else 0.dp
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = MedicalPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Text(
                                        text = symptom.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) MedicalPrimary else MedicalOnSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Bottom Navigation Row
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
                border = BorderStroke(1.dp, MedicalOutlineVariant)
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
                    text = "Next: ABCDE Check",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}
