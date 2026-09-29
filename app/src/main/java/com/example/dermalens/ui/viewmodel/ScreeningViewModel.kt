package com.example.dermalens.ui.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dermalens.data.local.entity.ScreeningHistoryEntity
import com.example.dermalens.data.local.entity.UserProfileEntity
import com.example.dermalens.data.repository.ProfileRepository
import com.example.dermalens.data.repository.ScreeningRepository
import com.example.dermalens.ml.DiseasePredictor
import com.example.dermalens.ml.MultimodalFusionEngine
import com.example.dermalens.ml.RiskScorer
import com.example.dermalens.ml.SkinClassifierEngine
import com.example.dermalens.ml.TopFactorsGenerator
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ScreeningViewModel @Inject constructor(
    private val skinClassifierEngine: SkinClassifierEngine,
    private val diseasePredictor: DiseasePredictor,
    private val screeningRepository: ScreeningRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _currentStep = MutableStateFlow(1)
    val currentStep: StateFlow<Int> = _currentStep.asStateFlow()

    private val _capturedPhoto = MutableStateFlow<Bitmap?>(null)
    val capturedPhoto: StateFlow<Bitmap?> = _capturedPhoto.asStateFlow()

    private val _selectedSymptoms = MutableStateFlow<Set<String>>(emptySet())
    val selectedSymptoms: StateFlow<Set<String>> = _selectedSymptoms.asStateFlow()

    // Step 3 ABCDE toggles
    private val _hasChanged = MutableStateFlow(false)
    val hasChanged: StateFlow<Boolean> = _hasChanged.asStateFlow()

    private val _bleeding = MutableStateFlow(false)
    val bleeding: StateFlow<Boolean> = _bleeding.asStateFlow()

    private val _asymmetry = MutableStateFlow(false)
    val asymmetry: StateFlow<Boolean> = _asymmetry.asStateFlow()

    private val _border = MutableStateFlow(false)
    val border: StateFlow<Boolean> = _border.asStateFlow()

    private val _multipleColors = MutableStateFlow(false)
    val multipleColors: StateFlow<Boolean> = _multipleColors.asStateFlow()

    private val _largerThan6mm = MutableStateFlow(false)
    val largerThan6mm: StateFlow<Boolean> = _largerThan6mm.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _analysisResult = MutableStateFlow<ScreeningHistoryEntity?>(null)
    val analysisResult: StateFlow<ScreeningHistoryEntity?> = _analysisResult.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun setStep(step: Int) {
        _currentStep.value = step.coerceIn(1, 6)
    }

    fun nextStep() {
        if (_currentStep.value < 6) {
            _currentStep.value += 1
        }
    }

    fun previousStep() {
        if (_currentStep.value > 1) {
            _currentStep.value -= 1
        }
    }

    fun setCapturedPhoto(bitmap: Bitmap?) {
        _capturedPhoto.value = bitmap
    }

    fun toggleSymptom(symptom: String) {
        val current = _selectedSymptoms.value.toMutableSet()
        if (current.contains(symptom)) {
            current.remove(symptom)
        } else {
            current.add(symptom)
        }
        _selectedSymptoms.value = current
    }

    fun setSelectedSymptoms(symptoms: Set<String>) {
        _selectedSymptoms.value = symptoms
    }

    fun setHasChanged(value: Boolean) { _hasChanged.value = value }
    fun setBleeding(value: Boolean) { _bleeding.value = value }
    fun setAsymmetry(value: Boolean) { _asymmetry.value = value }
    fun setBorder(value: Boolean) { _border.value = value }
    fun setMultipleColors(value: Boolean) { _multipleColors.value = value }
    fun setLargerThan6mm(value: Boolean) { _largerThan6mm.value = value }

    fun analyzeImage(
        bitmap: Bitmap,
        onComplete: (ScreeningHistoryEntity) -> Unit = {}
    ) {
        viewModelScope.launch {
            _currentStep.value = 5
            _isAnalyzing.value = true
            _errorMessage.value = null
            try {
                // 1. Swin image classification
                val (diagnoses, latency) = withContext(Dispatchers.Default) {
                    skinClassifierEngine.classify(bitmap)
                }
                val topDiag = diagnoses.first()
                val malignantSum = diagnoses
                    .filter { it.isMalignantRisk }
                    .sumOf { it.confidence.toDouble() }
                    .toFloat()

                // 2. Symptom Model 2 (Naive Bayes)
                val symptomResult = withContext(Dispatchers.Default) {
                    diseasePredictor.predict(_selectedSymptoms.value)
                }

                // 3. Risk Scorer (ABCDE + Profile)
                val profile = withContext(Dispatchers.IO) {
                    profileRepository.getProfileOnce()
                } ?: UserProfileEntity(
                    id = 1,
                    name = "User",
                    age = 30,
                    sex = "Male",
                    skinType = "Fair",
                    familyHistory = false,
                    previousSkinCancer = false,
                    hadBadSunburns = false,
                    consentGiven = true
                )

                val riskScore = RiskScorer.calculate(
                    familyHistory = profile.familyHistory,
                    previousSkinCancer = profile.previousSkinCancer,
                    hadBadSunburns = profile.hadBadSunburns,
                    lesionHasChanged = _hasChanged.value,
                    lesionBleeding = _bleeding.value,
                    lesionMultipleColors = _multipleColors.value,
                    lesionLargerThan6mm = _largerThan6mm.value
                )

                // 4. Multimodal Fusion Engine with Safety Overrides
                val fusion = MultimodalFusionEngine.fuse(
                    topDiagnosis = topDiag,
                    imageScore = malignantSum,
                    symptomScore = symptomResult.symptomScore,
                    riskScore = riskScore
                )

                // 5. Top Contributing Factors
                val topFactors = TopFactorsGenerator.generate(
                    topDiagnosis = topDiag,
                    imageMalignantProb = malignantSum,
                    lesionBleeding = _bleeding.value,
                    lesionHasChanged = _hasChanged.value,
                    diseaseConfidence = symptomResult.topConfidence,
                    predictedDisease = symptomResult.topDisease,
                    familyHistory = profile.familyHistory,
                    previousSkinCancer = profile.previousSkinCancer,
                    lesionLargerThan6mm = _largerThan6mm.value,
                    lesionMultipleColors = _multipleColors.value
                )

                val entity = ScreeningHistoryEntity(
                    timestamp = System.currentTimeMillis(),
                    selectedSymptomsJson = Gson().toJson(_selectedSymptoms.value.toList()),
                    lesionHasChanged = _hasChanged.value,
                    lesionBleeding = _bleeding.value,
                    lesionUnevenShape = _asymmetry.value,
                    lesionIrregularBorder = _border.value,
                    lesionMultipleColors = _multipleColors.value,
                    lesionLargerThan6mm = _largerThan6mm.value,
                    imageTopDiseaseName = topDiag.diseaseName,
                    imageTopCode = topDiag.code,
                    imageTopConfidence = topDiag.confidence,
                    imageMalignantProb = malignantSum,
                    imageIsMalignantRisk = topDiag.isMalignantRisk,
                    imagePredictionsJson = Gson().toJson(diagnoses),
                    imageLatencyMs = latency,
                    predictedDisease = symptomResult.topDisease,
                    diseaseConfidence = symptomResult.topConfidence,
                    topDiseasesJson = Gson().toJson(symptomResult.topDiseases),
                    riskScore = riskScore,
                    fusionScore = fusion.score,
                    riskLevel = fusion.riskLevel.name,
                    isUncertain = fusion.isUncertain,
                    topFactorsJson = Gson().toJson(topFactors)
                )

                // 6. Save to Room database
                withContext(Dispatchers.IO) {
                    screeningRepository.insertScreening(entity)
                }

                // 7. Guaranteed photo cleanup: delete from memory immediately
                _capturedPhoto.value = null

                _analysisResult.value = entity
                _isAnalyzing.value = false
                _currentStep.value = 6
                onComplete(entity)
            } catch (e: Exception) {
                e.printStackTrace()
                _isAnalyzing.value = false
                _errorMessage.value = e.message ?: "An unexpected inference error occurred."
            }
        }
    }

    fun resetWizard() {
        _currentStep.value = 1
        _capturedPhoto.value = null
        _selectedSymptoms.value = emptySet()
        _hasChanged.value = false
        _bleeding.value = false
        _asymmetry.value = false
        _border.value = false
        _multipleColors.value = false
        _largerThan6mm.value = false
        _analysisResult.value = null
        _isAnalyzing.value = false
        _errorMessage.value = null
    }
}
