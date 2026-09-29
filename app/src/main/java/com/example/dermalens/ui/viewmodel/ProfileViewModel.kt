package com.example.dermalens.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dermalens.data.local.entity.UserProfileEntity
import com.example.dermalens.data.prefs.SharedPrefsManager
import com.example.dermalens.data.repository.ProfileRepository
import com.example.dermalens.data.repository.ScreeningRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val screeningRepository: ScreeningRepository,
    private val sharedPrefsManager: SharedPrefsManager
) : ViewModel() {

    val profile: StateFlow<UserProfileEntity?> = profileRepository.getProfile()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun saveProfile(updatedProfile: UserProfileEntity, onSaved: (() -> Unit)? = null) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                profileRepository.saveProfile(updatedProfile)
            }
            onSaved?.invoke()
        }
    }

    fun logoutAndWipe(onWiped: () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                profileRepository.deleteAll()
                screeningRepository.deleteAll()
                sharedPrefsManager.clear()
            }
            onWiped()
        }
    }
}
