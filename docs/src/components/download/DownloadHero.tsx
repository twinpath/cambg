import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { SYSTEM_REQUIREMENTS } from "@/data/site"
import { DOWNLOAD_HERO_CONTENT } from "@/data/download"
import { Download, Smartphone } from "lucide-react"
import type { Release } from "@/data/releases"

export function DownloadHero({ latest }: { latest: Release }) {

  return (
    <section className="flex flex-col items-center gap-6 px-4 py-20 text-center">
      <div className="flex items-center gap-2">
        <Badge variant="secondary">{latest.version}</Badge>
        <Badge variant="outline">{latest.releaseType}</Badge>
      </div>

      <h1 className="font-heading max-w-2xl text-3xl font-bold tracking-tight md:text-5xl">
        {DOWNLOAD_HERO_CONTENT.heading}
      </h1>

      <p className="max-w-xl text-sm text-muted-foreground">
        {DOWNLOAD_HERO_CONTENT.description}
      </p>

      {/* Primary download */}
      {latest.assets.length > 0 && (
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
      )}


      {/* System requirements */}
      <div className="flex flex-wrap items-center justify-center gap-2">
        {SYSTEM_REQUIREMENTS.map((req) => (
          <div key={req.label} className="flex items-center gap-1.5">
            <Smartphone className="size-3 text-muted-foreground" />
            <span className="text-xs text-muted-foreground">{req.label}</span>
          </div>
        ))}
      </div>
    </section>
  )
}
