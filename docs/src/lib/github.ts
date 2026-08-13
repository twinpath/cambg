import { RELEASE_ARCHIVE, type Release } from "@/data/releases"
import { getArchitecture } from "./architecture"

function formatBytes(bytes: number, decimals = 1) {
  if (bytes === 0) return "0 Bytes"
  const k = 1024
  const dm = decimals < 0 ? 0 : decimals
  const sizes = ["Bytes", "KB", "MB", "GB"]
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + " " + sizes[i]
}

export async function fetchGitHubReleases(): Promise<Release[]> {
  try {
    const headers: Record<string, string> = {
      "User-Agent": "Astro-Site-Build",
    }

    const token = typeof process !== "undefined" && process.env ? process.env.GITHUB_TOKEN : undefined
    if (token) {
      headers["Authorization"] = `Bearer ${token}`
    }

    const res = await fetch("https://api.github.com/repos/twinpath/cambg/releases", {
      headers,
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
