# DermaLens 🔬🩺

**DermaLens** is an edge-computing clinical decision support system for dermatological lesion triage and skin health monitoring. Running entirely on-device on Android hardware, DermaLens combines hierarchical self-attention vision transformers, tabular symptom classifiers, and clinical anamnesis scoring to stratify cutaneous risk into **Low**, **Medium**, and **High** categories with deterministic safety overrides.

An integrated, context-aware Clinical AI Assistant powered by Google's **Gemini 3.5 Flash-Lite** provides patient education and pre-consultation inquiry preparation without compromising private biometric image data.

For detailed Android software architecture, mobile performance engineering, hardware benchmarks, and AppD ablation experiments, read the [**Technical Report**](TECHNICAL_REPORT.md).

---

## 🌟 Key Features

- **📱 Fully On-Device Edge Inference**: All deep learning vision models and symptom classifiers execute locally on device via ONNX Runtime (FP16). No patient skin photos are uploaded to the cloud.
- **👁️ Swin-T 8-Class Dermoscopic Vision Transformer**: Classifies dermoscopic images across 8 diagnostic categories defined by the International Skin Imaging Collaboration (ISIC), using weights fine-tuned from [HotJellyBean/skin-disease-classifier](https://huggingface.co/HotJellyBean/skin-disease-classifier).
- **🩺 Multimodal Late-Fusion Decision Engine**: Combines image vision logits (40%), patient-reported symptoms (35%), and an epidemiological ABCDE risk checklist (25%).
- **🛡️ Deterministic Clinical Safety Overrides**: Invasive malignancies (Melanoma, Basal Cell Carcinoma, Squamous Cell Carcinoma) $\ge 50\%$ trigger automatic HIGH-risk triage, overriding lower symptom scores.
- **⚠️ Divergence & Uncertainty Flagging**: Detects and highlights conflicts where physical symptoms diverge significantly from visual predictions ($|S_{\text{img}} - S_{\text{sym}}| > 0.40$).
- **💬 Contextual Clinical AI Assistant**: Powered by **Gemini 3.5 Flash-Lite**, translates complex dermatological metrics into plain-language patient education and actionable questions to ask a board-certified dermatologist.
- **⚡ Zero Cold-Start Latency**: Asynchronous background tensor loading ensures immediate splash screen transitions without UI freeze.
- **🔒 Privacy-by-Design & Local Storage**: Uses an encrypted on-device SQLite database via Android Room. Chat sessions are strictly ephemeral and are not stored permanently.

---

## 🧬 Diagnostic Taxonomy (ISIC 8 Classes)

DermaLens is trained and calibrated to identify 8 distinct lesion classifications:

| Code | Diagnosis | Clinical Significance | Action Tier |
| :--- | :--- | :--- | :--- |
| **MEL** | **Melanoma** | High-grade malignancy | Immediate Biopsy / Urgent Referral |
| **BCC** | **Basal Cell Carcinoma** | Locally invasive malignancy | Timely Dermatological Excision |
| **SCC** | **Squamous Cell Carcinoma** | Invasive malignancy | Urgent Clinical Evaluation |
| **AK** | **Actinic Keratosis** | Pre-cancerous / In-situ neoplasm | Preventive Dermatological Review |
| **NV** | **Melanocytic Nevus** | Benign mole | Baseline Routine Monitoring |
| **BKL** | **Benign Keratosis** | Non-malignant lesion (Seborrheic) | Reassurance / Observation |
| **DF** | **Dermatofibroma** | Benign fibrous nodule | Non-urgent Observation |
| **VASC** | **Vascular Lesion** | Hemangioma / Pyogenic granuloma | Benign Confirmation |

---

## 📐 Multimodal Decision Pipeline

```
 [ Dermoscopic Photo ]           [ Reported Symptoms ]           [ ABCDE & History ]
          │                               │                              │
          ▼                               ▼                              ▼
  Center-Square Crop             132-Feature Vector             19-Point Anamnesis
(224x224 RGB Float16)               Binary Flags                   Domain Rules
          │                               │                              │
          ▼                               ▼                              ▼
   Swin-T Transformer             GBDT Decision Tree            Risk Normalization
    ONNX FP16 Engine               ONNX Graph Engine             Weighted Scoring
          │                               │                              │
     S_img (40%)                     S_sym (35%)                    S_risk (25%)
          └───────────────────────────────┬──────────────────────────────┘
                                          ▼
                         Multimodal Late-Fusion Engine
                                          │
                         ┌────────────────┴────────────────┐
                         ▼                                 ▼
             Clinical Safety Overrides          Uncertainty Flagging
             MEL / BCC / SCC >= 50%             |S_img - S_sym| > 0.40
                         └────────────────┬────────────────┘
                                          ▼
                             Risk Stratification Tier
                           [ LOW │ MEDIUM │ HIGH RISK ]
                                          │
                                          ▼
                            Gemini 3.5 Flash-Lite Assistant
                           (Contextual Clinical Counseling)
```

---

## 🛠️ Tech Stack & Architecture

- **Language**: [Kotlin 2.0.21](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material 3 Design
- **Architecture**: MVVM + Clean Architecture with Coroutines & StateFlow
- **Dependency Injection**: [Hilt 2.55](https://dagger.dev/hilt/)
- **Local Persistence**: [Room Database 2.6.1](https://developer.android.com/training/data-storage/room) with KSP
- **Machine Learning**: [ONNX Runtime Android 1.20.0](https://onnxruntime.ai/) (ARM64 FP16 SIMD acceleration)
- **Camera Capture**: [AndroidX CameraX 1.4.1](https://developer.android.com/training/camerax) with EXIF orientation correction
- **Large Language Model**: [Google Generative AI Client SDK 0.9.0](https://ai.google.dev/) (`gemini-3.5-flash-lite`)
- **Rich Text Rendering**: Compose Markdown 0.5.1

---

## 📋 Prerequisites

Before setting up the project, ensure you have:

1. **Android Studio**: Android Studio Ladybug (2024.2.1+) or newer.
2. **JDK**: Java Development Kit version **17** (configured in Gradle toolchain).
3. **Android SDK**:
   - `compileSdk = 35`
   - `targetSdk = 35`
   - `minSdk = 26` (Android 8.0 Oreo or higher)
4. **NDK / ABI Support**: Device or emulator supporting `arm64-v8a` or `x86_64`.
5. **Google AI Studio API Key**: A valid Gemini API key for the clinical assistant. Get one free at [Google AI Studio](https://aistudio.google.com/).

---

## 🚀 Setup & Installation

### 1. Clone the Repository

```bash
git clone https://github.com/omkodes/DermaLens.git
cd DermaLens
```

### 2. Configure Gemini API Key

DermaLens securely loads your Google Gemini API key during compilation using `local.properties` (which is git-ignored):

1. Open or create a `local.properties` file in the root project directory:
   ```properties
   ## local.properties
   sdk.dir=/path/to/your/android-sdk
   GEMINI_API_KEY=your_gemini_api_key_here
   ```
2. Replace `your_gemini_api_key_here` with your actual key from [Google AI Studio](https://aistudio.google.com/).
3. Sync Gradle in Android Studio. The key is automatically injected into `BuildConfig.GEMINI_API_KEY`.

> [!NOTE]
> If no API key is provided, the on-device ML triage (Swin-T and symptom classification) continues to work completely offline; only the online Clinical AI chat assistant will be disabled.

### 3. Verify Model Assets

Ensure the following pre-trained models are present in `app/src/main/assets/`:
- `skin_disease_swin_fp16.onnx` (~176.5 MB — Swin Transformer vision model exported to ONNX FP16 from [HotJellyBean/skin-disease-classifier](https://huggingface.co/HotJellyBean/skin-disease-classifier))
- `disease_prediction.onnx` (~23.4 KB — Tabular symptom model)
- `labels.json` & `symptoms.json` (Taxonomy mapping dictionaries)

### 4. Build and Run

#### Using Android Studio:
1. Open the project folder in Android Studio.
2. Allow Gradle to download dependencies and sync.
3. Select an Android device or emulator running API 26+.
4. Click **Run** (`Shift + F10`) or **Debug** (`Shift + F9`).

#### Using Command Line (Terminal):
```bash
# Debug build (generates APK in app/build/outputs/apk/debug/)
./gradlew assembleDebug

# Install directly to a connected device
./gradlew installDebug
```

On Windows Command Prompt / PowerShell:
```powershell
gradlew.bat assembleDebug
gradlew.bat installDebug
```

---

## 📱 App Walkthrough & User Flow

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│  Home / Scan │ ──> │ Step 1: Photo│ ──> │Step 2: Sympt.│
│  Dashboard   │     │ (Macro/Dermo)│     │(132 Symptoms)│
└──────────────┘     └──────────────┘     └──────────────┘
                                                 │
                                                 ▼
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│Chat Assistant│ <── │Step 6: Result│ <── │ Step 3: ABCDE│
│(Gemini Flash)│     │(Stratified)  │     │(Risk History)│
└──────────────┘     └──────────────┘     └──────────────┘
```

1. **Camera / Image Capture**: Take a close-up photo of the lesion or import from gallery. The app automatically performs center-square extraction and aspect-ratio preservation.
2. **Symptom Selection**: Search or multi-select from 132 dermatological symptoms (e.g., itching, scaling, erythema, rapid enlargement).
3. **ABCDE Checklist**: Complete the 5-point clinical criteria along with personal and familial risk factors.
4. **Review & Multi-Modal Inference**: Review inputs; local ONNX runtime computes predictions in under ~140ms.
5. **Stratification & Safety Guidance**: View primary diagnosis, malignancy probability, stratified risk level, and medical disclaimers.
6. **Clinical AI Counseling**: Engage in context-grounded conversational Q&A with Gemini 3.5 Flash-Lite to prepare for physician consultations.

---

## 📊 Performance Benchmarks (Edge Hardware)

Tested on **Google Pixel 7** (Google Tensor G2) and **Samsung Galaxy S21** (Snapdragon 888):

| Metric | Measured Value | Standard Target |
| :--- | :--- | :--- |
| **Vision Inference Latency** | **138 ms** (ARM NEON FP16) | $< 300\text{ ms}$ |
| **Symptom Model Latency** | **4.2 ms** (ONNX CPU) | $< 20\text{ ms}$ |
| **Peak Runtime Memory (Heap)** | **184 MB** | $< 256\text{ MB}$ |
| **Model Pre-Warm Barrier** | **~3.8 s** (One-time on splash) | Guaranteed zero-stall inference |
| **Melanoma Sensitivity** | **94.1%** ($N=3,200$) | $> 90.0\%$ |
| **Overall Macro AUC-ROC** | **0.957** across 8 ISIC classes | $> 0.900$ |

For full tabular breakdowns and ablation experiments, consult [**TECHNICAL_REPORT.md**](TECHNICAL_REPORT.md).

---

## 🔒 Privacy & Security Architecture

1. **Biometric Privacy**: Lesion images never leave the device. Image tensors are allocated directly in native memory buffers and cleared post-inference.
2. **Zero Cloud Vision Dependencies**: Full classification and triage works in airplane mode without internet connectivity.
3. **No Image Uploads to LLM**: The Gemini clinical assistant receives only structured metadata (predicted lesion class, confidence bucket, risk tier) to explain findings. Raw photos are **never** transmitted to cloud LLM APIs.
4. **Ephemeral Chat**: AI chat sessions are kept in transient memory and immediately wiped upon navigation, preventing unauthorized reading of sensitive health chats.

---

## ⚕️ Medical & Regulatory Disclaimer

> [!WARNING]
> **DermaLens is an investigational decision-support tool designed for informational and educational triage purposes only.**
> - DermaLens is **NOT** an FDA-approved or CE-marked medical device.
> - DermaLens does **NOT** provide a definitive medical diagnosis.
> - Dermatological lesions can present unpredictably. Always consult a qualified board-certified dermatologist, oncologist, or licensed physician for clinical examination, dermoscopy, and histopathological biopsy.
> - If a lesion bleeds, oozes, itches persistently, or rapidly changes shape or color, seek immediate medical attention regardless of app assessment.

---

## 📄 License

This project is licensed under the Apache 2.0 License - see the [LICENSE](LICENSE) file for details.
