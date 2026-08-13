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
