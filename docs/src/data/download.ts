import { Download, Shield, Settings, CheckCircle } from "lucide-react"

export const DOWNLOAD_PAGE_METADATA = {
  title: "Download",
  description:
    "Download CamBG Record APK for Android. Browse the release archive for all available versions including stable, beta, and alpha builds.",
} as const

export const DOWNLOAD_HERO_CONTENT = {
  heading: "Download CamBG Record",
  description:
    "Get the latest release of CamBG Record for your Android device. Available as a direct APK download from GitHub Releases.",
  buttonLabel: "Download APK",
} as const

export const INSTALL_GUIDE_CONTENT = {
  heading: "Installation Guide",
  description:
    "Follow these steps to install CamBG Record on your Android device via direct APK sideloading.",
  steps: [
    {
      icon: Download,
      title: "Download the APK",
      description:
        "Tap the download button above or select a specific version from the Release Archive. The APK file will be saved to your device's Downloads folder.",
    },
    {
      icon: Shield,
      title: "Enable Unknown Sources",
      description:
        "Navigate to Settings and then Security and then Install Unknown Apps. Select your browser or file manager and enable 'Allow from this source' to permit sideloading.",
    },
    {
      icon: Settings,
      title: "Install the Application",
      description:
        "Open the downloaded APK file using your file manager. Tap 'Install' when prompted by the system package installer. The process typically completes within seconds.",
    },
    {
      icon: CheckCircle,
      title: "Grant Permissions",
      description:
        "On first launch, grant the required permissions: Camera, Microphone, Storage, and Notification access. The app will guide you through each permission request.",
    },
  ],
} as const

export const RELEASE_ARCHIVE_CONTENT = {
  heading: "Release Archive",
  description:
    "Browse all previous releases. Expand any version to view release notes and download specific build artifacts.",
  tableHeaders: {
    file: "File",
    architecture: "Architecture",
    size: "Size",
  },
  buttons: {
    download: "Download",
    loadMore: "Load More",
  },
} as const
