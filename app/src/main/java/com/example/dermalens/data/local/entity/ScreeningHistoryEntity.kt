package com.example.dermalens.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "screening_history")
data class ScreeningHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),

    // Photo: NOT stored — deleted after inference (null in DB)
    val selectedSymptomsJson: String,      // Gson JSON list of active symptom names

    // ABCDE Lesion Checklist
    val lesionHasChanged: Boolean,         // +3 pts
    val lesionBleeding: Boolean,           // +4 pts
    val lesionUnevenShape: Boolean,        // Asymmetry (qualitative)
    val lesionIrregularBorder: Boolean,    // Border (qualitative)
    val lesionMultipleColors: Boolean,     // +2 pts
    val lesionLargerThan6mm: Boolean,      // +2 pts

    // Model 1 Output (Swin Transformer 8-Class)
    val imageTopDiseaseName: String,       // e.g. "Melanoma (Malignant Skin Cancer)"
    val imageTopCode: String,              // e.g. "MEL"
    val imageTopConfidence: Float,         // e.g. 0.84
    val imageMalignantProb: Float,         // Sum of probabilities for MEL, BCC, SCC, AK
    val imageIsMalignantRisk: Boolean,     // True if top code in {MEL, BCC, SCC, AK}
    val imagePredictionsJson: String,      // Gson JSON of all 8 diagnoses [{code, name, conf, isRisk}]
    val imageLatencyMs: Long,

    // Model 2 Output (Symptom Classifier)
    val predictedDisease: String,          // Top-1 condition name
    val diseaseConfidence: Float,
    val topDiseasesJson: String,           // Gson JSON: top-3 [{name, confidence}]

    // Risk Scorer Output
    val riskScore: Float,                  // (abcde pts + profile pts) / 19

    // Fusion Output
    val fusionScore: Float,                // 0.0–1.0
    val riskLevel: String,                 // "LOW" | "MEDIUM" | "HIGH"
    val isUncertain: Boolean,              // abs(imageScore - symptomScore) > 0.40
    val topFactorsJson: String             // Gson JSON: list of top fired factors
)
