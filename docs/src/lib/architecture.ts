import { ARCHITECTURE_MAP } from "@/data/architectures"

export function getArchitecture(fileName: string): string {
  const lower = fileName.toLowerCase()
  for (const [key, label] of Object.entries(ARCHITECTURE_MAP)) {
    if (lower.includes(key)) {
      return label
    }
  }
  return "Universal"
}

export function getClientArch(): string {
  if (typeof window === "undefined" || !window.navigator) return "universal"
  const ua = window.navigator.userAgent.toLowerCase()
  const platform = (window.navigator.platform || "").toLowerCase()
  
  if (ua.includes("arm64") || ua.includes("aarch64")) return "arm64-v8a"
  if (ua.includes("arm") || ua.includes("armeabi")) return "armeabi-v7a"
  if (ua.includes("x86_64") || ua.includes("amd64") || platform.includes("win64") || platform.includes("macintel")) return "x86_64"
  if (ua.includes("x86") || ua.includes("i686")) return "x86"
  
  return "universal"
}
