package com.example.dermalens.ml

import android.content.Context
import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.nio.FloatBuffer
import javax.inject.Inject
import javax.inject.Singleton

data class PredictedDisease(
    val name: String,
    val confidence: Float
)

data class SymptomPredictionResult(
    val topDisease: String,
    val topConfidence: Float,
    val topDiseases: List<PredictedDisease>,
    val symptomScore: Float
)

@Singleton
class DiseasePredictor @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val ortEnv: OrtEnvironment = OrtEnvironment.getEnvironment()
    private var session: OrtSession? = null

    private var symptomList: List<String> = emptyList()
    private var diseaseLabels: List<String> = emptyList()

    val cancerRelatedDiseases = setOf(
        "Melanoma", "Skin Cancer", "Cervical Cancer", "Fungal infection"
    )

    suspend fun initialize(
        modelName: String = "disease_prediction.onnx",
        symptomsFileName: String = "symptoms.json",
        diseaseLabelsFileName: String = "disease_labels.json"
    ) = withContext(Dispatchers.IO) {
        if (session != null) return@withContext
        symptomList = loadJsonArray(symptomsFileName)
        diseaseLabels = loadJsonArray(diseaseLabelsFileName)

        val modelBytes = context.assets.open(modelName).use { it.readBytes() }
        val sessionOptions = OrtSession.SessionOptions().apply {
            val cores = Runtime.getRuntime().availableProcessors()
            setIntraOpNumThreads(cores.coerceIn(1, 2))
        }
        session = ortEnv.createSession(modelBytes, sessionOptions)
    }

    private fun loadJsonArray(fileName: String): List<String> {
        return try {
            val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            val array = JSONArray(jsonString)
            val list = mutableListOf<String>()
            for (i in 0 until array.length()) {
                list.add(array.getString(i))
            }
            list
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun getAllSymptoms(): List<String> = symptomList

    suspend fun predict(activeSymptoms: Set<String>): SymptomPredictionResult = withContext(Dispatchers.Default) {
        if (session == null) {
            initialize()
        }
        val sess = session ?: throw IllegalStateException("Model not initialized")

        val numSymptoms = if (symptomList.isNotEmpty()) symptomList.size else 132
        val buffer = FloatBuffer.allocate(1 * numSymptoms)

        // Normalize active symptom names
        val normalizedActive = activeSymptoms.map { it.trim().lowercase() }.toSet()

        for (symptom in symptomList) {
            val isPresent = normalizedActive.contains(symptom.trim().lowercase())
            buffer.put(if (isPresent) 1.0f else 0.0f)
        }
        buffer.rewind()

        val inputName = sess.inputNames.iterator().next()
        val inTensor = OnnxTensor.createTensor(ortEnv, buffer, longArrayOf(1, numSymptoms.toLong()))

        val output = sess.run(mapOf(inputName to inTensor))
        inTensor.close()

        // Extract probabilities: scikit-learn ONNX models output [label, probabilities]
        val probsVal = if (output.size() > 1) {
            output[1].value
        } else {
            output[0].value
        }

        val probabilities = extractFloatArray(probsVal)
        output.close()

        val rankedDiseases = probabilities.mapIndexed { index, conf ->
            val name = diseaseLabels.getOrElse(index) { "Unknown_$index" }
            PredictedDisease(name = name, confidence = conf)
        }.sortedByDescending { it.confidence }

        val top1 = rankedDiseases.firstOrNull() ?: PredictedDisease("Unknown", 0f)
        val top3 = rankedDiseases.take(3)

        // Identify highest-confidence cancer-related disease probability
        var cancerProb = 0f
        for (disease in rankedDiseases) {
            if (disease.name in cancerRelatedDiseases && disease.confidence > cancerProb) {
                cancerProb = disease.confidence
            }
        }

        val finalSymptomScore = if (cancerProb > 0f) {
            cancerProb
        } else {
            if (top1.name in cancerRelatedDiseases) top1.confidence * 0.5f else 0.0f
        }

        SymptomPredictionResult(
            topDisease = top1.name,
            topConfidence = top1.confidence,
            topDiseases = top3,
            symptomScore = finalSymptomScore
        )
    }

    private fun extractFloatArray(value: Any): FloatArray {
        return when (value) {
            is Array<*> -> {
                when (val first = value[0]) {
                    is FloatArray -> first
                    is DoubleArray -> first.map { it.toFloat() }.toFloatArray()
                    else -> throw IllegalArgumentException("Unexpected tensor format")
                }
            }
            is FloatArray -> value
            is DoubleArray -> value.map { it.toFloat() }.toFloatArray()
            else -> throw IllegalArgumentException("Unexpected output shape: ${value.javaClass}")
        }
    }

    fun close() {
        session?.close()
        session = null
    }
}
