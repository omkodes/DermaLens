package com.example.dermalens.di

import com.example.dermalens.data.ai.GeminiChatService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AiModule {

    @Provides
    @Singleton
    fun provideGeminiChatService(): GeminiChatService {
        return GeminiChatService()
    }
}
