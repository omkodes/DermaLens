package com.example.dermalens.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dermalens.data.local.entity.UserProfileEntity
import com.example.dermalens.data.prefs.SharedPrefsManager
import com.example.dermalens.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val sharedPrefsManager: SharedPrefsManager
) : ViewModel() {

    private val _step = MutableStateFlow(1)
    val step: StateFlow<Int> = _step.asStateFlow()

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _age = MutableStateFlow(30)
    val age: StateFlow<Int> = _age.asStateFlow()

    private val _sex = MutableStateFlow("Male")
    val sex: StateFlow<String> = _sex.asStateFlow()

    private val _skinType = MutableStateFlow("Fair")
    val skinType: StateFlow<String> = _skinType.asStateFlow()

    private val _familyHistory = MutableStateFlow(false)
    val familyHistory: StateFlow<Boolean> = _familyHistory.asStateFlow()

    private val _priorCancer = MutableStateFlow(false)
    val priorCancer: StateFlow<Boolean> = _priorCancer.asStateFlow()

    private val _sunburns = MutableStateFlow(false)
    val sunburns: StateFlow<Boolean> = _sunburns.asStateFlow()

    private val _nameError = MutableStateFlow<String?>(null)
    val nameError: StateFlow<String?> = _nameError.asStateFlow()

    fun updateName(value: String) {
        _name.value = value
        if (value.isNotBlank()) _nameError.value = null
    }

    fun updateAge(value: Int) {
        _age.value = value
    }

    fun updateSex(value: String) {
        _sex.value = value
    }

    fun updateSkinType(value: String) {
        _skinType.value = value
    }

    fun updateFamilyHistory(value: Boolean) {
        _familyHistory.value = value
    }

    fun updatePriorCancer(value: Boolean) {
        _priorCancer.value = value
    }

    fun updateSunburns(value: Boolean) {
        _sunburns.value = value
    }

    fun goToNextStep(): Boolean {
        if (_step.value == 1 && _name.value.trim().isBlank()) {
            _nameError.value = "Please enter your name to continue."
            return false
        }
        if (_step.value < 5) {
            _step.value += 1
            return true
        }
        return false
    }

    fun goToPreviousStep(): Boolean {
        if (_step.value > 1) {
            _step.value -= 1
            return true
        }
        return false
    }

    fun completeOnboarding(onSuccess: () -> Unit) {
        val finalName = _name.value.trim().ifBlank { "User" }
        viewModelScope.launch {
            val entity = UserProfileEntity(
                id = 1,
                name = finalName,
                age = _age.value,
                sex = _sex.value,
                skinType = _skinType.value,
                familyHistory = _familyHistory.value,
                previousSkinCancer = _priorCancer.value,
                hadBadSunburns = _sunburns.value,
                consentGiven = true
            )
            withContext(Dispatchers.IO) {
                profileRepository.saveProfile(entity)
                sharedPrefsManager.isConsentGiven = true
            }
            onSuccess()
        }
    }
}
