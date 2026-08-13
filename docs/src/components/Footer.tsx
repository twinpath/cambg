import { Separator } from "@/components/ui/separator"
import {
  SITE_NAME,
  SITE_TAGLINE,
  GITHUB_URL,
  FOOTER_SECTIONS,
} from "@/data/site"
import { GitFork, ExternalLink, Video } from "lucide-react"

export function Footer() {
  const currentYear = new Date().getFullYear()

  return (
    <footer className="border-t bg-muted/30">
      <div className="mx-auto max-w-5xl px-4 py-10">
        {/* Top section */}
        <div className="grid gap-8 md:grid-cols-4">
          {/* Brand column */}
          <div className="flex flex-col gap-3">
            <div className="flex items-center gap-2">
              <Video className="size-5 text-primary" />
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
              View on GitHub
            </a>
          </div>

          {/* Link columns */}
          {FOOTER_SECTIONS.map((section) => (
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
          <span>
            &copy; {currentYear} {SITE_NAME}. All rights reserved.
          </span>
          <span>Built with precision. Designed for reliability.</span>
        </div>
      </div>
    </footer>
  )
}
