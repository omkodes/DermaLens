package com.example.dermalens.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dermalens.ui.components.YesNoToggleCard
import com.example.dermalens.ui.theme.*

@Composable
fun OnboardingScreen(
    onComplete: (
        name: String,
        age: Int,
        sex: String,
        skinType: String,
        familyHistory: Boolean,
        priorCancer: Boolean,
        sunburns: Boolean
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableIntStateOf(1) }

    // Onboarding form state
    var name by remember { mutableStateOf("") }
    var age by remember { mutableIntStateOf(30) }
    var sex by remember { mutableStateOf("Male") }
    var skinType by remember { mutableStateOf("Fair") }
    var familyHistory by remember { mutableStateOf(false) }
    var priorCancer by remember { mutableStateOf(false) }
    var sunburns by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MedicalBackground)
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Progress Bar & Step Text
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Step $step of 5",
                style = MaterialTheme.typography.labelLarge,
                color = MedicalPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = when (step) {
                    1 -> "Name"
                    2 -> "Demographics"
                    3 -> "Skin Type"
                    4 -> "Risk Factors"
                    else -> "Confirmation"
                },
                style = MaterialTheme.typography.labelMedium,
                color = MedicalOnSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { step / 5f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = MedicalPrimary,
            trackColor = MedicalPrimaryContainer
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Step Content Area with smooth transition
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            AnimatedContent(
                targetState = step,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "OnboardingStepTransition"
            ) { targetStep ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    when (targetStep) {
                        1 -> Step1Name(name = name, onNameChange = { name = it })
                        2 -> Step2Demographics(
                            age = age,
                            onAgeChange = { age = it },
                            sex = sex,
                            onSexChange = { sex = it }
                        )
                        3 -> Step3SkinType(
                            skinType = skinType,
                            onSkinTypeChange = { skinType = it }
                        )
                        4 -> Step4RiskFactors(
                            familyHistory = familyHistory,
                            onFamilyHistoryChange = { familyHistory = it },
                            priorCancer = priorCancer,
                            onPriorCancerChange = { priorCancer = it },
                            sunburns = sunburns,
                            onSunburnsChange = { sunburns = it }
                        )
                        5 -> Step5Summary(
                            name = name,
                            age = age,
                            sex = sex,
                            skinType = skinType,
                            familyHistory = familyHistory,
                            priorCancer = priorCancer,
                            sunburns = sunburns
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bottom Navigation Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (step > 1) {
                OutlinedButton(
                    onClick = { step-- },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MedicalPrimary
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(MedicalOutlineVariant)
                    )
                ) {
                    Text("Back", fontWeight = FontWeight.SemiBold)
                }
            }

            Button(
                onClick = {
                    if (step < 5) {
                        step++
                    } else {
                        onComplete(
                            name.trim(),
                            age,
                            sex,
                            skinType,
                            familyHistory,
                            priorCancer,
                            sunburns
                        )
                    }
                },
                enabled = step != 1 || name.isNotBlank(),
                modifier = Modifier
                    .weight(if (step > 1) 1f else 2f)
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MedicalPrimary,
                    disabledContainerColor = MedicalOutlineVariant
                )
            ) {
                Text(
                    text = if (step == 5) "Get Started" else "Next",
                    fontWeight = FontWeight.SemiBold,
                    color = MedicalOnPrimary
                )
            }
        }
    }
}

@Composable
private fun Step1Name(
    name: String,
    onNameChange: (String) -> Unit
) {
    Text(
        text = "Welcome to DermaLens",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = MedicalPrimary
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "Please enter your name to personalize your clinical dashboard.",
        style = MaterialTheme.typography.bodyMedium,
        color = MedicalOnSurfaceVariant
    )
    Spacer(modifier = Modifier.height(28.dp))

    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        label = { Text("Your Full or Preferred Name") },
        placeholder = { Text("e.g. Alex Johnson") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MedicalPrimary,
            unfocusedBorderColor = MedicalOutlineVariant,
            focusedLabelColor = MedicalPrimary
        )
    )
}

