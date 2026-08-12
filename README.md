<p align="center">
  <img src="assets/branding/logo.svg" width="160" alt="CamBG Record Logo">
</p>

<p align="center">
  <picture>
    <source media="(prefers-color-scheme: dark)" srcset="https://img.shields.io/badge/CamBG_Record-Background_Video_Recorder-6C3AED?style=for-the-badge&labelColor=1a1a2e&logo=android&logoColor=white">
    <img alt="CamBG Record" src="https://img.shields.io/badge/CamBG_Record-Background_Video_Recorder-6C3AED?style=for-the-badge&labelColor=1a1a2e&logo=android&logoColor=white">
  </picture>
</p>

<p align="center">
  <strong>A native Android application for persistent background video recording<br>with intelligent detection capabilities and Material Design 3 aesthetics.</strong>
</p>

<br>

<p align="center">
  <a href="#"><img alt="Platform" src="https://img.shields.io/badge/Platform-Android-34A853?style=flat-square&logo=android&logoColor=white"></a>
  <a href="#"><img alt="Min SDK" src="https://img.shields.io/badge/Min_SDK-24_(Nougat)-0D47A1?style=flat-square"></a>
  <a href="#"><img alt="Target SDK" src="https://img.shields.io/badge/Target_SDK-35-1565C0?style=flat-square"></a>
  <a href="#"><img alt="Compile SDK" src="https://img.shields.io/badge/Compile_SDK-36-1976D2?style=flat-square"></a>
  <a href="#"><img alt="Language" src="https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?style=flat-square&logo=kotlin&logoColor=white"></a>
  <a href="#"><img alt="Build System" src="https://img.shields.io/badge/Gradle-KTS-02303A?style=flat-square&logo=gradle&logoColor=white"></a>
  <a href="#"><img alt="UI" src="https://img.shields.io/badge/Jetpack_Compose-Material_3-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white"></a>
  <a href="#"><img alt="License" src="https://img.shields.io/badge/License-Proprietary-333333?style=flat-square"></a>
</p>

---

<br>

## Table of Contents

```
  I.    Overview
  II.   Core Capabilities
  III.  Technology Stack
  IV.   Architecture
  V.    Project Structure
  VI.   Prerequisites
  VII.  Getting Started
  VIII. Build Variants and Signing
  IX.   Automated Release Pipeline
  X.    Versioning
  XI.   License
```

<br>

---

<br>

## I. Overview

**CamBG Record** is a purpose-built Android application that enables continuous video recording in the background, operating seamlessly even when the device screen is locked or other applications are in the foreground. It leverages Android's foreground service architecture to maintain a persistent recording session across camera, microphone, and media projection channels.

The application is designed for scenarios that demand uninterrupted recording capabilities -- security monitoring, evidence capture, field documentation, and hands-free content creation -- all wrapped in a polished Material Design 3 interface with dynamic theming support.

<br>

---

<br>

## II. Core Capabilities

### Background Service Execution

| Capability | Description |
|:---|:---|
| Persistent Foreground Service | Utilizes a dedicated `ForegroundService` with combined `camera`, `microphone`, and `mediaProjection` types to maintain recording continuity. |
| Lock-Screen Operation | Continues recording with full fidelity while the device display is off or the application is moved to the background. |
| Battery Optimization Bypass | Requests exemption from battery optimization constraints to prevent service interruption on long-duration sessions. |

### Camera and Recording Configuration

| Capability | Description |
|:---|:---|
| CameraX Integration | Built on the `androidx.camera` suite (Camera2, Lifecycle, View, Video) for reliable hardware abstraction and lifecycle-aware capture. |
| Resolution Control | Supports configurable output resolution, including 1080p as the default profile. |
| Frame Rate Selection | Allows adjustment of capture frame rate (default: 30 fps) to balance quality and storage consumption. |
| Bitrate Management | Provides bitrate presets (High, Medium, Low) to fine-tune compression behavior. |
| Audio Capture | Configurable audio source selection (Camcorder) with stereo or mono channel modes. |

### Intelligent Detection

| Capability | Description |
|:---|:---|
| Motion Detection | Frame-differencing analysis with adjustable sensitivity (Low / Medium / High) to flag movement events during recording. |
| Person Detection | Confidence-threshold-based human presence identification, configurable from 50% to 95% precision. |
| Event Logging | Chronological event feed displaying detection type, timestamp, and confidence metrics for each triggered alert. |

### User Interface and Theming

| Capability | Description |
|:---|:---|
| Material Design 3 | Full Material You implementation with dynamic color extraction from the device wallpaper. |
| Theme Modes | System-follow, Light, and Dark theme selection via application settings. |
| Compose Navigation | Multi-screen architecture with bottom navigation across Camera, Gallery, Detection, and Settings destinations. |
| Gallery Management | Integrated media browser for reviewing, sharing, and managing recorded video files. |

<br>

---

<br>

## III. Technology Stack

