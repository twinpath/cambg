import { Badge } from "@/components/ui/badge"
import { ABOUT_HERO_CONTENT } from "@/data/about"

export function AboutHero() {
  return (
    <section className="flex flex-col items-center gap-6 px-4 py-20 text-center">
      <Badge variant="secondary">{ABOUT_HERO_CONTENT.badge}</Badge>

      <h1 className="font-heading max-w-2xl text-3xl font-bold tracking-tight md:text-5xl">
        {ABOUT_HERO_CONTENT.heading}
      </h1>

      {ABOUT_HERO_CONTENT.paragraphs.map((p, idx) => (
        <p key={idx} className="max-w-xl text-sm text-muted-foreground md:text-base">
          {p}
        </p>
      ))}
    </section>
  )
}


