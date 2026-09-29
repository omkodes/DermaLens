package com.example.dermalens.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.dermalens.data.local.dao.ProfileDao
import com.example.dermalens.data.local.dao.ScreeningHistoryDao
import com.example.dermalens.data.local.entity.ScreeningHistoryEntity
import com.example.dermalens.data.local.entity.UserProfileEntity

@Database(
    entities = [UserProfileEntity::class, ScreeningHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class DermaLensDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun screeningHistoryDao(): ScreeningHistoryDao
}