| Layer | Technology | Version |
|:---|:---|:---|
| Language | Kotlin | 2.2.10 |
| UI Framework | Jetpack Compose + Material 3 | BOM 2024.09.00 |
| Build System | Gradle (Kotlin DSL) | AGP 9.1.1 |
| Camera | CameraX (Camera2, Video, View, Lifecycle) | 1.5.0 |
| Navigation | Navigation Compose | 2.8.9 |
| Persistence | Room Database | 2.7.0 |
| Preferences | DataStore Preferences | 1.1.7 |
| Networking | Retrofit + OkHttp + Moshi | 2.12.0 / 4.10.0 / 1.15.2 |
| Image Loading | Coil Compose | 2.7.0 |
| Permissions | Accompanist Permissions | 0.37.3 |
| Concurrency | Kotlinx Coroutines | 1.10.2 |
| Firebase | Firebase BOM (AI, App Check) | 34.15.0 |
| Code Generation | KSP (Room Compiler, Moshi Codegen) | 2.3.5 |
| Testing | JUnit + Espresso + Robolectric + Roborazzi | -- |

<br>

---

<br>

## IV. Architecture

The application follows the **MVVM (Model-View-ViewModel)** architectural pattern, cleanly separating concerns across three layers:

```
+---------------------------------------------------------------------+
|                        Presentation Layer                           |
|                                                                     |
|   +------------------+  +------------------+  +------------------+  |
|   |   CameraScreen   |  |  GalleryScreen   |  | DetectionScreen  |  |
|   +--------+---------+  +--------+---------+  +--------+---------+  |
|            |                      |                      |          |
|   +--------+---------+  +--------+---------+  +--------+---------+  |
|   |  CameraViewModel |  | GalleryViewModel |  |DetectionViewModel|  |
|   +--------+---------+  +--------+---------+  +--------+---------+  |
|                                                                     |
+------------------------------+--------------------------------------+
                               |
                    StateFlow / Coroutines
                               |
+------------------------------+--------------------------------------+
|                          Domain Layer                                |
|                                                                     |
|   +---------------------+   +---------------------+                |
|   | BackgroundRecording  |   | CameraXRecording    |                |
|   | Service              |   | Manager             |                |
|   +---------------------+   +---------------------+                |
|                                                                     |
+------------------------------+--------------------------------------+
                               |
+------------------------------+--------------------------------------+
|                          Data Layer                                  |
|                                                                     |
|   +---------------------+   +---------------------+                |
|   | Room Database        |   | DataStore            |                |
|   | (VideoItem)          |   | (AppSettings)        |                |
|   +---------------------+   +---------------------+                |
|                                                                     |
+---------------------------------------------------------------------+
```

<br>

---

<br>

## V. Project Structure

```
cambg-record/
|
+-- .github/
|   +-- scripts/
|   |   +-- generate_release_notes.js    # Gemini API release note generator
|   +-- workflows/
|   |   +-- android-release.yml          # CI/CD release pipeline
|   +-- release-prompt-template.md       # Prompt template for Gemini changelog
|
+-- app/
|   +-- src/
|   |   +-- main/
|   |       +-- java/com/example/
|   |       |   +-- MainActivity.kt              # Application entry point
|   |       |   +-- camera/
|   |       |   |   +-- CameraXRecordingManager.kt
|   |       |   +-- model/
|   |       |   |   +-- AppSettings.kt           # Settings data class
|   |       |   |   +-- CameraViewModel.kt       # Camera state management
|   |       |   |   +-- DetectionEvent.kt         # Detection event model
|   |       |   |   +-- DetectionViewModel.kt     # Detection state management
|   |       |   |   +-- GalleryViewModel.kt       # Gallery state management
|   |       |   |   +-- SettingsViewModel.kt      # Settings state management
|   |       |   |   +-- VideoItem.kt              # Video file model
|   |       |   +-- service/
|   |       |   |   +-- BackgroundRecordingService.kt
|   |       |   +-- ui/
|   |       |   |   +-- camera/
|   |       |   |   |   +-- CameraScreen.kt
|   |       |   |   +-- detection/
|   |       |   |   |   +-- DetectionScreen.kt
|   |       |   |   +-- gallery/
|   |       |   |   |   +-- GalleryScreen.kt
|   |       |   |   +-- navigation/
|   |       |   |   |   +-- AppNavigation.kt
|   |       |   |   +-- settings/
|   |       |   |   |   +-- SettingsScreen.kt
|   |       |   |   +-- theme/
|   |       |   |       +-- Color.kt
|   |       |   |       +-- Theme.kt
|   |       |   |       +-- Type.kt
|   |       |   +-- util/
|   |       |       +-- RecordedFilesHelper.kt
|   |       +-- res/                              # Resources (drawables, values, XML)
|   |       +-- AndroidManifest.xml
|   +-- build.gradle.kts                          # Module-level build config
|
+-- gradle/
|   +-- libs.versions.toml                        # Centralized dependency catalog
|
+-- .env.example                                  # Environment variable template
+-- build.gradle.kts                              # Root-level build config
+-- gradle.properties                             # Build environment settings
+-- metadata.json                                 # Project metadata
+-- settings.gradle.kts                           # Gradle module declarations
+-- RELEASE_GUIDE.md                              # Developer release instructions
+-- README.md                                     # This document
```

