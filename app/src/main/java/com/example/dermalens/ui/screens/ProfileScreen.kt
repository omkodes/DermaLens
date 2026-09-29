package com.example.dermalens.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dermalens.data.local.entity.UserProfileEntity
import com.example.dermalens.ui.components.WipeConfirmDialog
import com.example.dermalens.ui.theme.*

@Composable
fun ProfileScreen(
    profile: UserProfileEntity?,
    onNavigateToEdit: () -> Unit,
    onLogoutAndWipe: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showWipeDialog by remember { mutableStateOf(false) }

    val name = profile?.name?.ifBlank { "User" } ?: "User"
    val age = profile?.age ?: 30
    val sex = profile?.sex ?: "Male"
    val skinType = profile?.skinType ?: "Fair"
    val familyHistory = profile?.familyHistory ?: false
    val priorCancer = profile?.previousSkinCancer ?: false
    val sunburns = profile?.hadBadSunburns ?: false

    val skinTypeColor = when (skinType) {
        "Very Fair" -> Color(0xFFFFE0BD)
        "Fair" -> Color(0xFFE0AC69)
        "Medium" -> Color(0xFFC68642)
        "Dark" -> Color(0xFF5C3826)
        else -> Color(0xFFE0AC69)
    }

    val skinTypeDesc = when (skinType) {
        "Very Fair" -> "Type I-II: Burns easily, minimally/never tans"
        "Fair" -> "Type III: Sometimes mild burn, gradually tans"
        "Medium" -> "Type IV-V: Rarely burns, tans easily"
        "Dark" -> "Type VI: Never burns, deeply pigmented"
        else -> "Fitzpatrick skin phototype classification"
    }

    if (showWipeDialog) {
        WipeConfirmDialog(
            onConfirmWipe = {
                showWipeDialog = false
                onLogoutAndWipe()
            },
            onDismiss = { showWipeDialog = false }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MedicalBackground)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Screen Header with Edit Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Clinical Profile",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MedicalPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Your baseline demographic and epidemiological risk settings.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MedicalOnSurfaceVariant
                )
            }

            FilledTonalButton(
                onClick = onNavigateToEdit,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MedicalPrimaryContainer,
                    contentColor = MedicalOnPrimaryContainer
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Edit", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 1. User Overview Header Card
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.outlinedCardColors(containerColor = MedicalSurface),
            border = BorderStroke(1.dp, MedicalOutlineVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(MedicalPrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MedicalPrimary,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MedicalOnSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Clinical Baseline: Configured",
                        style = MaterialTheme.typography.bodySmall,
                        color = LowRiskGreen,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Read-only Demographics Card
        Text(
            text = "Demographics",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MedicalOnSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.outlinedCardColors(containerColor = MedicalSurface),
            border = BorderStroke(1.dp, MedicalOutlineVariant)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Age",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MedicalOnSurfaceVariant
                    )
                    Text(
                        text = "$age years",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MedicalPrimary
                    )
                }

                HorizontalDivider(color = MedicalOutlineVariant.copy(alpha = 0.5f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Biological Sex",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MedicalOnSurfaceVariant
                    )
                    Text(
                        text = sex,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MedicalOnSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Read-only Skin Phototype Card
        Text(
            text = "Fitzpatrick Skin Phototype",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MedicalOnSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.outlinedCardColors(containerColor = MedicalSurface),
            border = BorderStroke(1.dp, MedicalOutlineVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(skinTypeColor)
                        .border(1.dp, MedicalOutlineVariant, CircleShape)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = skinType,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MedicalOnSurface
                    )
                    Text(
                        text = skinTypeDesc,
                        style = MaterialTheme.typography.bodySmall,
                        color = MedicalOnSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Read-only Clinical Risk Factors Card
        Text(
            text = "Clinical Risk Factors",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MedicalOnSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.outlinedCardColors(containerColor = MedicalSurface),
            border = BorderStroke(1.dp, MedicalOutlineVariant)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                RiskFactorRow(
                    title = "Family History of Melanoma",
                    isReported = familyHistory,
                    pointsText = "+1 pt"
                )

                HorizontalDivider(color = MedicalOutlineVariant.copy(alpha = 0.5f))

                RiskFactorRow(
                    title = "Personal History of Skin Cancer",
                    isReported = priorCancer,
                    pointsText = "+3 pts"
                )

                HorizontalDivider(color = MedicalOutlineVariant.copy(alpha = 0.5f))

                RiskFactorRow(
                    title = "History of Severe Blistering Sunburns",
                    isReported = sunburns,
                    pointsText = "+1 pt"
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Edit Profile Full Width Button
        Button(
            onClick = onNavigateToEdit,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MedicalPrimary,
                contentColor = MedicalOnPrimary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Edit Clinical Profile", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Danger Zone Container (Title removed as requested, all functionality kept intact)
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.outlinedCardColors(containerColor = HighRiskRedContainer.copy(alpha = 0.4f)),
            border = BorderStroke(1.5.dp, HighRiskRed)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Permanently clear all user records, screening history, and local preferences from this device.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MedicalOnSurface,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { showWipeDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HighRiskRed,
                        contentColor = OnHighRiskRed
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteForever,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logout & Wipe All Data", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun RiskFactorRow(
    title: String,
    isReported: Boolean,
    pointsText: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MedicalOnSurface
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        if (isReported) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = HighRiskRedContainer
            ) {
                Text(
                    text = "Reported ($pointsText)",
                    color = HighRiskRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        } else {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MedicalSurfaceVariant
            ) {
                Text(
                    text = "No",
                    color = MedicalOnSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}
