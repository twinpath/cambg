export interface ReleaseAsset {
  name: string
  downloadUrl: string
  sizeLabel: string
  architecture?: string
  sha256?: string
}

export interface Release {
  version: string
  versionCode?: number
  releaseType: "stable" | "beta" | "alpha" | "test"
  date: string
  notes: string
  assets: ReleaseAsset[]
}
