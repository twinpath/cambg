import {
  Video,
  Settings,
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


export const CTA_CONTENT = {
  heading: "Ready to Get Started?",
  description:
    "Download CamBG Record and experience persistent background video recording with intelligent detection on your Android device.",
  buttons: {
    download: "Download Now",
    viewSource: "View Source",
  },
} as const
