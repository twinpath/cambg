import type { Release } from "@/types/releases"
import { getArchitecture } from "./architecture"

function formatBytes(bytes: number, decimals = 1) {
  if (bytes === 0) return "0 Bytes"
  const k = 1024
  const dm = decimals < 0 ? 0 : decimals
  const sizes = ["Bytes", "KB", "MB", "GB"]
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + " " + sizes[i]
}

function parseDigestSha256(digest?: string): string | undefined {
  if (digest && digest.startsWith("sha256:")) {
    return digest.replace("sha256:", "")
  }
  return undefined
}

function extractSHA256(body: string, filename: string): string | undefined {
  if (!body) return undefined
  const escapedFilename = filename.replace(/[-\/\\^$*+?.()|[\]{}]/g, '\\$&')
  const patterns = [
    new RegExp(`${escapedFilename}[\\s\\S]*?([a-fA-F0-9]{64})`, 'i'),
    new RegExp(`([a-fA-F0-9]{64})[\\s\\S]*?${escapedFilename}`, 'i')
  ]

  for (const pattern of patterns) {
    const match = body.match(pattern)
    if (match) {
      const indexFile = body.indexOf(filename)
      const indexHash = body.indexOf(match[1])
      if (Math.abs(indexFile - indexHash) < 500) {
        return match[1]
      }
    }
  }
  return undefined
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
      return []
    }

    const data = await res.json()
    if (!Array.isArray(data)) {
      return []
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
          sha256: parseDigestSha256(asset.digest) || extractSHA256(item.body || "", asset.name),
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
    return []
  }
}
