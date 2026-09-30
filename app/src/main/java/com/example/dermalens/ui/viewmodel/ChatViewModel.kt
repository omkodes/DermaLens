package com.example.dermalens.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dermalens.data.ai.ClinicalPromptBuilder
import com.example.dermalens.data.ai.GeminiChatService
import com.example.dermalens.data.ai.model.ChatMessage
import com.example.dermalens.data.ai.model.MessageRole
import com.example.dermalens.data.local.entity.ScreeningHistoryEntity
import com.example.dermalens.data.local.entity.UserProfileEntity
import com.example.dermalens.data.repository.ProfileRepository
import com.example.dermalens.data.repository.ScreeningRepository
import android.util.Log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isGenerating: Boolean = false,
    val isKeyMissing: Boolean = false,
    val riskLevel: String = "LOW",
    val topDiseaseName: String = "",
    val screeningId: Long = -1L,
    val error: String? = null
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val screeningRepository: ScreeningRepository,
    private val profileRepository: ProfileRepository,
    private val geminiChatService: GeminiChatService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    private var profile: UserProfileEntity? = null
    private var screening: ScreeningHistoryEntity? = null
    private var lastUserMessage: String = ""

    init {
        val rawId: Any? = savedStateHandle.get("screeningId")
        val screeningId: Long = when (rawId) {
            is Long -> rawId
            is String -> rawId.toLongOrNull() ?: -1L
            is Int -> rawId.toLong()
            else -> -1L
        }

        _uiState.update { it.copy(screeningId = screeningId) }
        loadScreeningAndInitialize(screeningId)
    }

    private fun loadScreeningAndInitialize(screeningId: Long) {
        viewModelScope.launch {
            val keyMissing = !geminiChatService.isApiKeyConfigured()
            _uiState.update { it.copy(isKeyMissing = keyMissing) }

            profile = profileRepository.getProfileOnce()
            screening = if (screeningId > 0) {
                screeningRepository.getById(screeningId)
            } else {
                screeningRepository.getLatestScreening().firstOrNull()
            }

            screening?.let { record ->
                _uiState.update {
                    it.copy(
                        riskLevel = record.riskLevel,
                        topDiseaseName = record.imageTopDiseaseName
                    )
                }

                // Initialize Gemini session with complete clinical persona + patient context
                val systemPrompt = ClinicalPromptBuilder.buildSystemPrompt(profile, record)
                geminiChatService.startNewSession(systemPrompt)

                // Inject initial personalized greeting without LLM latency
                val greeting = ClinicalPromptBuilder.buildGreeting(
                    riskLevel = record.riskLevel,
                    topDiseaseName = record.imageTopDiseaseName
                )
                val initialMsg = ChatMessage(
                    role = MessageRole.ASSISTANT,
                    content = greeting
                )

                _uiState.update { state ->
                    if (state.messages.isEmpty()) {
                        state.copy(messages = listOf(initialMsg))
                    } else {
                        state
                    }
                }
            }
        }
    }

    fun sendMessage(text: String) {
        val trimmed = text.trim()
        if (trimmed.isBlank() || _uiState.value.isGenerating) return

        if (!geminiChatService.isApiKeyConfigured()) {
            Log.w("ChatViewModel", "sendMessage: GEMINI_API_KEY is not configured in BuildConfig")
        }

        lastUserMessage = trimmed
        val userMsg = ChatMessage(role = MessageRole.USER, content = trimmed)
        val assistantPlaceholder = ChatMessage(
            role = MessageRole.ASSISTANT,
            content = "",
            isStreaming = true
        )

        _uiState.update { state ->
            state.copy(
                messages = state.messages + userMsg + assistantPlaceholder,
                isGenerating = true,
                error = null
            )
        }

        viewModelScope.launch {
            try {
                // Ensure session is initialized before streaming
                if (!geminiChatService.hasActiveSession()) {
                    val record = screening ?: if (_uiState.value.screeningId > 0) {
                        screeningRepository.getById(_uiState.value.screeningId)
                    } else {
                        screeningRepository.getLatestScreening().firstOrNull()
                    }
                    if (record != null) {
                        val prompt = ClinicalPromptBuilder.buildSystemPrompt(profile, record)
                        geminiChatService.startNewSession(prompt)
                    }
                }

                geminiChatService.sendMessageStream(trimmed).collect { token ->
                    _uiState.update { state ->
                        val updated = state.messages.toMutableList()
                        val targetIdx = updated.indexOfFirst { it.id == assistantPlaceholder.id }
                        if (targetIdx != -1) {
                            val current = updated[targetIdx]
                            updated[targetIdx] = current.copy(
                                content = current.content + token,
                                isStreaming = true
                            )
                        }
                        state.copy(messages = updated)
                    }
                }

                // Streaming completed successfully
                _uiState.update { state ->
                    val updated = state.messages.toMutableList()
                    val targetIdx = updated.indexOfFirst { it.id == assistantPlaceholder.id }
                    if (targetIdx != -1) {
                        updated[targetIdx] = updated[targetIdx].copy(isStreaming = false)
                    }
                    state.copy(messages = updated, isGenerating = false)
                }
            } catch (e: Exception) {
                val rawMsg = e.localizedMessage ?: e.message ?: "Connection error"
                Log.e("ChatViewModel", "Gemini streaming error: $rawMsg", e)
                _toastEvent.emit(rawMsg)

                _uiState.update { state ->
                    val updated = state.messages.toMutableList()
                    val targetIdx = updated.indexOfFirst { it.id == assistantPlaceholder.id }
                    if (targetIdx != -1) {
                        updated[targetIdx] = updated[targetIdx].copy(
                            content = "Unable to generate a response. Please check your internet connection.",
                            isStreaming = false,
                            isError = true
                        )
                    }
                    state.copy(
                        messages = updated,
                        isGenerating = false,
                        error = rawMsg
                    )
                }
            }
        }
    }

    fun retryLastMessage() {
        if (lastUserMessage.isNotBlank()) {
            _uiState.update { state ->
                val cleaned = state.messages.filterNot { it.isError }
                state.copy(messages = cleaned, error = null)
            }
            sendMessage(lastUserMessage)
        }
    }

    fun restartSession() {
        val currentId = _uiState.value.screeningId
        _uiState.update { it.copy(messages = emptyList(), error = null, isGenerating = false) }
        geminiChatService.clearSession()
        loadScreeningAndInitialize(currentId)
    }

    override fun onCleared() {
        super.onCleared()
        geminiChatService.clearSession()
    }
}
