import { RELEASE_ARCHIVE, type Release } from "@/data/releases"

function formatBytes(bytes: number, decimals = 1) {
  if (bytes === 0) return "0 Bytes"
  const k = 1024
  const dm = decimals < 0 ? 0 : decimals
  const sizes = ["Bytes", "KB", "MB", "GB"]
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + " " + sizes[i]
}

function getArchitecture(fileName: string): string {
  const lower = fileName.toLowerCase()
  if (lower.includes("arm64-v8a") || lower.includes("arm64")) return "ARM64 (v8a)"
  if (lower.includes("armeabi-v7a") || lower.includes("armeabi")) return "ARMv7 (32-bit)"
  if (lower.includes("x86_64")) return "x86_64"
  if (lower.includes("x86")) return "x86"
  if (lower.includes("universal")) return "Universal"
  if (lower.includes(".aab")) return "App Bundle (Universal)"
  return "Universal"
}

export async function fetchGitHubReleases(): Promise<Release[]> {
  try {
    const res = await fetch("https://api.github.com/repos/twinpath/cambg/releases", {
      headers: {
        "User-Agent": "Astro-Site-Build",
      },
    })

    if (!res.ok) {
      console.warn("Failed to fetch releases from GitHub API, using fallback data:", res.statusText)
      return RELEASE_ARCHIVE
    }

    const data = await res.json()
    if (!Array.isArray(data)) {
      return RELEASE_ARCHIVE
    }

    return data.map((item: any) => {
      const tag = item.tag_name || ""
      let releaseType: Release["releaseType"] = "stable"
      const tagLower = tag.toLowerCase()

      if (tagLower.includes("alpha")) {
        releaseType = "alpha"
      } else if (tagLower.includes("beta")) {
        releaseType = "beta"
      } else if (tagLower.includes("test")) {
        releaseType = "test"
      } else if (item.prerelease) {
        releaseType = "beta" // Fallback prerelease type if no keyword is present
      }

      const assets = (item.assets || [])
        .filter((asset: any) => !asset.name.endsWith(".zip") && !asset.name.endsWith(".tar.gz"))
        .map((asset: any) => ({
          name: asset.name,
          downloadUrl: asset.browser_download_url,
          sizeLabel: formatBytes(asset.size),
          architecture: getArchitecture(asset.name),
        }))

      return {
        version: tag,
        releaseType,
        date: item.published_at ? item.published_at.split("T")[0] : "",
        notes: item.body || "",
        assets,
      }
    })
  } catch (error) {
    console.error("Error fetching GitHub releases, falling back to static data:", error)
    return RELEASE_ARCHIVE
  }
}
