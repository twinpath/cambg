import { Button } from "@/components/ui/button"
import { Separator } from "@/components/ui/separator"
import { GITHUB_URL } from "@/data/site"
import { CTA_CONTENT } from "@/data/home"
import { Download, GitFork } from "lucide-react"

export function CtaSection() {
  return (
    <section className="px-4 py-16">
      <Separator className="mx-auto mb-16 max-w-5xl" />
      <div className="mx-auto flex max-w-2xl flex-col items-center gap-4 text-center">
        <h2 className="font-heading text-2xl font-bold tracking-tight">
          {CTA_CONTENT.heading}
        </h2>
        <p className="text-sm text-muted-foreground">
          {CTA_CONTENT.description}
        </p>

        <div className="flex flex-wrap items-center justify-center gap-3">
          <a href={`${import.meta.env.BASE_URL}download`}>
            <Button size="lg">
              <Download data-icon="inline-start" />
              {CTA_CONTENT.buttons.download}
            </Button>
          </a>
          <a href={GITHUB_URL} target="_blank" rel="noopener noreferrer">
            <Button variant="outline" size="lg">
              <GitFork data-icon="inline-start" />
              {CTA_CONTENT.buttons.viewSource}
            </Button>
          </a>
        </div>
      </div>
    </section>
  )
}


