export interface ReleaseAsset {
  name: string
  downloadUrl: string
  sizeLabel: string
}

export interface Release {
  version: string
  releaseType: "stable" | "beta" | "alpha" | "test"
  date: string
  notes: string
  assets: ReleaseAsset[]
}

export const LATEST_RELEASE: Release = {
  version: "v1.0.0-beta.2",
  releaseType: "beta",
  date: "2026-08-10",
  notes:
    "Second beta release with improved background recording stability, motion detection sensitivity tuning, and battery optimization enhancements. Fixed camera preview orientation on select devices.",
  assets: [
    {
      name: "cambg-record-v1.0.0-beta.2-arm64-v8a-release.apk",
      downloadUrl:
        "https://github.com/twinpath/cambg/releases/download/v1.0.0-beta.2/cambg-record-v1.0.0-beta.2-arm64-v8a-release.apk",
      sizeLabel: "42.3 MB",
    },
    {
      name: "cambg-record-v1.0.0-beta.2-armeabi-v7a-release.apk",
      downloadUrl:
        "https://github.com/twinpath/cambg/releases/download/v1.0.0-beta.2/cambg-record-v1.0.0-beta.2-armeabi-v7a-release.apk",
      sizeLabel: "38.1 MB",
    },
  ],
}

export const RELEASE_ARCHIVE: Release[] = [
  LATEST_RELEASE,
  {
    version: "v1.0.0-beta.1",
    releaseType: "beta",
    date: "2026-07-28",
    notes:
      "Initial beta release featuring background recording service, CameraX integration, motion and person detection, Material Design 3 UI with dynamic theming, and automated release pipeline.",
    assets: [
      {
        name: "cambg-record-v1.0.0-beta.1-arm64-v8a-release.apk",
        downloadUrl:
          "https://github.com/twinpath/cambg/releases/download/v1.0.0-beta.1/cambg-record-v1.0.0-beta.1-arm64-v8a-release.apk",
        sizeLabel: "41.8 MB",
      },
      {
        name: "cambg-record-v1.0.0-beta.1-armeabi-v7a-release.apk",
        downloadUrl:
          "https://github.com/twinpath/cambg/releases/download/v1.0.0-beta.1/cambg-record-v1.0.0-beta.1-armeabi-v7a-release.apk",
        sizeLabel: "37.5 MB",
      },
    ],
  },
  {
    version: "v1.0.0-alpha.3",
    releaseType: "alpha",
    date: "2026-07-15",
    notes:
      "Alpha release with person detection confidence thresholds, gallery management improvements, and Settings screen overhaul.",
    assets: [
      {
        name: "cambg-record-v1.0.0-alpha.3-arm64-v8a-release.apk",
        downloadUrl:
          "https://github.com/twinpath/cambg/releases/download/v1.0.0-alpha.3/cambg-record-v1.0.0-alpha.3-arm64-v8a-release.apk",
        sizeLabel: "40.2 MB",
      },
    ],
  },
  {
    version: "v1.0.0-alpha.2",
    releaseType: "alpha",
    date: "2026-07-01",
    notes:
      "Added motion detection with adjustable sensitivity, event logging chronological feed, and frame-differencing analysis engine.",
    assets: [
      {
        name: "cambg-record-v1.0.0-alpha.2-arm64-v8a-release.apk",
        downloadUrl:
          "https://github.com/twinpath/cambg/releases/download/v1.0.0-alpha.2/cambg-record-v1.0.0-alpha.2-arm64-v8a-release.apk",
        sizeLabel: "39.0 MB",
      },
    ],
  },
  {
    version: "v1.0.0-alpha.1",
    releaseType: "alpha",
    date: "2026-06-18",
    notes:
      "First alpha release with core background recording service, CameraX capture pipeline, and basic UI scaffolding.",
    assets: [
      {
        name: "cambg-record-v1.0.0-alpha.1-arm64-v8a-release.apk",
        downloadUrl:
          "https://github.com/twinpath/cambg/releases/download/v1.0.0-alpha.1/cambg-record-v1.0.0-alpha.1-arm64-v8a-release.apk",
        sizeLabel: "36.4 MB",
      },
    ],
  },
]
