package com.example.dermalens.di

import com.example.dermalens.data.local.dao.ProfileDao
import com.example.dermalens.data.local.dao.ScreeningHistoryDao
import com.example.dermalens.data.repository.ProfileRepository
import com.example.dermalens.data.repository.ScreeningRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideProfileRepository(profileDao: ProfileDao): ProfileRepository {
        return ProfileRepository(profileDao)
    }

    @Provides
    @Singleton
    fun provideScreeningRepository(screeningHistoryDao: ScreeningHistoryDao): ScreeningRepository {
        return ScreeningRepository(screeningHistoryDao)
    }
}
