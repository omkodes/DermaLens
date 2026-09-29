package com.example.dermalens.di

import android.content.Context
import androidx.room.Room
import com.example.dermalens.data.local.DermaLensDatabase
import com.example.dermalens.data.local.dao.ProfileDao
import com.example.dermalens.data.local.dao.ScreeningHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDermaLensDatabase(
        @ApplicationContext context: Context
    ): DermaLensDatabase {
        return Room.databaseBuilder(
            context,
            DermaLensDatabase::class.java,
            "dermalens.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideProfileDao(db: DermaLensDatabase): ProfileDao = db.profileDao()

    @Provides
    @Singleton
    fun provideScreeningHistoryDao(db: DermaLensDatabase): ScreeningHistoryDao = db.screeningHistoryDao()

    @Provides
    @Singleton
    fun provideSharedPrefsManager(
        @ApplicationContext context: Context
    ): com.example.dermalens.data.prefs.SharedPrefsManager {
        return com.example.dermalens.data.prefs.SharedPrefsManager(context)
    }
}
