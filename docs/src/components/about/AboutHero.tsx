import { Badge } from "@/components/ui/badge"
import { ABOUT_HERO_CONTENT } from "@/data/about"
import { Skeleton } from "@/components/ui/skeleton"
import { useLoading } from "@/hooks/use-loading"

export function AboutHero() {
  const loading = useLoading()

  return (
    <section className="flex flex-col items-center gap-6 px-4 py-20 text-center">
      {loading ? (
        <Skeleton className="h-6 w-20 rounded" />
      ) : (
        <Badge variant="secondary">{ABOUT_HERO_CONTENT.badge}</Badge>
      )}

      {loading ? (
        <div className="flex flex-col items-center gap-2 w-full max-w-2xl">
          <Skeleton className="h-10 w-3/4 rounded md:h-12" />
          <Skeleton className="h-10 w-1/2 rounded md:h-12" />
        </div>
      ) : (
        <h1 className="font-heading max-w-2xl text-3xl font-bold tracking-tight md:text-5xl">
          {ABOUT_HERO_CONTENT.heading}
        </h1>
      )}

      {loading ? (
        <div className="flex flex-col items-center gap-2 w-full max-w-xl">
          <Skeleton className="h-4 w-full rounded" />
          <Skeleton className="h-4 w-5/6 rounded" />
          <Skeleton className="h-4 w-4/5 rounded" />
        </div>
      ) : (
        ABOUT_HERO_CONTENT.paragraphs.map((p, idx) => (
          <p key={idx} className="max-w-xl text-sm text-muted-foreground md:text-base">
            {p}
          </p>
        ))
      )}
    </section>
  )
}


