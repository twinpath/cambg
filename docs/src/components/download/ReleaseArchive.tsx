import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from "@/components/ui/accordion"
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import type { Release } from "@/data/releases"
import { Download, FileText } from "lucide-react"

const RELEASE_TYPE_VARIANT: Record<
  Release["releaseType"],
  "default" | "secondary" | "outline" | "destructive"
> = {
  stable: "default",
  beta: "secondary",
  alpha: "outline",
  test: "destructive",
}

function formatDate(dateStr: string) {
  return new Date(dateStr).toLocaleDateString("en-US", {
    year: "numeric",
    month: "short",
    day: "numeric",
  })
}

export function ReleaseArchive({ releases }: { releases: Release[] }) {
  return (
    <section id="archive" className="px-4 py-16">
      <div className="mx-auto max-w-5xl">
        <div className="mb-10 flex flex-col items-center gap-2 text-center">
          <h2 className="font-heading text-2xl font-bold tracking-tight">
            Release Archive
          </h2>
          <p className="max-w-lg text-sm text-muted-foreground">
            Browse all previous releases. Expand any version to view release
            notes and download specific build artifacts.
          </p>
        </div>

        <Accordion type="single" collapsible>
          {releases.map((release) => (
            <AccordionItem key={release.version} value={release.version}>
              <AccordionTrigger>
                <div className="flex items-center gap-3">
                  <span className="font-mono text-xs font-medium">
                    {release.version}
                  </span>
                  <Badge variant={RELEASE_TYPE_VARIANT[release.releaseType]}>
                    {release.releaseType}
                  </Badge>
                  <span className="text-xs text-muted-foreground">
                    {formatDate(release.date)}
                  </span>
                </div>
              </AccordionTrigger>
              <AccordionContent>
                <div className="flex flex-col gap-4">
                  {/* Release notes */}
                  <div className="flex items-start gap-2">
                    <FileText className="mt-0.5 size-3.5 shrink-0 text-muted-foreground" />
                    <p className="text-xs text-muted-foreground">
                      {release.notes}
                    </p>
                  </div>

                  {/* Assets table */}
                  <Table>
                    <TableHeader>
                      <TableRow>
                        <TableHead>File</TableHead>
                        <TableHead>Size</TableHead>
                        <TableHead className="w-24" />
                      </TableRow>
                    </TableHeader>
                    <TableBody>
                      {release.assets.map((asset) => (
                        <TableRow key={asset.name}>
                          <TableCell className="font-mono">
                            {asset.name}
                          </TableCell>
                          <TableCell>{asset.sizeLabel}</TableCell>
                          <TableCell>
                            <a
                              href={asset.downloadUrl}
                              target="_blank"
                              rel="noopener noreferrer"
                            >
                              <Button variant="ghost" size="xs">
                                <Download className="size-3" />
                                APK
                              </Button>
                            </a>
                          </TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                </div>
              </AccordionContent>
            </AccordionItem>
          ))}
        </Accordion>
      </div>
    </section>
  )
}
