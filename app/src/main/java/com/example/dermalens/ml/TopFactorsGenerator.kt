package com.example.dermalens.ml

import com.example.dermalens.data.local.entity.ScreeningHistoryEntity
import com.example.dermalens.data.local.entity.UserProfileEntity
import com.example.dermalens.ml.model.SkinDiagnosis

object TopFactorsGenerator {

    fun generate(
        topDiagnosis: SkinDiagnosis,
        imageMalignantProb: Float,
        lesionBleeding: Boolean,
        lesionHasChanged: Boolean,
        diseaseConfidence: Float,
        predictedDisease: String,
        familyHistory: Boolean,
        previousSkinCancer: Boolean,
        lesionLargerThan6mm: Boolean,
        lesionMultipleColors: Boolean
    ): List<String> {
        val candidates = mutableListOf<String>()

        if (topDiagnosis.isMalignantRisk && topDiagnosis.confidence >= 0.40f) {
            val pct = (topDiagnosis.confidence * 100).toInt()
            candidates.add("Top finding: ${topDiagnosis.diseaseName} ($pct%)")
        }
        if (imageMalignantProb >= 0.50f) {
            val pct = (imageMalignantProb * 100).toInt()
            candidates.add("Aggregate malignant probability across lesion classes: $pct%")
        }
        if (lesionBleeding) {
            candidates.add("Lesion is bleeding or failing to heal")
        }
        if (lesionHasChanged) {
            candidates.add("Lesion has recently changed in size, shape, or color")
        }
        if (diseaseConfidence >= 0.70f && predictedDisease in MultimodalFusionEngine.CANCER_RELATED_DISEASES) {
            val pct = (diseaseConfidence * 100).toInt()
            candidates.add("High $predictedDisease symptom correlation: $pct%")
        }
        if (familyHistory) {
            candidates.add("Family history of melanoma/skin cancer")
        }
        if (previousSkinCancer) {
            candidates.add("Personal history of prior skin cancer")
        }
        if (lesionLargerThan6mm) {
            candidates.add("Lesion diameter exceeds 6mm")
        }
        if (lesionMultipleColors) {
            candidates.add("Lesion exhibits multiple or uneven color shades")
        }

        return candidates.take(3)
    }

    fun generate(
        topDiagnosis: SkinDiagnosis,
        imageMalignantProb: Float,
        entity: ScreeningHistoryEntity,
        profile: UserProfileEntity
    ): List<String> {
        return generate(
            topDiagnosis = topDiagnosis,
            imageMalignantProb = imageMalignantProb,
            lesionBleeding = entity.lesionBleeding,
            lesionHasChanged = entity.lesionHasChanged,
            diseaseConfidence = entity.diseaseConfidence,
            predictedDisease = entity.predictedDisease,
            familyHistory = profile.familyHistory,
            previousSkinCancer = profile.previousSkinCancer,
            lesionLargerThan6mm = entity.lesionLargerThan6mm,
            lesionMultipleColors = entity.lesionMultipleColors
        )
    }
}
