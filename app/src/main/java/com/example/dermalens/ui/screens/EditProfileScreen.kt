package com.example.dermalens.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dermalens.data.local.entity.UserProfileEntity
import com.example.dermalens.ui.components.YesNoToggleCard
import com.example.dermalens.ui.theme.*

@Composable
fun EditProfileScreen(
    profile: UserProfileEntity?,
    onSaveProfile: (UserProfileEntity) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Initial values for unsaved changes detection
    val initialName = profile?.name ?: ""
    val initialAge = profile?.age ?: 30
    val initialSex = profile?.sex ?: "Male"
    val initialSkinType = profile?.skinType ?: "Fair"
    val initialFamilyHistory = profile?.familyHistory ?: false
    val initialPriorCancer = profile?.previousSkinCancer ?: false
    val initialSunburns = profile?.hadBadSunburns ?: false

    // Form editing states
    var name by rememberSaveable(profile) { mutableStateOf(initialName) }
    var age by rememberSaveable(profile) { mutableIntStateOf(initialAge) }
    var sex by rememberSaveable(profile) { mutableStateOf(initialSex) }
    var skinType by rememberSaveable(profile) { mutableStateOf(initialSkinType) }
    var familyHistory by rememberSaveable(profile) { mutableStateOf(initialFamilyHistory) }
    var priorCancer by rememberSaveable(profile) { mutableStateOf(initialPriorCancer) }
    var sunburns by rememberSaveable(profile) { mutableStateOf(initialSunburns) }

    var showDiscardDialog by rememberSaveable { mutableStateOf(false) }

    // Check if user has changed any field
    val hasUnsavedChanges = name != initialName ||
            age != initialAge ||
            sex != initialSex ||
            skinType != initialSkinType ||
            familyHistory != initialFamilyHistory ||
            priorCancer != initialPriorCancer ||
            sunburns != initialSunburns

    fun buildUpdatedProfile(): UserProfileEntity {
        return UserProfileEntity(
            id = 1,
            name = name.trim().ifBlank { "User" },
            age = age,
            sex = sex,
            skinType = skinType,
            familyHistory = familyHistory,
            previousSkinCancer = priorCancer,
            hadBadSunburns = sunburns,
            consentGiven = true
        )
    }

    fun saveAndExit() {
        onSaveProfile(buildUpdatedProfile())
        Toast.makeText(context, "Profile changes saved successfully.", Toast.LENGTH_SHORT).show()
        onNavigateBack()
    }

    fun handleBack() {
        if (hasUnsavedChanges) {
            showDiscardDialog = true
        } else {
            onNavigateBack()
        }
    }

    // Intercept hardware and gesture back presses
    BackHandler(enabled = true) {
        handleBack()
    }

    // Warn of Discard Changes Dialog with Save option
    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = {
                Text(
                    text = "Discard Changes?",
                    fontWeight = FontWeight.Bold,
                    color = MedicalPrimary
                )
            },
            text = {
                Text(
                    text = "You have unsaved changes to your clinical profile. Do you want to save them before leaving, or discard them?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MedicalOnSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDiscardDialog = false
                        saveAndExit()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MedicalPrimary,
                        contentColor = MedicalOnPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Save & Exit", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(
                        onClick = {
                            showDiscardDialog = false
                            onNavigateBack()
                        }
                    ) {
                        Text("Discard", color = HighRiskRed, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    TextButton(
                        onClick = { showDiscardDialog = false }
                    ) {
                        Text("Keep Editing", color = MedicalOnSurfaceVariant)
                    }
                }
            },
            containerColor = MedicalSurface,
            shape = RoundedCornerShape(18.dp)
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MedicalBackground)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Screen Top Bar with Back Arrow, Title, and Save Action (Zero extra insets gap)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(
                    onClick = { handleBack() },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MedicalOnSurface
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Edit Profile",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MedicalPrimary
                )
            }

            FilledTonalButton(
                onClick = { saveAndExit() },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MedicalPrimaryContainer,
                    contentColor = MedicalOnPrimaryContainer
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Save", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Update your demographic information and epidemiological risk factors.",
            style = MaterialTheme.typography.bodySmall,
            color = MedicalOnSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 1. Name Input
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MedicalPrimary,
                unfocusedBorderColor = MedicalOutlineVariant,
                focusedLabelColor = MedicalPrimary
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 2. Age Slider
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.outlinedCardColors(containerColor = MedicalSurface),
            border = BorderStroke(1.dp, MedicalOutlineVariant)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Age",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MedicalOnSurface
                    )
                    Text(
                        text = "$age years",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MedicalPrimary
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Slider(
                    value = age.toFloat(),
                    onValueChange = { age = it.toInt() },
                    valueRange = 1f..100f,
                    steps = 98,
                    colors = SliderDefaults.colors(
                        thumbColor = MedicalPrimary,
                        activeTrackColor = MedicalPrimary,
                        inactiveTrackColor = MedicalPrimaryContainer
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 3. Biological Sex
        Text(
            text = "Biological Sex",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MedicalOnSurface
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf("Male", "Female", "Other").forEach { option ->
                val isSelected = sex == option
                OutlinedCard(
                    onClick = { sex = option },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = if (isSelected) MedicalPrimaryContainer else MedicalSurface
                    ),
                    border = BorderStroke(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) MedicalPrimary else MedicalOutlineVariant
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = option,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) MedicalOnPrimaryContainer else MedicalOnSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 4. Skin Phototype
        Text(
            text = "Fitzpatrick Skin Phototype",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MedicalOnSurface
        )

        Spacer(modifier = Modifier.height(10.dp))

        val skinTypes = listOf(
            Triple("Very Fair", "Type I-II: Burns easily, minimally/never tans", Color(0xFFFFE0BD)),
            Triple("Fair", "Type III: Sometimes mild burn, gradually tans", Color(0xFFE0AC69)),
            Triple("Medium", "Type IV-V: Rarely burns, tans easily", Color(0xFFC68642)),
            Triple("Dark", "Type VI: Never burns, deeply pigmented", Color(0xFF5C3826))
        )

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            skinTypes.forEach { (typeKey, description, swatchColor) ->
                val isSelected = skinType == typeKey
                OutlinedCard(
                    onClick = { skinType = typeKey },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = if (isSelected) MedicalPrimaryContainer else MedicalSurface
                    ),
                    border = BorderStroke(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) MedicalPrimary else MedicalOutlineVariant
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
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(swatchColor)
                                .border(1.dp, MedicalOutlineVariant, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = typeKey,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MedicalOnPrimaryContainer else MedicalOnSurface
                            )
                            Text(
                                text = description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MedicalOnSurfaceVariant
                            )
                        }
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = MedicalPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 5. Epidemiological Risk Factors
        Text(
            text = "Clinical Risk Factors",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MedicalOnSurface
        )

        Spacer(modifier = Modifier.height(10.dp))

        YesNoToggleCard(
            title = "Family History of Melanoma",
            description = "First-degree relative (parent, sibling, child) diagnosed with melanoma (+1 pt).",
            isSelected = familyHistory,
            onToggle = { familyHistory = it }
        )

        Spacer(modifier = Modifier.height(12.dp))

        YesNoToggleCard(
            title = "Personal History of Skin Cancer",
            description = "Previous diagnosis of melanoma, basal cell, or squamous cell carcinoma (+3 pts).",
            isSelected = priorCancer,
            onToggle = { priorCancer = it }
        )

        Spacer(modifier = Modifier.height(12.dp))

        YesNoToggleCard(
            title = "History of Severe Blistering Sunburns",
            description = "One or more blistering sunburns during childhood or adolescence (+1 pt).",
            isSelected = sunburns,
            onToggle = { sunburns = it }
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Primary Save Changes Button at the bottom of the form
        Button(
            onClick = { saveAndExit() },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MedicalPrimary,
                contentColor = MedicalOnPrimary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Save Changes",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(36.dp))
    }
}
