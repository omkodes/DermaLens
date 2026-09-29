package com.example.dermalens.ml

import com.example.dermalens.ml.model.SkinDiagnosis
import kotlin.math.abs

object MultimodalFusionEngine {
    private const val W_IMAGE   = 0.40f
    private const val W_SYMPTOM = 0.35f
    private const val W_RISK    = 0.25f

    private val INVASIVE_MALIGNANT_CODES = setOf("MEL", "BCC", "SCC")

    val CANCER_RELATED_DISEASES = setOf(
        "Melanoma", "Skin Cancer", "Cervical Cancer", "Fungal infection"
    )

    data class FusionResult(
        val score: Float,
        val riskLevel: RiskLevel,
        val isUncertain: Boolean,
        val clinicalAlert: Boolean,
        val alertMessage: String?
    )

    enum class RiskLevel {
        LOW,
        MEDIUM,
        HIGH
    }

    /**
     * Fuses image score, symptom prediction score, and ABCDE/profile risk score
     * with Clinical Safety Overrides.
     *
     * @param topDiagnosis Top-1 classified ISIC skin lesion diagnosis
     * @param imageScore Sum of probabilities for MEL, BCC, SCC, AK
     * @param symptomScore Probability of most cancer-related condition from symptom model
     * @param riskScore (ABCDE points + Profile points) / 19
     */
    fun fuse(
        topDiagnosis: SkinDiagnosis,
        imageScore: Float,
        symptomScore: Float,
        riskScore: Float
    ): FusionResult {
        val weightedScore = (W_IMAGE * imageScore) + (W_SYMPTOM * symptomScore) + (W_RISK * riskScore)

        var level = when {
            weightedScore < 0.35f -> RiskLevel.LOW
            weightedScore < 0.65f -> RiskLevel.MEDIUM
            else                  -> RiskLevel.HIGH
        }

        var alertMessage: String? = null
        var hasAlert = false

        // CLINICAL SAFETY OVERRIDE 1: Invasive malignancy (MEL, BCC, SCC) >= 50% forces HIGH
        if (topDiagnosis.code in INVASIVE_MALIGNANT_CODES && topDiagnosis.confidence >= 0.50f) {
            level = RiskLevel.HIGH
            hasAlert = true
            val pct = (topDiagnosis.confidence * 100).toInt()
            alertMessage = "High Risk Alert: Top finding is ${topDiagnosis.diseaseName} ($pct%). Immediate dermatologist consultation recommended."
        }
        // CLINICAL SAFETY OVERRIDE 2: Pre-cancerous (AK) >= 50% guarantees at least MEDIUM
        else if (topDiagnosis.code == "AK" && topDiagnosis.confidence >= 0.50f) {
            if (level == RiskLevel.LOW) {
                level = RiskLevel.MEDIUM
            }
            hasAlert = true
            val pct = (topDiagnosis.confidence * 100).toInt()
            alertMessage = "Pre-Cancerous Lesion Alert: Features suggestive of Actinic Keratosis ($pct%). Clinical evaluation recommended."
        }

        val uncertain = abs(imageScore - symptomScore) > 0.40f

        return FusionResult(
            score = weightedScore.coerceIn(0f, 1f),
            riskLevel = level,
            isUncertain = uncertain,
            clinicalAlert = hasAlert,
            alertMessage = alertMessage
        )
    }
}
