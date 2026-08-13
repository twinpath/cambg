import React from "react"
import { Button } from "@/components/ui/button"
import { Skeleton } from "@/components/ui/skeleton"
import { useLoading } from "@/hooks/use-loading"
import { AlertTriangle, ServerCrash, Home } from "lucide-react"

interface ErrorViewProps {
  statusCode: number
  title: string
  description: string
}

export function ErrorView({ statusCode, title, description }: ErrorViewProps) {
  const loading = useLoading(500) // 500ms delay for skeleton transition

  return (
    <section className="flex flex-col items-center justify-center gap-6 px-4 py-20 text-center">
      {/* Icon Area */}
      <div className="flex items-center justify-center size-20 rounded-full bg-muted/50 border border-border">
        {loading ? (
          <Skeleton className="size-10 rounded-full" />
        ) : statusCode === 404 ? (
          <AlertTriangle className="size-10 text-yellow-500" />
        ) : (
          <ServerCrash className="size-10 text-destructive" />
        )}
      </div>

      {/* Status Code Badge */}
      {loading ? (
        <Skeleton className="h-6 w-16 rounded" />
      ) : (
        <span className="font-mono text-sm font-semibold tracking-wider text-primary uppercase">
          Error {statusCode}
        </span>
      )}

      {/* Heading */}
      {loading ? (
        <div className="flex flex-col items-center gap-2 w-full max-w-2xl">
          <Skeleton className="h-10 w-3/4 rounded md:h-12" />
          <Skeleton className="h-10 w-1/2 rounded md:h-12" />
        </div>
      ) : (
        <h1 className="font-heading max-w-2xl text-3xl font-bold tracking-tight md:text-5xl">
          {title}
        </h1>
      )}

      {/* Description */}
      {loading ? (
        <div className="flex flex-col items-center gap-2 w-full max-w-xl">
          <Skeleton className="h-4 w-full rounded" />
          <Skeleton className="h-4 w-5/6 rounded" />
        </div>
      ) : (
        <p className="max-w-xl text-sm text-muted-foreground md:text-base">
          {description}
        </p>
      )}

      {/* Call to action */}
      {loading ? (
        <Skeleton className="h-11 w-44 rounded-md" />
      ) : (
        <a href={import.meta.env.BASE_URL}>
          <Button size="lg">
            <Home data-icon="inline-start" />
            Return Home
          </Button>
        </a>
      )}
    </section>
  )
}
