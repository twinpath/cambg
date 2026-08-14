import { useState, useEffect } from "react"
import type { TechStackItem } from "@/types/tech-stack"
import { API_ENDPOINTS } from "@/data/site"

export function useTechStack() {
  const [techStack, setTechStack] = useState<TechStackItem[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let isMounted = true
    async function loadTechStack() {
      try {
        const res = await fetch(API_ENDPOINTS.TECH_STACK)
        if (!res.ok) throw new Error("Failed to fetch tech stack")
        const data = await res.json()
        if (Array.isArray(data) && isMounted) {
          setTechStack(data)
        }
      } catch (err) {
        console.warn("Client-side fetch failed, using fallback data", err)
      } finally {
        if (isMounted) {
          setLoading(false)
        }
      }
    }
    loadTechStack()
    return () => {
      isMounted = false
    }
  }, [])

  return { techStack, loading }
}
