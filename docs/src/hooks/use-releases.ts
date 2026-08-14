import { useState, useEffect } from "react"
import type { Release } from "@/types/releases"
import { API_ENDPOINTS } from "@/data/site"

export function useReleases(fallbackReleases: Release[] = []) {
  const [releases, setReleases] = useState<Release[]>(fallbackReleases)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let isMounted = true
    async function loadReleases() {
      try {
        const res = await fetch(API_ENDPOINTS.RELEASES)
        if (!res.ok) throw new Error("Failed to fetch releases")
        const data = await res.json()
        if (Array.isArray(data) && isMounted) {
          setReleases(data)
        }
      } catch (err) {
        console.warn("Client-side fetch failed, using fallback data", err)
      } finally {
        if (isMounted) {
          setLoading(false)
        }
      }
    }
    loadReleases()
    return () => {
      isMounted = false
    }
  }, [])

  return { releases, loading }
}
