package com.example.dermalens.ml.model

data class SkinDiagnosis(
    val classId: Int,
    val code: String,
    val diseaseName: String,
    val confidence: Float,
    val isMalignantRisk: Boolean
)
