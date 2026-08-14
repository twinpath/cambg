import { useState, useEffect } from "react"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { SYSTEM_REQUIREMENTS } from "@/data/site"
import { DOWNLOAD_HERO_CONTENT } from "@/data/download"
import { Download, Smartphone } from "lucide-react"
import type { Release } from "@/types/releases"
import { Skeleton } from "@/components/ui/skeleton"
import { useLoading } from "@/hooks/use-loading"

function getClientArch(): string {
  if (typeof window === "undefined" || !window.navigator) return "universal"
  const ua = window.navigator.userAgent.toLowerCase()
  const platform = (window.navigator.platform || "").toLowerCase()
  
  if (ua.includes("arm64") || ua.includes("aarch64")) return "arm64-v8a"
  if (ua.includes("arm") || ua.includes("armeabi")) return "armeabi-v7a"
  if (ua.includes("x86_64") || ua.includes("amd64") || platform.includes("win64") || platform.includes("macintel")) return "x86_64"
  if (ua.includes("x86") || ua.includes("i686")) return "x86"
  
  return "universal"
}

export function DownloadHero({ latest, isLoading }: { latest: Release; isLoading: boolean }) {
  const loading = isLoading
  const [arch, setArch] = useState<string>("universal")

  useEffect(() => {
    setArch(getClientArch())
  }, [])

  return (
    <section className="flex flex-col items-center gap-6 px-4 py-20 text-center">
      <div className="flex items-center gap-2">
        {loading || !latest ? (
          <>
            <Skeleton className="h-5 w-16 rounded" />
            <Skeleton className="h-5 w-12 rounded" />
          </>
        ) : (
          <>
            <Badge variant="secondary">{latest.version}</Badge>
            <Badge variant="outline">{latest.releaseType}</Badge>
          </>
        )}
      </div>

      {loading ? (
        <div className="flex flex-col items-center gap-2 w-full max-w-2xl">
          <Skeleton className="h-10 w-3/4 rounded md:h-12" />
          <Skeleton className="h-10 w-1/2 rounded md:h-12" />
        </div>
      ) : (
        <h1 className="font-heading max-w-2xl text-3xl font-bold tracking-tight md:text-5xl">
          {DOWNLOAD_HERO_CONTENT.heading}
        </h1>
      )}

      {loading ? (
        <div className="flex flex-col items-center gap-2 w-full max-w-xl">
          <Skeleton className="h-4 w-full rounded" />
          <Skeleton className="h-4 w-5/6 rounded" />
        </div>
      ) : (
        <p className="max-w-xl text-sm text-muted-foreground">
          {DOWNLOAD_HERO_CONTENT.description}
        </p>
      )}

      {/* Primary & Universal download buttons */}
      {loading || !latest ? (
        <div className="flex flex-wrap items-center justify-center gap-4">
          <Skeleton className="h-11 w-48 rounded-md" />
          <Skeleton className="h-11 w-48 rounded-md" />
        </div>
      ) : (() => {
        const assets = latest.assets || []
        let recommendedAsset = assets.find(asset => asset.name.toLowerCase().includes(arch))
        if (!recommendedAsset) {
          recommendedAsset = assets.find(asset => asset.name.toLowerCase().includes("universal"))
        }
        if (!recommendedAsset && assets.length > 0) {
          recommendedAsset = assets[0]
        }

        const universalAsset = assets.find(asset => asset.name.toLowerCase().includes("universal"))

        return (
          <div className="flex flex-wrap items-center justify-center gap-4">
            {recommendedAsset && (
              <a
                href={recommendedAsset.downloadUrl}
                target="_blank"
                rel="noopener noreferrer"
              >
                <Button size="lg" className="shadow-md">
                  <Download data-icon="inline-start" />
                  {DOWNLOAD_HERO_CONTENT.buttonLabel} ({recommendedAsset.sizeLabel})
                </Button>
              </a>
            )}

            {universalAsset && universalAsset.downloadUrl !== recommendedAsset?.downloadUrl && (
              <a
                href={universalAsset.downloadUrl}
                target="_blank"
                rel="noopener noreferrer"
              >
                <Button size="lg" variant="outline">
                  <Download data-icon="inline-start" />
                  Download Universal ({universalAsset.sizeLabel})
                </Button>
              </a>
            )}
          </div>
        )
      })()}

      {/* System requirements */}
      <div className="flex flex-wrap items-center justify-center gap-2">
        {loading ? (
          Array.from({ length: 3 }).map((_, idx) => (
            <Skeleton key={idx} className="h-4 w-24 rounded" />
          ))
        ) : (
          SYSTEM_REQUIREMENTS.map((req) => (
            <div key={req.label} className="flex items-center gap-1.5">
              <Smartphone className="size-3 text-muted-foreground" />
              <span className="text-xs text-muted-foreground">{req.label}</span>
            </div>
          ))
        )}
      </div>
    </section>
  )
}

