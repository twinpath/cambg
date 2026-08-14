import { useState } from "react"
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
import { RELEASE_ARCHIVE_CONTENT } from "@/data/download"
import { Download, Copy, Check } from "lucide-react"
import { getArchitecture } from "@/lib/architecture"
import { Skeleton } from "@/components/ui/skeleton"
import { useLoading } from "@/hooks/use-loading"

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

function CopyButton({ text }: { text: string }) {
  const [copied, setCopied] = useState(false)

  const handleCopy = async () => {
    try {
      await navigator.clipboard.writeText(text)
      setCopied(true)
      setTimeout(() => setCopied(false), 2000)
    } catch (err) {
      console.error("Failed to copy", err)
    }
  }

  return (
    <Button variant="ghost" size="xs" onClick={handleCopy} className="size-6 p-0">
      {copied ? <Check className="size-3 text-green-500" /> : <Copy className="size-3" />}
    </Button>
  )
}

export function ReleaseArchive({ releases, isLoading }: { releases: Release[]; isLoading: boolean }) {
  const [visibleCount, setVisibleCount] = useState(5)
  const visibleReleases = releases.slice(0, visibleCount)
  const loading = isLoading

  return (
    <section id="archive" className="px-4 py-16">
      <div className="mx-auto max-w-5xl">
        <div className="mb-10 flex flex-col items-center gap-2 text-center">
          {loading ? (
            <>
              <Skeleton className="h-8 w-48 rounded" />
              <Skeleton className="h-4 w-64 rounded mt-1" />
            </>
          ) : (
            <>
              <h2 className="font-heading text-2xl font-bold tracking-tight">
                {RELEASE_ARCHIVE_CONTENT.heading}
              </h2>
              <p className="max-w-lg text-sm text-muted-foreground">
                {RELEASE_ARCHIVE_CONTENT.description}
              </p>
            </>
          )}
        </div>

        {loading ? (
          <div className="space-y-2">
            {Array.from({ length: 3 }).map((_, idx) => (
              <div key={idx} className="border rounded-lg p-4 flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <Skeleton className="h-4 w-12 rounded" />
                  <Skeleton className="h-5 w-16 rounded" />
                  <Skeleton className="h-4 w-24 rounded" />
                </div>
                <Skeleton className="h-4 w-4 rounded" />
              </div>
            ))}
          </div>
        ) : (
          <Accordion type="single" collapsible className="space-y-2">
            {visibleReleases.map((release) => (
              <AccordionItem 
                key={release.version} 
                value={release.version}
                className="transition-all duration-300 animate-in fade-in slide-in-from-bottom-2"
              >
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
                    {/* Assets table */}
                    <Table>
                      <TableHeader>
                        <TableRow>
                          <TableHead>{RELEASE_ARCHIVE_CONTENT.tableHeaders.file}</TableHead>
                          <TableHead>{RELEASE_ARCHIVE_CONTENT.tableHeaders.architecture}</TableHead>
                          <TableHead>{RELEASE_ARCHIVE_CONTENT.tableHeaders.size}</TableHead>
                          <TableHead>SHA-256</TableHead>
                          <TableHead className="w-24" />
                        </TableRow>
                      </TableHeader>
                      <TableBody>
                        {release.assets.map((asset) => (
                          <TableRow key={asset.name}>
                            <TableCell className="font-mono text-xs">
                              {asset.name}
                            </TableCell>
                            <TableCell className="text-xs text-muted-foreground">
                              {asset.architecture || getArchitecture(asset.name)}
                            </TableCell>
                            <TableCell className="text-xs">{asset.sizeLabel}</TableCell>
                            <TableCell className="max-w-[180px]">
                              {asset.sha256 ? (
                                <div className="flex items-center gap-1.5">
                                  <span className="truncate font-mono text-[10px] text-muted-foreground" title={asset.sha256}>
                                    {asset.sha256.substring(0, 8)}...{asset.sha256.substring(asset.sha256.length - 8)}
                                  </span>
                                  <CopyButton text={asset.sha256} />
                                </div>
                              ) : (
                                <span className="text-[10px] text-muted-foreground italic">N/A</span>
                              )}
                            </TableCell>
                            <TableCell>
                              <a
                                href={asset.downloadUrl}
                                target="_blank"
                                rel="noopener noreferrer"
                              >
                                <Button variant="ghost" size="xs">
                                  <Download className="size-3" />
                                  {RELEASE_ARCHIVE_CONTENT.buttons.download}
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
        )}

        {!loading && releases.length > visibleCount && (
          <div className="mt-8 flex justify-center">
            <Button 
              variant="outline"
              onClick={() => setVisibleCount((prev) => prev + 5)}
              className="min-w-32"
            >
              {RELEASE_ARCHIVE_CONTENT.buttons.loadMore}
            </Button>
          </div>
        )}
      </div>
    </section>
  )
}


