import { CHANGELOG_LIST_CONTENT } from "@/data/changelog"

export function formatDate(dateStr: string, monthFormat: "long" | "short" = "long") {
  if (!dateStr) return CHANGELOG_LIST_CONTENT?.labels?.unknownDate || "Unknown Date"
  return new Date(dateStr).toLocaleDateString("en-US", {
    year: "numeric",
    month: monthFormat,
    day: "numeric",
  })
}

export function cleanReleaseNotes(notes: string): string {
  if (!notes) return CHANGELOG_LIST_CONTENT?.labels?.noDetails || "No details provided."
  let cleaned = notes
  cleaned = cleaned.replace(/### Build Artifacts[\s\S]*$/, "")
  cleaned = cleaned.replace(/### Artifacts[\s\S]*$/, "")
  cleaned = cleaned.replace(/\| File Name \|[\s\S]*$/, "")
  return cleaned.trim()
}
