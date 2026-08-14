import { Monitor, Cpu, Database, Scale, GitBranch, Wrench } from "lucide-react"

export const ABOUT_PAGE_METADATA = {
  title: "About",
  description:
    "Learn about CamBG Record, its MVVM architecture, technology stack, and project information. Find answers to frequently asked questions.",
} as const

export const ABOUT_HERO_CONTENT = {
  badge: "Proprietary App & MIT Website",
  heading: "About CamBG Record",
  paragraphs: [
    "CamBG Record is a purpose-built Android application that enables continuous video recording in the background, operating seamlessly even when the device screen is locked or other applications are in the foreground.",
    "Designed for scenarios that demand uninterrupted recording capabilities -- security monitoring, evidence capture, field documentation, and hands-free content creation -- all wrapped in a polished Material Design 3 interface with dynamic theming support.",
  ],
} as const

export const ARCHITECTURE_CONTENT = {
  heading: "Architecture",
  description:
    "CamBG Record follows the MVVM (Model-View-ViewModel) pattern, cleanly separating concerns across three layers.",
  layers: [
    {
      icon: Monitor,
      title: "Presentation Layer",
      description: "UI screens and ViewModels managing application state.",
      components: [
        "CameraScreen",
        "GalleryScreen",
        "SettingsScreen",
        "CameraViewModel",
        "GalleryViewModel",
        "SettingsViewModel",
      ],
    },
    {
      icon: Cpu,
      title: "Domain Layer",
      description:
        "Business logic and service orchestration using StateFlow and Coroutines.",
      components: ["BackgroundRecordingService", "CameraXRecordingManager"],
    },
    {
      icon: Database,
      title: "Data Layer",
      description: "Persistence and local storage for application data.",
      components: ["Room Database (VideoItem)", "DataStore (AppSettings)"],
    },
  ],
} as const

export const FAQ_CONTENT = {
  heading: "Frequently Asked Questions",
  description:
    "Common questions about CamBG Record, its features, and how to get started.",
  items: [
    {
      question: "What Android versions are supported?",
      answer:
        "CamBG Record requires Android 7.0 (Nougat, API level 24) or higher. The application is compiled against SDK 36 and targets SDK 35 for maximum compatibility with modern Android security policies.",
    },
    {
      question: "Does the app record when the screen is off?",
      answer:
        "Yes. CamBG Record utilizes a persistent foreground service with camera, microphone, and media projection types to maintain recording continuity even when the device display is off or the application is moved to the background.",
    },
    {
      question: "What about battery consumption?",
      answer:
        "CamBG Record includes an option to request battery optimization exemptions from the Android system. This prevents the OS from interrupting the recording service during long-duration sessions. Battery impact varies based on resolution, frame rate, and detection settings.",
    },
    {
      question: "Is my data private?",
      answer:
        "All recorded video files are stored locally on your device. The application does not upload recordings to any remote server. Network access is used only for optional Firebase AI features and app update checks.",
    },
    {
      question: "Can I configure video quality?",
      answer:
        "Yes. CamBG Record offers configurable output resolution (including 1080p default), adjustable frame rate (default 30 fps), selectable bitrate presets (High, Medium, Low), and audio source configuration with stereo or mono channel modes.",
    },
    {
      question: "How do I install the APK?",
      answer:
        "Download the APK from the Download page, enable 'Install Unknown Apps' in your device security settings for your browser or file manager, then open the APK file to begin installation. On first launch, grant Camera, Microphone, Storage, and Notification permissions.",
    },
    {
      question: "Is the source code available?",
      answer:
        "Yes. CamBG Record is hosted on GitHub at github.com/twinpath/cambg. The project uses a proprietary license -- all rights are reserved by the project owner.",
    },
  ],
} as const

export const PROJECT_INFO_CONTENT = {
  heading: "Project Information",
  description:
    "Technical details about the CamBG Record project, its licensing, and development infrastructure.",
  items: [
    {
      icon: Scale,
      title: "License",
      description:
        "The Android application is proprietary (all rights reserved by twinpath). The documentation website codebase is open-source under the MIT License.",
    },
    {
      icon: GitBranch,
      title: "Versioning",
      description:
        "Follows Semantic Versioning 2.0.0. Releases include Stable (vX.Y.Z), Beta (vX.Y.Z-beta.N), Alpha (vX.Y.Z-alpha.N), and Test (vX.Y.Z-test.N) variants.",
    },
    {
      icon: Wrench,
      title: "Build System",
      description:
        "Built with Gradle (Kotlin DSL) and Android Gradle Plugin. Releases are automated via GitHub Actions with Gemini API-powered changelog generation.",
    },
  ],
  badge: "Proprietary License",
  labels: {
    sourceCode: "Source Code",
    githubReleases: "GitHub Releases",
  },
} as const
