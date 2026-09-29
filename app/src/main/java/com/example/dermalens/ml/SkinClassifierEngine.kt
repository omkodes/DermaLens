package com.example.dermalens.ml

import android.content.Context
import android.graphics.Bitmap
import ai.onnxruntime.OnnxJavaType
import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import com.example.dermalens.ml.model.SkinDiagnosis
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.exp

@Singleton
class SkinClassifierEngine @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val ortEnv: OrtEnvironment = OrtEnvironment.getEnvironment()
    private var session: OrtSession? = null

    // Default 8-Class ISIC standard
    private var labelMap: Map<Int, Pair<String, String>> = mapOf(
        0 to Pair("AK", "Actinic Keratosis / Solar Keratosis (Pre-cancerous)"),
        1 to Pair("BCC", "Basal Cell Carcinoma (Malignant)"),
        2 to Pair("BKL", "Benign Keratosis-like Lesions"),
        3 to Pair("DF", "Dermatofibroma (Benign)"),
        4 to Pair("MEL", "Melanoma (Malignant Skin Cancer)"),
        5 to Pair("NV", "Melanocytic Nevus (Normal Mole)"),
        6 to Pair("SCC", "Squamous Cell Carcinoma (Malignant)"),
        7 to Pair("VASC", "Vascular Lesion (Benign)")
    )

    val malignantCodes = setOf("MEL", "BCC", "SCC", "AK")

    suspend fun initialize(
        modelName: String = "skin_disease_swin_fp16.onnx",
        labelsFileName: String = "labels.json"
    ) = withContext(Dispatchers.IO) {
        if (session != null) return@withContext
        loadLabelsFromJson(labelsFileName)
        val modelBytes = context.assets.open(modelName).use { it.readBytes() }
        val sessionOptions = OrtSession.SessionOptions().apply {
            val cores = Runtime.getRuntime().availableProcessors()
            setIntraOpNumThreads(cores.coerceIn(2, 4))
            setOptimizationLevel(OrtSession.SessionOptions.OptLevel.ALL_OPT)
        }
        session = ortEnv.createSession(modelBytes, sessionOptions)
    }

    private fun loadLabelsFromJson(fileName: String) {
        try {
            val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            val json = JSONObject(jsonString)
            val id2label = json.getJSONObject("id2label")
            val descriptions = json.optJSONObject("disease_descriptions")

            val mutableMap = mutableMapOf<Int, Pair<String, String>>()
            val keys = id2label.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                val id = key.toInt()
                val code = id2label.getString(key)
                val desc = descriptions?.optString(code, code) ?: code
                mutableMap[id] = Pair(code, desc)
            }
            if (mutableMap.isNotEmpty()) labelMap = mutableMap
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun classify(rawBitmap: Bitmap): Pair<List<SkinDiagnosis>, Long> = withContext(Dispatchers.Default) {
        if (session == null) {
            initialize()
        }
        val sess = session ?: throw IllegalStateException("Model not initialized")
        val startTime = System.currentTimeMillis()

        // 1. Center-square crop & resize to 224x224
        val bitmap = ImagePreprocessor.cropCenterSquareAndResize(rawBitmap, 224)
        val inputName = sess.inputNames.iterator().next()

        // 2. Inference: check FP16 vs FP32
        val isFp16 = modelRequiresFloat16(sess)
        val floatLogits = if (isFp16) {
            val shortBuf = ImagePreprocessor.preprocessBitmapToFloat16Buffer(bitmap, 224, 224)
            val inTensor = OnnxTensor.createTensor(ortEnv, shortBuf, longArrayOf(1, 3, 224, 224), OnnxJavaType.FLOAT16)
            val output = sess.run(mapOf(inputName to inTensor))
            val raw = output[0].value
            inTensor.close()
            output.close()
            extractFloatArray(raw)
        } else {
            val floatBuf = ImagePreprocessor.preprocessBitmapToFloat32Buffer(bitmap, 224, 224)
            val inTensor = OnnxTensor.createTensor(ortEnv, floatBuf, longArrayOf(1, 3, 224, 224))
            val output = sess.run(mapOf(inputName to inTensor))
            val raw = output[0].value
            inTensor.close()
            output.close()
            extractFloatArray(raw)
        }

        val latency = System.currentTimeMillis() - startTime
        val probabilities = softmax(floatLogits)

        val results = probabilities.mapIndexed { index, prob ->
            val (code, name) = labelMap[index] ?: Pair("ID_$index", "Unknown Class $index")
            SkinDiagnosis(
                classId = index,
                code = code,
                diseaseName = name,
                confidence = prob,
                isMalignantRisk = code in malignantCodes
            )
        }.sortedByDescending { it.confidence }

        Pair(results, latency)
    }

    private fun modelRequiresFloat16(session: OrtSession): Boolean {
        val inputName = session.inputNames.iterator().next()
        val info = session.inputInfo[inputName]?.info
        return info?.toString()?.contains("FLOAT16", ignoreCase = true) == true
    }

    private fun extractFloatArray(value: Any): FloatArray {
        return when (value) {
            is Array<*> -> {
                when (val first = value[0]) {
                    is FloatArray -> first
                    is ShortArray -> first.map { ImagePreprocessor.halfToFloat(it) }.toFloatArray()
                    else -> throw IllegalArgumentException("Unexpected tensor format")
                }
            }
            is FloatArray -> value
            else -> throw IllegalArgumentException("Unexpected output shape")
        }
    }

    private fun softmax(logits: FloatArray): FloatArray {
        val maxLogit = logits.maxOrNull() ?: 0f
        val expScores = logits.map { exp(it - maxLogit) }
        val sumExp = expScores.sum()
        return expScores.map { it / sumExp }.toFloatArray()
    }

    fun close() {
        session?.close()
        session = null
    }
}
