import { Button } from "@/components/ui/button"
import { Separator } from "@/components/ui/separator"
import { GITHUB_URL } from "@/data/site"
import { Download, GitFork } from "lucide-react"

export function CtaSection() {
  return (
    <section className="px-4 py-16">
      <Separator className="mx-auto mb-16 max-w-5xl" />
      <div className="mx-auto flex max-w-2xl flex-col items-center gap-4 text-center">
        <h2 className="font-heading text-2xl font-bold tracking-tight">
          Ready to Get Started?
        </h2>
        <p className="text-sm text-muted-foreground">
          Download CamBG Record and experience persistent background video
          recording with intelligent detection on your Android device.
        </p>
        <div className="flex flex-wrap items-center justify-center gap-3">
          <a href="/download">
            <Button size="lg">
              <Download data-icon="inline-start" />
              Download Now
            </Button>
          </a>
          <a href={GITHUB_URL} target="_blank" rel="noopener noreferrer">
            <Button variant="outline" size="lg">
              <GitFork data-icon="inline-start" />
              View Source
            </Button>
          </a>
        </div>
      </div>
    </section>
  )
}
