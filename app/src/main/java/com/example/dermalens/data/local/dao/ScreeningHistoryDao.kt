package com.example.dermalens.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dermalens.data.local.entity.ScreeningHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScreeningHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScreening(screening: ScreeningHistoryEntity): Long

    @Query("SELECT * FROM screening_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<ScreeningHistoryEntity>>

    @Query("SELECT * FROM screening_history WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): ScreeningHistoryEntity?

    @Query("SELECT * FROM screening_history ORDER BY timestamp DESC LIMIT 1")
    fun getLatestScreening(): Flow<ScreeningHistoryEntity?>

    @Query("SELECT COUNT(*) FROM screening_history")
    fun getScreeningCount(): Flow<Int>

    @Query("DELETE FROM screening_history")
    suspend fun deleteAll()
}
