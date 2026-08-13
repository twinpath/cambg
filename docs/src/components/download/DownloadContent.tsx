import { useState, useEffect } from "react"
import { DownloadHero } from "./DownloadHero"
import { ReleaseArchive } from "./ReleaseArchive"
import { API_ENDPOINTS } from "@/data/site"
import type { Release } from "@/data/releases"

export function DownloadContent({ fallbackReleases }: { fallbackReleases: Release[] }) {
  const [releases, setReleases] = useState<Release[]>(fallbackReleases)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    async function loadReleases() {
      try {
        const res = await fetch(API_ENDPOINTS.RELEASES)
        if (!res.ok) throw new Error("Failed to fetch releases")
        const data = await res.json()
        if (Array.isArray(data)) {
          setReleases(data)
        }
      } catch (err) {
        console.warn("Client-side fetch failed, using fallback data", err)
      } finally {
        setLoading(false)
      }
    }
    loadReleases()
  }, [])

  return (
    <>
      <DownloadHero latest={releases[0]} isLoading={loading} />
      <ReleaseArchive releases={releases} isLoading={loading} />
    </>
  )
}
