package com.example.dermalens.data.ai

import com.example.dermalens.BuildConfig
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiChatService @Inject constructor() {

    private var activeChat: Chat? = null
    private var currentModel: GenerativeModel? = null

    fun isApiKeyConfigured(): Boolean {
        return BuildConfig.GEMINI_API_KEY.isNotBlank()
    }

    fun hasActiveSession(): Boolean {
        return activeChat != null
    }

    /**
     * Initializes a fresh Gemini chat session with the full clinical system instruction.
     */
    fun startNewSession(systemInstructionText: String) {
        if (!isApiKeyConfigured()) {
            activeChat = null
            currentModel = null
            return
        }

        val config = generationConfig {
            temperature = 0.4f
            topK = 32
            topP = 0.95f
            maxOutputTokens = 1024
        }

        val model = GenerativeModel(
            modelName = "gemini-3.5-flash-lite",
            apiKey = BuildConfig.GEMINI_API_KEY,
            generationConfig = config,
            systemInstruction = content {
                text(systemInstructionText)
            }
        )

        currentModel = model
        activeChat = model.startChat()
    }

    /**
     * Streams responses from Gemini token-by-token.
     * Maintains conversation history within the active chat session.
     */
    fun sendMessageStream(userMessage: String): Flow<String> {
        val chat = activeChat
            ?: throw IllegalStateException("Gemini chat session is not initialized or API key is missing.")

        return chat.sendMessageStream(userMessage).map { response ->
            response.text ?: ""
        }
    }

    /**
     * Resets the active session when leaving the chat screen.
     */
    fun clearSession() {
        activeChat = null
        currentModel = null
    }
}