@Composable
private fun Step2Demographics(
    age: Int,
    onAgeChange: (Int) -> Unit,
    sex: String,
    onSexChange: (String) -> Unit
) {
    Text(
        text = "Demographics",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = MedicalPrimary
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "Age and biological sex are used to weight epidemiological risk models.",
        style = MaterialTheme.typography.bodyMedium,
        color = MedicalOnSurfaceVariant
    )

    Spacer(modifier = Modifier.height(28.dp))

    // Age Slider
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = MedicalSurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(MedicalOutlineVariant)
        )
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
            Spacer(modifier = Modifier.height(12.dp))
            Slider(
                value = age.toFloat(),
                onValueChange = { onAgeChange(it.toInt()) },
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

    // Biological Sex
    Text(
        text = "Biological Sex",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MedicalOnSurface
    )
    Spacer(modifier = Modifier.height(10.dp))

    val sexOptions = listOf("Male", "Female", "Other")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        sexOptions.forEach { option ->
            val isSelected = sex == option
            OutlinedCard(
                onClick = { onSexChange(option) },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = if (isSelected) MedicalPrimaryContainer else MedicalSurface
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(
                        if (isSelected) MedicalPrimary else MedicalOutlineVariant
                    )
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
}

@Composable
private fun Step3SkinType(
    skinType: String,
    onSkinTypeChange: (String) -> Unit
) {
    Text(
        text = "Skin Phototype",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = MedicalPrimary
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "Select the Fitzpatrick phototype that best describes how your skin reacts to sun exposure.",
        style = MaterialTheme.typography.bodyMedium,
        color = MedicalOnSurfaceVariant
    )

    Spacer(modifier = Modifier.height(20.dp))

    val skinTypes = listOf(
        Triple("Very Fair", "Type I-II: Burns easily, minimally/never tans", Color(0xFFFFE0BD)),
        Triple("Fair", "Type III: Sometimes mild burn, gradually tans", Color(0xFFE0AC69)),
        Triple("Medium", "Type IV-V: Rarely burns, tans easily", Color(0xFFC68642)),
        Triple("Dark", "Type VI: Never burns, deeply pigmented", Color(0xFF5C3826))
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        skinTypes.forEach { (typeKey, description, swatchColor) ->
            val isSelected = skinType == typeKey
            OutlinedCard(
                onClick = { onSkinTypeChange(typeKey) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = if (isSelected) MedicalPrimaryContainer else MedicalSurface
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(
                        if (isSelected) MedicalPrimary else MedicalOutlineVariant
                    )
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(swatchColor)
                            .border(1.dp, MedicalOutlineVariant, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
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
}

@Composable
private fun Step4RiskFactors(
    familyHistory: Boolean,
    onFamilyHistoryChange: (Boolean) -> Unit,
    priorCancer: Boolean,
    onPriorCancerChange: (Boolean) -> Unit,
    sunburns: Boolean,
    onSunburnsChange: (Boolean) -> Unit
) {
    Text(
        text = "Clinical Risk Factors",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = MedicalPrimary
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "Check any relevant medical history. These calibrate the baseline risk score (+8 pts max).",
        style = MaterialTheme.typography.bodyMedium,
        color = MedicalOnSurfaceVariant
    )

    Spacer(modifier = Modifier.height(20.dp))

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        YesNoToggleCard(
            title = "Family History of Skin Cancer",
            description = "First-degree relative (parent, sibling, child) diagnosed with melanoma (+3 pts).",
            isSelected = familyHistory,
            onToggle = onFamilyHistoryChange
        )

        YesNoToggleCard(
            title = "Personal Prior Skin Cancer",
            description = "Previous medical diagnosis of melanoma, BCC, SCC, or dysplastic nevi (+4 pts).",
            isSelected = priorCancer,
            onToggle = onPriorCancerChange
        )

        YesNoToggleCard(
            title = "History of Severe Blistering Sunburns",
            description = "One or more blistering sunburns during childhood or adolescence (+1 pt).",
            isSelected = sunburns,
            onToggle = onSunburnsChange
        )
    }
}

@Composable
private fun Step5Summary(
    name: String,
    age: Int,
    sex: String,
    skinType: String,
    familyHistory: Boolean,
    priorCancer: Boolean,
    sunburns: Boolean
) {
    Text(
        text = "Review Baseline Profile",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = MedicalPrimary
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "Confirm your details. You can update these at any time in the Profile tab.",
        style = MaterialTheme.typography.bodyMedium,
        color = MedicalOnSurfaceVariant
    )

    Spacer(modifier = Modifier.height(20.dp))

    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = MedicalSurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(MedicalOutlineVariant)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            SummaryItem(label = "Name", value = name)
            HorizontalDivider(color = MedicalOutlineVariant.copy(alpha = 0.5f))
            SummaryItem(label = "Demographics", value = "$age years old • $sex")
            HorizontalDivider(color = MedicalOutlineVariant.copy(alpha = 0.5f))
            SummaryItem(label = "Skin Phototype", value = skinType)
            HorizontalDivider(color = MedicalOutlineVariant.copy(alpha = 0.5f))

            val factors = buildList {
                if (familyHistory) add("Family history")
                if (priorCancer) add("Prior skin cancer")
                if (sunburns) add("Severe sunburns")
            }
            SummaryItem(
                label = "Reported Risk Factors",
                value = if (factors.isEmpty()) "None reported" else factors.joinToString(", ")
            )
        }
    }
}

@Composable
private fun SummaryItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MedicalOnSurfaceVariant
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = MedicalOnSurface
        )
    }
}
