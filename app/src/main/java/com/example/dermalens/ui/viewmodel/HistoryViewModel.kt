package com.example.dermalens.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dermalens.data.local.entity.ScreeningHistoryEntity
import com.example.dermalens.data.repository.ScreeningRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val screeningRepository: ScreeningRepository
) : ViewModel() {

    val historyList: StateFlow<List<ScreeningHistoryEntity>> =
        screeningRepository.getAllHistory()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val latestScreening: StateFlow<ScreeningHistoryEntity?> =
        screeningRepository.getLatestScreening()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )

    val screeningCount: StateFlow<Int> =
        screeningRepository.getScreeningCount()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = 0
            )

    private val _selectedScan = MutableStateFlow<ScreeningHistoryEntity?>(null)
    val selectedScan: StateFlow<ScreeningHistoryEntity?> = _selectedScan.asStateFlow()

    fun selectScan(scan: ScreeningHistoryEntity?) {
        _selectedScan.value = scan
    }

    fun clearSelectedScan() {
        _selectedScan.value = null
    }
}
