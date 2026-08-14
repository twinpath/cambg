import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
  CardDescription,
} from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Separator } from "@/components/ui/separator"
import { GITHUB_URL, GITHUB_RELEASES_URL } from "@/data/site"
import { PROJECT_INFO_CONTENT } from "@/data/about"
import {
  ExternalLink,
} from "lucide-react"
import { Skeleton } from "@/components/ui/skeleton"
import { useLoading } from "@/hooks/use-loading"

export function ProjectInfoSection() {
  const loading = useLoading()

  return (
    <section id="license" className="px-4 py-16">
      <div className="mx-auto max-w-5xl">
        <Separator className="mb-16" />
        <div className="mb-10 flex flex-col items-center gap-2 text-center">
          {loading ? (
            <>
              <Skeleton className="h-8 w-48 rounded" />
              <Skeleton className="h-4 w-64 rounded mt-1" />
            </>
          ) : (
            <>
              <h2 className="font-heading text-2xl font-bold tracking-tight">
                {PROJECT_INFO_CONTENT.heading}
              </h2>
              <p className="max-w-lg text-sm text-muted-foreground">
                {PROJECT_INFO_CONTENT.description}
              </p>
            </>
          )}
        </div>

        <div className="grid gap-4 sm:grid-cols-3">
          {loading
            ? Array.from({ length: 3 }).map((_, idx) => (
                <Card key={idx}>
                  <CardHeader className="gap-2">
                    <Skeleton className="h-5 w-5 rounded" />
                    <Skeleton className="h-5 w-24 rounded" />
                  </CardHeader>
                  <CardContent>
                    <Skeleton className="h-4 w-full rounded" />
                  </CardContent>
                </Card>
              ))
            : PROJECT_INFO_CONTENT.items.map((info) => (
                <Card key={info.title}>
                  <CardHeader>
                    <info.icon className="mb-1 size-5 text-primary" />
                    <CardTitle>{info.title}</CardTitle>
                  </CardHeader>
                  <CardContent>
                    <CardDescription>{info.description}</CardDescription>
                  </CardContent>
                </Card>
              ))}
        </div>

        {/* Repository links */}
        <div className="mt-8 flex flex-wrap items-center justify-center gap-3">
          {loading ? (
            <>
              <Skeleton className="h-4 w-20 rounded" />
              <span className="text-muted-foreground">|</span>
              <Skeleton className="h-4 w-24 rounded" />
              <span className="text-muted-foreground">|</span>
              <Skeleton className="h-5 w-16 rounded" />
            </>
          ) : (
            <>
              <a
                href={GITHUB_URL}
                target="_blank"
                rel="noopener noreferrer"
                className="inline-flex items-center gap-1.5 text-xs text-muted-foreground transition-colors hover:text-foreground"
              >
                <ExternalLink className="size-3" />
                {PROJECT_INFO_CONTENT.labels.sourceCode}
              </a>
              <span className="text-muted-foreground">|</span>
              <a
                href={GITHUB_RELEASES_URL}
                target="_blank"
                rel="noopener noreferrer"
                className="inline-flex items-center gap-1.5 text-xs text-muted-foreground transition-colors hover:text-foreground"
              >
                <ExternalLink className="size-3" />
                {PROJECT_INFO_CONTENT.labels.githubReleases}
              </a>
              <span className="text-muted-foreground">|</span>
              <Badge variant="outline">{PROJECT_INFO_CONTENT.badge}</Badge>
            </>
          )}
        </div>
      </div>
    </section>
  )
}

