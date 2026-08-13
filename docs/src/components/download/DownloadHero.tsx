import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { SYSTEM_REQUIREMENTS } from "@/data/site"
import { DOWNLOAD_HERO_CONTENT } from "@/data/download"
import { Download, Smartphone } from "lucide-react"
import type { Release } from "@/data/releases"
import { Skeleton } from "@/components/ui/skeleton"
import { useLoading } from "@/hooks/use-loading"

export function DownloadHero({ latest }: { latest: Release }) {
  const loading = useLoading()

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

      {/* Primary download */}
      {loading || !latest ? (
        <Skeleton className="h-11 w-48 rounded-md" />
      ) : (
        latest.assets.length > 0 && (
          <a
            href={latest.assets[0].downloadUrl}
            target="_blank"
            rel="noopener noreferrer"
          >
            <Button size="lg">
              <Download data-icon="inline-start" />
              {DOWNLOAD_HERO_CONTENT.buttonLabel} ({latest.assets[0].sizeLabel})
            </Button>
          </a>
        )
      )}

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

