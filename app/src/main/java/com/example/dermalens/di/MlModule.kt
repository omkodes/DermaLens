package com.example.dermalens.di

import android.content.Context
import com.example.dermalens.ml.DiseasePredictor
import com.example.dermalens.ml.SkinClassifierEngine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MlModule {

    @Provides
    @Singleton
    fun provideSkinClassifierEngine(
        @ApplicationContext context: Context
    ): SkinClassifierEngine {
        return SkinClassifierEngine(context)
    }

    @Provides
    @Singleton
    fun provideDiseasePredictor(
        @ApplicationContext context: Context
    ): DiseasePredictor {
        return DiseasePredictor(context)
    }
}
