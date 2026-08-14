import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { SITE_TAGLINE, SITE_DESCRIPTION } from "@/data/site"
import { HERO_CONTENT } from "@/data/home"
import { Download, ArrowRight } from "lucide-react"

export function HeroSection() {
  return (
    <section className="flex flex-col items-center gap-6 px-4 py-20 text-center">
      <Badge variant="secondary">{HERO_CONTENT.badge}</Badge>

      <h1 className="font-heading max-w-2xl text-3xl font-bold tracking-tight md:text-5xl">
        {SITE_TAGLINE}
      </h1>

      <p className="max-w-xl text-sm text-muted-foreground md:text-base">
        {SITE_DESCRIPTION}
      </p>

      <div className="flex flex-wrap items-center justify-center gap-3">
        <a href={`${import.meta.env.BASE_URL}download`}>
          <Button size="lg">
            <Download data-icon="inline-start" />
            {HERO_CONTENT.buttons.download}
          </Button>
        </a>
        <a href={`${import.meta.env.BASE_URL}about`}>
          <Button variant="outline" size="lg">
            {HERO_CONTENT.buttons.learnMore}
            <ArrowRight data-icon="inline-end" />
          </Button>
        </a>
      </div>
    </section>
  )
}


