package com.example.dermalens.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dermalens.ui.theme.*

/**
 * Modern floating pill input bar inspired by the Google Gemini mobile app.
 * Features:
 * - Subtle warning disclaimer directly above the floating pill ("DermaLens is AI and does not replace doctor evaluation.")
 * - Floating rounded capsule with soft neutral background
 * - 'Ask DermaLens...' text input
 * - Dynamic send / AI action button
 */
@Composable
fun ChatInputBar(
    inputText: String,
    onInputChange: (String) -> Unit,
    onSendClick: () -> Unit,
    isGenerating: Boolean,
    isKeyMissing: Boolean = false,
    modifier: Modifier = Modifier
) {
    val canSend = inputText.isNotBlank() && !isGenerating && !isKeyMissing

    Column(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .imePadding()
            .background(MedicalBackground),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Warning text positioned directly above input capsule (Gemini style)
        Text(
            text = "DermaLens is AI and does not replace doctor evaluation.",
            fontSize = 11.5.sp,
            color = MedicalOutline,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 6.dp)
        )

        // Floating Gemini-style Input Pill
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 10.dp),
            shape = RoundedCornerShape(32.dp),
            color = Color(0xFFF1F5F7), // Soft neutral pill background
            tonalElevation = 0.dp,
            shadowElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 18.dp, end = 6.dp, top = 4.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Center Input Text Field
                BasicTextField(
                    value = inputText,
                    onValueChange = onInputChange,
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 10.dp),
                    textStyle = TextStyle(
                        fontSize = 15.sp,
                        color = MedicalOnSurface,
                        lineHeight = 20.sp
                    ),
                    maxLines = 4,
                    enabled = !isGenerating && !isKeyMissing,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (canSend) onSendClick()
                        }
                    ),
                    decorationBox = { innerTextField ->
                        if (inputText.isEmpty()) {
                            Text(
                                text = if (isKeyMissing) "Chat unavailable (API Key missing)" else "Ask DermaLens",
                                color = MedicalOutline,
                                fontSize = 15.sp,
                                maxLines = 1
                            )
                        }
                        innerTextField()
                    }
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Action / Send Button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .then(
                            if (canSend) {
                                Modifier
                                    .shadow(2.dp, CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(MedicalPrimary, MedicalSecondary)
                                        )
                                    )
                            } else {
                                Modifier.background(Color(0xFFD3E3FD).copy(alpha = 0.55f))
                            }
                        )
                        .clickable(
                            enabled = canSend && !isGenerating,
                            onClick = onSendClick
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MedicalPrimary
                        )
                    } else if (canSend) {
                        Icon(
                            imageVector = Icons.Filled.ArrowUpward,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = "AI Action",
                            tint = MedicalPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
