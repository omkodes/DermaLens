# Proguard rules for DermaLens

# Keep ONNX Runtime JNI bindings and classes
-keep class ai.onnxruntime.** { *; }

# Keep data models used by Gson serialization
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keep class com.example.dermalens.data.local.entity.** { *; }
-keep class com.example.dermalens.ml.model.** { *; }
