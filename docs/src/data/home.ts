import {
  Video,
  Settings,
  Radar,
  User,
  Palette,
  BatteryCharging,
} from "lucide-react"

export const HERO_CONTENT = {
  badge: "Android Native",
  buttons: {
    download: "Download",
    learnMore: "Learn More",
  },
} as const

export const FEATURES_CONTENT = {
  heading: "Core Capabilities",
  description:
    "Purpose-built for scenarios that demand uninterrupted recording -- security monitoring, evidence capture, field documentation, and hands-free content creation.",
  items: [
    {
      icon: Video,
      title: "Background Recording",
      description:
        "Persistent foreground service maintains recording continuity even when the screen is locked or other apps are in the foreground.",
    },
    {
      icon: Settings,
      title: "Camera Configuration",
      description:
        "Configurable resolution, frame rate, bitrate presets, and audio capture with CameraX hardware abstraction.",
    },
    {
      icon: Radar,
      title: "Motion Detection",
      description:
        "Frame-differencing analysis with adjustable sensitivity levels to flag movement events during active recording sessions.",
    },
    {
      icon: User,
      title: "Person Detection",
      description:
        "Confidence-threshold-based human presence identification, configurable from 50% to 95% precision.",
    },
    {
      icon: Palette,
      title: "Material Design 3",
      description:
        "Full Material You implementation with dynamic color extraction from device wallpaper and system-follow theming.",
    },
    {
      icon: BatteryCharging,
      title: "Battery Optimization",
      description:
        "Requests exemption from battery optimization constraints to prevent service interruption on long-duration sessions.",
    },
  ],
} as const

export const TECH_STACK_CONTENT = {
  heading: "Technology Stack",
  description:
    "Built with modern Android development tools and libraries for maximum reliability and performance.",
  tableHeaders: {
    layer: "Layer",
    technology: "Technology",
    version: "Version",
  },
  items: [
    { layer: "Language", technology: "Kotlin", version: "2.2.10" },
    {
      layer: "UI Framework",
      technology: "Jetpack Compose + Material 3",
      version: "BOM 2024.09.00",
    },
    {
      layer: "Build System",
      technology: "Gradle (Kotlin DSL)",
      version: "AGP 9.1.1",
    },
    {
      layer: "Camera",
      technology: "CameraX (Camera2, Video, View, Lifecycle)",
      version: "1.5.0",
    },
    {
      layer: "Navigation",
      technology: "Navigation Compose",
      version: "2.8.9",
    },
    { layer: "Persistence", technology: "Room Database", version: "2.7.0" },
    {
      layer: "Preferences",
      technology: "DataStore Preferences",
      version: "1.1.7",
    },
    {
      layer: "Networking",
      technology: "Retrofit + OkHttp + Moshi",
      version: "2.12.0 / 4.10.0 / 1.15.2",
    },
    {
      layer: "Image Loading",
      technology: "Coil Compose",
      version: "2.7.0",
    },
    {
      layer: "Concurrency",
      technology: "Kotlinx Coroutines",
      version: "1.10.2",
    },
    {
      layer: "Firebase",
      technology: "Firebase BOM (AI, App Check)",
      version: "34.15.0",
    },
  ],
} as const

export const CTA_CONTENT = {
  heading: "Ready to Get Started?",
  description:
    "Download CamBG Record and experience persistent background video recording with intelligent detection on your Android device.",
  buttons: {
    download: "Download Now",
    viewSource: "View Source",
  },
} as const
