import { useState, useEffect } from "react"

export function useLoading(delay = 600) {
  const [loading, setLoading] = useState(true)
  const resolvedDelay = Math.max(delay, 600)

  useEffect(() => {
    const timer = setTimeout(() => {
      setLoading(false)
    }, resolvedDelay)
    return () => clearTimeout(timer)
  }, [resolvedDelay])

  return loading
}
