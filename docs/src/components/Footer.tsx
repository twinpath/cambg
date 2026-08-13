import { Separator } from "@/components/ui/separator"
import {
  SITE_NAME,
  SITE_TAGLINE,
  GITHUB_URL,
  FOOTER_SECTIONS,
  FOOTER_CONTENT,
} from "@/data/site"
import { GitFork, ExternalLink } from "lucide-react"
import { Logo } from "@/components/Logo"
import { Skeleton } from "@/components/ui/skeleton"
import { useLoading } from "@/hooks/use-loading"

export function Footer() {
  const currentYear = new Date().getFullYear()
  const loading = useLoading()

  return (
    <footer className="border-t bg-muted/30">
      <div className="mx-auto max-w-5xl px-4 py-10">
        {/* Top section */}
        <div className="grid gap-8 md:grid-cols-4">
          {/* Brand column */}
          <div className="flex flex-col gap-3">
            {loading ? (
              <>
                <div className="flex items-center gap-2">
                  <Skeleton className="h-6 w-6 rounded-full" />
                  <Skeleton className="h-4 w-20 rounded" />
                </div>
                <Skeleton className="h-4 w-32 rounded" />
                <Skeleton className="h-4 w-24 rounded" />
              </>
            ) : (
              <>
                <div className="flex items-center gap-2">
                  <Logo className="size-6" />
                  <span className="font-heading text-sm font-medium">
                    {SITE_NAME}
                  </span>
                </div>
                <p className="text-xs text-muted-foreground">{SITE_TAGLINE}</p>
                <a
                  href={GITHUB_URL}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="inline-flex items-center gap-1.5 text-xs text-muted-foreground transition-colors hover:text-foreground"
                >
                  <GitFork className="size-3.5" />
                  {FOOTER_CONTENT.githubLink}
                </a>
              </>
            )}
          </div>

          {/* Link columns */}
          {loading
            ? Array.from({ length: 3 }).map((_, idx) => (
                <div key={idx} className="flex flex-col gap-2">
                  <Skeleton className="h-4 w-16 rounded" />
                  <div className="flex flex-col gap-1.5">
                    <Skeleton className="h-3.5 w-20 rounded" />
                    <Skeleton className="h-3.5 w-24 rounded" />
                    <Skeleton className="h-3.5 w-16 rounded" />
                  </div>
                </div>
              ))
            : FOOTER_SECTIONS.map((section) => (
                <div key={section.title} className="flex flex-col gap-2">
                  <span className="font-heading text-xs font-medium">
                    {section.title}
                  </span>
                  <ul className="flex flex-col gap-1.5">
                    {section.links.map((link) => (
                      <li key={link.label}>
                        <a
                          href={link.href}
                          {...("external" in link && link.external
                            ? { target: "_blank", rel: "noopener noreferrer" }
                            : {})}
                          className="inline-flex items-center gap-1 text-xs text-muted-foreground transition-colors hover:text-foreground"
                        >
                          {link.label}
                          {"external" in link && link.external && (
                            <ExternalLink className="size-2.5" />
                          )}
                        </a>
                      </li>
                    ))}
                  </ul>
                </div>
              ))}
        </div>

        <Separator className="my-6" />

        {/* Bottom section */}
        <div className="flex flex-col items-center justify-between gap-2 text-xs text-muted-foreground sm:flex-row">
          {loading ? (
            <>
              <Skeleton className="h-3.5 w-48 rounded" />
              <Skeleton className="h-3.5 w-32 rounded" />
            </>
          ) : (
            <>
              <span>
                &copy; {currentYear} {SITE_NAME}. {FOOTER_CONTENT.copyright}
              </span>
              <span>{FOOTER_CONTENT.brandingNote}</span>
            </>
          )}
        </div>
      </div>
    </footer>
  )
}


