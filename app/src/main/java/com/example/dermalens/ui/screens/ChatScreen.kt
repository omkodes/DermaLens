package com.example.dermalens.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dermalens.ui.components.ChatInputBar
import com.example.dermalens.ui.components.ChatMessageBubble
import com.example.dermalens.ui.components.ClinicalAiAvatar
import com.example.dermalens.ui.components.RiskLevelBadge
import com.example.dermalens.ui.theme.*
import com.example.dermalens.ui.viewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Show toast for errors
    LaunchedEffect(Unit) {
        viewModel.toastEvent.collect { errorMsg ->
            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
        }
    }

    // Auto-scroll to latest message whenever new messages or streaming tokens arrive
    val lastMessageContentLength = uiState.messages.lastOrNull()?.content?.length ?: 0
    LaunchedEffect(uiState.messages.size, lastMessageContentLength) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.lastIndex)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MedicalBackground)
    ) {
        // TopAppBar styled cleanly like Google Gemini mobile
        TopAppBar(
            windowInsets = WindowInsets(0, 0, 0, 0),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ClinicalAiAvatar(size = 36.dp, showOnlineDot = true)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Clinical Assistant",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 17.sp,
                            color = MedicalOnSurface
                        )
                        if (uiState.topDiseaseName.isNotBlank()) {
                            Text(
                                text = uiState.topDiseaseName,
                                fontSize = 12.sp,
                                color = MedicalOutline,
                                maxLines = 1
                            )
                        }
                    }
                }
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MedicalOnSurface,
                        modifier = Modifier.size(22.dp)
                    )
                }
            },
            actions = {
                if (uiState.riskLevel.isNotBlank()) {
                    RiskLevelBadge(
                        riskLevel = uiState.riskLevel,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MedicalBackground
            )
        )


        // API Key Missing Warning Banner
        if (uiState.isKeyMissing) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MediumRiskOrangeContainer,
                border = BorderStroke(1.dp, MediumRiskOrange.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyOff,
                        contentDescription = "Key Missing",
                        tint = MediumRiskOrange,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Gemini API key is not set. Add GEMINI_API_KEY in local.properties and rebuild.",
                        fontSize = 12.sp,
                        color = MediumRiskOrange,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Message List filling the remaining height
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = uiState.messages,
                key = { it.id }
            ) { msg ->
                ChatMessageBubble(
                    message = msg,
                    onRetry = { viewModel.retryLastMessage() }
                )
            }
        }

        // Docked Bottom Input Bar
        ChatInputBar(
            inputText = inputText,
            onInputChange = { inputText = it },
            onSendClick = {
                val messageToSend = inputText
                inputText = ""
                viewModel.sendMessage(messageToSend)
            },
            isGenerating = uiState.isGenerating,
            isKeyMissing = uiState.isKeyMissing
        )
    }
}
