import { useState, useEffect } from "react"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Skeleton } from "@/components/ui/skeleton"
import { Calendar } from "lucide-react"
import { marked } from "marked"
import type { Release } from "@/data/releases"
import { getArchitecture } from "@/lib/architecture"
import { CHANGELOG_LIST_CONTENT } from "@/data/changelog"

interface ChangelogListProps {
  fallbackReleases: Release[]
}

const RELEASE_TYPE_VARIANT = {
  stable: "default",
  beta: "secondary",
  alpha: "outline",
  test: "destructive",
} as const

function formatDate(dateStr: string) {
  if (!dateStr) return CHANGELOG_LIST_CONTENT.labels.unknownDate
  return new Date(dateStr).toLocaleDateString("en-US", {
    year: "numeric",
    month: "long",
    day: "numeric",
  })
}

function cleanReleaseNotes(notes: string): string {
  if (!notes) return CHANGELOG_LIST_CONTENT.labels.noDetails
  let cleaned = notes
  cleaned = cleaned.replace(/### Build Artifacts[\s\S]*$/, "")
  cleaned = cleaned.replace(/### Artifacts[\s\S]*$/, "")
  cleaned = cleaned.replace(/\| File Name \|[\s\S]*$/, "")
  return cleaned.trim()
}

export function ChangelogList({ fallbackReleases }: ChangelogListProps) {
  const [releases, setReleases] = useState<Release[]>(fallbackReleases)
  const [loading, setLoading] = useState(true)
  const [visibleCount, setVisibleCount] = useState(5)
  const [parsedNotes, setParsedNotes] = useState<Record<string, string>>({})

  useEffect(() => {
    async function loadReleases() {
      try {
        const res = await fetch("https://api.github.com/repos/twinpath/cambg/releases")
        if (!res.ok) throw new Error("API Limit or Network error")
        const data = await res.json()
        if (Array.isArray(data)) {
          const mapped: Release[] = data.map((item: any) => {
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
              releaseType = "beta"
            }

            const assets = (item.assets || [])
              .filter((asset: any) => !asset.name.endsWith(".zip") && !asset.name.endsWith(".tar.gz"))
              .map((asset: any) => ({
                name: asset.name,
                downloadUrl: asset.browser_download_url,
                sizeLabel: asset.size ? `${(asset.size / (1024 * 1024)).toFixed(1)} MB` : "Unknown",
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
          setReleases(mapped)
        }
      } catch (err) {
        console.warn("Client-side releases fetch failed, falling back to static build data", err)
        // Keep static fallbackReleases
      } finally {
        setLoading(false)
      }
    }
    loadReleases()
  }, [fallbackReleases])

  // Parse markdown to HTML asynchronously when releases change
  useEffect(() => {
    async function parseAll() {
      const records: Record<string, string> = {}
      for (const rel of releases) {
        const cleaned = cleanReleaseNotes(rel.notes)
        const html = await marked.parse(cleaned)
        records[rel.version] = html
      }
      setParsedNotes(records)
    }
    parseAll()
  }, [releases])

  const visibleReleases = releases.slice(0, visibleCount)

  if (loading) {
    return (
      <div className="relative border-l border-muted pl-6 ml-4 space-y-12">
        {[1, 2, 3].map((i) => (
          <div key={i} className="relative space-y-4">
            <div className="absolute -left-[31px] top-1.5 flex h-4 w-4 items-center justify-center rounded-full bg-background border-2 border-muted">
              <div className="h-1.5 w-1.5 rounded-full bg-muted" />
            </div>
            <div className="flex items-center gap-3">
              <Skeleton className="h-6 w-20 rounded" />
              <Skeleton className="h-5 w-12 rounded" />
              <Skeleton className="h-4 w-28 rounded ml-auto" />
            </div>
            <div className="space-y-2">
              <Skeleton className="h-4 w-full rounded" />
              <Skeleton className="h-4 w-5/6 rounded" />
              <Skeleton className="h-4 w-4/5 rounded" />
            </div>
          </div>
        ))}
      </div>
    )
  }

  return (
    <div className="space-y-12">
      <div className="relative border-l border-muted pl-6 ml-4 space-y-12">
        {visibleReleases.map((release) => {
          const htmlContent = parsedNotes[release.version] || `<p>${CHANGELOG_LIST_CONTENT.labels.loadingDetails}</p>`
          return (
            <div 
              key={release.version} 
              className="relative transition-all duration-300 animate-in fade-in slide-in-from-bottom-2"
            >
              {/* Dot indicator */}
              <div className="absolute -left-[31px] top-1.5 flex h-4 w-4 items-center justify-center rounded-full bg-background border-2 border-primary">
                <div className="h-1.5 w-1.5 rounded-full bg-primary" />
              </div>

              <div className="flex flex-col gap-3">
                <div className="flex flex-wrap items-center gap-2">
                  <h2 className="font-heading text-xl font-bold tracking-tight">
                    {release.version}
                  </h2>
                  <Badge variant={RELEASE_TYPE_VARIANT[release.releaseType]}>
                    {release.releaseType}
                  </Badge>
                  <div className="flex items-center gap-1 text-xs text-muted-foreground ml-auto">
                    <Calendar className="size-3" />
                    <span>{formatDate(release.date)}</span>
                  </div>
                </div>

                {/* Release details / body parsed markdown */}
                <div 
                  className="changelog-prose max-w-none"
                  dangerouslySetInnerHTML={{ __html: htmlContent }}
                />
              </div>
            </div>
          )
        })}
      </div>

      {releases.length > visibleCount && (
        <div className="flex justify-center mt-12">
          <Button 
            variant="outline"
            onClick={() => setVisibleCount((prev) => prev + 5)}
            className="min-w-32"
          >
            {CHANGELOG_LIST_CONTENT.labels.loadMore}
          </Button>
        </div>
      )}
    </div>
  )
}
