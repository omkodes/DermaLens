package com.example.dermalens.data.repository

import com.example.dermalens.data.local.dao.ScreeningHistoryDao
import com.example.dermalens.data.local.entity.ScreeningHistoryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScreeningRepository @Inject constructor(
    private val screeningHistoryDao: ScreeningHistoryDao
) {
    fun getAllHistory(): Flow<List<ScreeningHistoryEntity>> = screeningHistoryDao.getAllHistory()

    fun getLatestScreening(): Flow<ScreeningHistoryEntity?> = screeningHistoryDao.getLatestScreening()

    fun getScreeningCount(): Flow<Int> = screeningHistoryDao.getScreeningCount()

    suspend fun getById(id: Long): ScreeningHistoryEntity? = screeningHistoryDao.getById(id)

    suspend fun insertScreening(screening: ScreeningHistoryEntity): Long {
        return screeningHistoryDao.insertScreening(screening)
    }

    suspend fun deleteAll() {
        screeningHistoryDao.deleteAll()
    }
}
