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
import {
  Scale,
  GitBranch,
  Wrench,
  ExternalLink,
} from "lucide-react"

const PROJECT_INFO = [
  {
    icon: Scale,
    title: "License",
    description:
      "This project is proprietary software. All rights reserved. Unauthorized copying, modification, distribution, or use is strictly prohibited without prior written consent.",
  },
  {
    icon: GitBranch,
    title: "Versioning",
    description:
      "Follows Semantic Versioning 2.0.0. Releases include Stable (vX.Y.Z), Beta (vX.Y.Z-beta.N), Alpha (vX.Y.Z-alpha.N), and Test (vX.Y.Z-test.N) variants.",
  },
  {
    icon: Wrench,
    title: "Build System",
    description:
      "Built with Gradle (Kotlin DSL) and Android Gradle Plugin. Releases are automated via GitHub Actions with Gemini API-powered changelog generation.",
  },
] as const

export function ProjectInfoSection() {
  return (
    <section id="license" className="px-4 py-16">
      <div className="mx-auto max-w-5xl">
        <Separator className="mb-16" />
        <div className="mb-10 flex flex-col items-center gap-2 text-center">
          <h2 className="font-heading text-2xl font-bold tracking-tight">
            Project Information
          </h2>
          <p className="max-w-lg text-sm text-muted-foreground">
            Technical details about the CamBG Record project, its licensing, and
            development infrastructure.
          </p>
        </div>

        <div className="grid gap-4 sm:grid-cols-3">
          {PROJECT_INFO.map((info) => (
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
          <a
            href={GITHUB_URL}
            target="_blank"
            rel="noopener noreferrer"
            className="inline-flex items-center gap-1.5 text-xs text-muted-foreground transition-colors hover:text-foreground"
          >
            <ExternalLink className="size-3" />
            Source Code
          </a>
          <span className="text-muted-foreground">|</span>
          <a
            href={GITHUB_RELEASES_URL}
            target="_blank"
            rel="noopener noreferrer"
            className="inline-flex items-center gap-1.5 text-xs text-muted-foreground transition-colors hover:text-foreground"
          >
            <ExternalLink className="size-3" />
            GitHub Releases
          </a>
          <span className="text-muted-foreground">|</span>
          <Badge variant="outline">Proprietary License</Badge>
        </div>
      </div>
    </section>
  )
}
