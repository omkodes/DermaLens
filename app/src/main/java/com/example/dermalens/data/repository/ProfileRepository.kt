package com.example.dermalens.data.repository

import com.example.dermalens.data.local.dao.ProfileDao
import com.example.dermalens.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val profileDao: ProfileDao
) {
    fun getProfile(): Flow<UserProfileEntity?> = profileDao.getProfile()

    suspend fun getProfileOnce(): UserProfileEntity? = profileDao.getProfileOnce()

    suspend fun saveProfile(profile: UserProfileEntity) {
        profileDao.upsertProfile(profile)
    }

    suspend fun deleteAll() {
        profileDao.deleteAll()
    }
}
