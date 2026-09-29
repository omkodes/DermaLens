package com.example.dermalens.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dermalens.data.prefs.SharedPrefsManager
import com.example.dermalens.data.repository.ProfileRepository
import com.example.dermalens.ml.DiseasePredictor
import com.example.dermalens.ml.SkinClassifierEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

sealed interface NavigationTarget {
    object Consent : NavigationTarget
    object Onboarding : NavigationTarget
    object Home : NavigationTarget
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val sharedPrefsManager: SharedPrefsManager,
    private val skinClassifierEngine: SkinClassifierEngine,
    private val diseasePredictor: DiseasePredictor
) : ViewModel() {

    private val _navigationTarget = MutableStateFlow<NavigationTarget?>(null)
    val navigationTarget: StateFlow<NavigationTarget?> = _navigationTarget.asStateFlow()

    init {
        startColdStartInitialization()
    }

    private fun startColdStartInitialization() {
        viewModelScope.launch {
            // Background ML Model Preload on IO dispatcher
            withContext(Dispatchers.IO) {
                try {
                    skinClassifierEngine.initialize()
                    diseasePredictor.initialize()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // Clean splash brand delay
            delay(1000)

            // Check Consent & Profile existence
            val isConsentGiven = sharedPrefsManager.isConsentGiven
            if (!isConsentGiven) {
                _navigationTarget.value = NavigationTarget.Consent
                return@launch
            }

            val existingProfile = withContext(Dispatchers.IO) {
                profileRepository.getProfileOnce()
            }

            if (existingProfile == null) {
                _navigationTarget.value = NavigationTarget.Onboarding
            } else {
                _navigationTarget.value = NavigationTarget.Home
            }
        }
    }
}