<br>

---

<br>

## VI. Prerequisites

Before building the project locally, ensure the following tools are installed and configured:

| Requirement | Minimum Version | Notes |
|:---|:---|:---|
| **Android Studio** | Ladybug or later | Required for Kotlin 2.x and Compose compiler plugin support. |
| **JDK** | 17 | The build is configured for Java 11 compatibility, but JDK 17 is used for the Gradle daemon. |
| **Android SDK** | API 36 (Compile) / API 24 (Min) | Install via Android Studio SDK Manager. |
| **Git** | 2.x | Required for cloning and for the release pipeline's tag-based workflow. |

<br>

---

<br>

## VII. Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/twinpath/cambg-record.git
cd cambg-record
```

### 2. Configure Environment Variables

Copy the example environment file and populate it with your credentials as needed:

```bash
cp .env.example .env
```

If using the Gemini API for Firebase AI features, uncomment and set the `GEMINI_API_KEY` value in the `.env` file.

### 3. Build the Project

Assemble a debug build using the Gradle wrapper:

```bash
# On Linux / macOS
./gradlew assembleDebug

# On Windows
gradlew.bat assembleDebug
```

### 4. Run on a Device or Emulator

Deploy the debug APK directly to a connected device:

```bash
./gradlew installDebug
```

Alternatively, use the **Run** configuration within Android Studio to launch the application on a selected target.

<br>

---

<br>

## VIII. Build Variants and Signing

### Debug Builds

Debug builds are signed automatically with a local `debug.keystore` using the standard Android debug credentials.

### Release Builds

Release builds require a signing keystore. The signing configuration reads from the following environment variables:

| Variable | Description |
|:---|:---|
| `KEYSTORE_PATH` | Absolute path to the `.jks` keystore file. Defaults to `./my-upload-key.jks`. |
| `STORE_PASSWORD` | Password for the keystore. |
| `KEY_PASSWORD` | Password for the signing key (alias: `upload`). |

To produce a signed release build locally:

```bash
export KEYSTORE_PATH=/path/to/your/keystore.jks
export STORE_PASSWORD=your_store_password
export KEY_PASSWORD=your_key_password

./gradlew assembleRelease bundleRelease
```

<br>

---

<br>

## IX. Automated Release Pipeline

Releases are fully automated via GitHub Actions. The pipeline is defined in `.github/workflows/android-release.yml` and is triggered by pushing a version tag matching the pattern `v*`.

### Pipeline Stages

```
  Tag Push (v*)
       |
       v
  +--------------------+
  | Checkout Source     |
  +--------------------+
       |
       v
  +--------------------+
  | Setup JDK 17       |
  +--------------------+
       |
       v
  +--------------------+
  | Decode Keystore    |------> (from KEYSTORE_BASE64 secret)
  +--------------------+
       |
       v
  +--------------------+
  | Build APK + AAB    |------> assembleRelease + bundleRelease
  +--------------------+
       |
       v
  +--------------------+
  | Generate Notes     |------> Gemini API via generate_release_notes.js
  +--------------------+
       |
       v
  +--------------------+
  | Publish Release    |------> softprops/action-gh-release@v2
  +--------------------+
```

### Required Repository Secrets

| Secret | Purpose |
|:---|:---|
| `KEYSTORE_BASE64` | Base64-encoded signing keystore for release builds. |
| `STORE_PASSWORD` | Keystore password. |
| `KEY_PASSWORD` | Signing key password. |
| `GEMINI_API_KEY` | API key for Gemini-powered release note generation. |

For a complete step-by-step walkthrough, refer to the [RELEASE_GUIDE.md](RELEASE_GUIDE.md).

<br>

---

<br>

## X. Versioning

This project adheres to [Semantic Versioning 2.0.0](https://semver.org/) with the tag format:

```
v<MAJOR>.<MINOR>.<PATCH>[-<SUFFIX>.<N>]
```

| Release Type | Tag Pattern | Example | Description |
|:---|:---|:---|:---|
| **Stable** | `vX.Y.Z` | `v1.0.0` | Production-ready release. |
| **Beta** | `vX.Y.Z-beta.N` | `v1.0.0-beta.1` | Public pre-release for beta testing. |
| **Alpha** | `vX.Y.Z-alpha.N` | `v1.0.0-alpha.2` | Internal pre-release for QA validation. |
| **Test** | `vX.Y.Z-test.N` | `v1.0.0-test.1` | Pipeline validation and CI/CD testing. |

<br>

---

<br>

## XI. License

This project is proprietary software. All rights reserved.

Unauthorized copying, modification, distribution, or use of this software, via any medium, is strictly prohibited without prior written consent from the project owner.

<br>

---

<p align="center">
  <sub>Built with precision. Designed for reliability.</sub>
</p>
