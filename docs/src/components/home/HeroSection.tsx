import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { SITE_TAGLINE, SITE_DESCRIPTION } from "@/data/site"
import { HERO_CONTENT } from "@/data/home"
import { Download, ArrowRight } from "lucide-react"
import { Skeleton } from "@/components/ui/skeleton"
import { useLoading } from "@/hooks/use-loading"

export function HeroSection() {
  const loading = useLoading()

  return (
    <section className="flex flex-col items-center gap-6 px-4 py-20 text-center">
      {loading ? (
        <Skeleton className="h-6 w-20 rounded" />
      ) : (
        <Badge variant="secondary">{HERO_CONTENT.badge}</Badge>
      )}

      {loading ? (
        <div className="flex flex-col items-center gap-2 w-full max-w-2xl">
          <Skeleton className="h-10 w-3/4 rounded md:h-12" />
          <Skeleton className="h-10 w-1/2 rounded md:h-12" />
        </div>
      ) : (
        <h1 className="font-heading max-w-2xl text-3xl font-bold tracking-tight md:text-5xl">
          {SITE_TAGLINE}
        </h1>
      )}

      {loading ? (
        <div className="flex flex-col items-center gap-2 w-full max-w-xl">
          <Skeleton className="h-4 w-full rounded" />
          <Skeleton className="h-4 w-5/6 rounded" />
        </div>
      ) : (
        <p className="max-w-xl text-sm text-muted-foreground md:text-base">
          {SITE_DESCRIPTION}
        </p>
      )}

      <div className="flex flex-wrap items-center justify-center gap-3">
        {loading ? (
          <>
            <Skeleton className="h-11 w-36 rounded-md" />
            <Skeleton className="h-11 w-36 rounded-md" />
          </>
        ) : (
          <>
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
          </>
        )}
      </div>
    </section>
  )
}


