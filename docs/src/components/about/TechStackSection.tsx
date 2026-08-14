import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table"
import { Badge } from "@/components/ui/badge"
import { TECH_STACK_CONTENT } from "@/data/about"
import { Skeleton } from "@/components/ui/skeleton"
import { useLoading } from "@/hooks/use-loading"
import { useTechStack } from "@/hooks/use-tech-stack"

export function TechStackSection() {
  const uiLoading = useLoading()
  const { techStack, loading: apiLoading } = useTechStack()
  const loading = uiLoading || apiLoading

  if (!loading && techStack.length === 0) {
    return null
  }

  return (
    <section className="px-4 py-16">
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
                {TECH_STACK_CONTENT.heading}
              </h2>
              <p className="max-w-lg text-sm text-muted-foreground">
                {TECH_STACK_CONTENT.description}
              </p>
            </>
          )}
        </div>

        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>{TECH_STACK_CONTENT.tableHeaders.layer}</TableHead>
              <TableHead>{TECH_STACK_CONTENT.tableHeaders.technology}</TableHead>
              <TableHead>{TECH_STACK_CONTENT.tableHeaders.version}</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {loading
              ? Array.from({ length: 4 }).map((_, idx) => (
                  <TableRow key={idx}>
                    <TableCell>
                      <Skeleton className="h-5 w-16 rounded" />
                    </TableCell>
                    <TableCell>
                      <Skeleton className="h-4 w-28 rounded" />
                    </TableCell>
                    <TableCell>
                      <Skeleton className="h-4 w-12 rounded" />
                    </TableCell>
                  </TableRow>
                ))
              : techStack.map((row) => (
                  <TableRow key={row.layer}>
                    <TableCell>
                      <Badge variant="outline">{row.layer}</Badge>
                    </TableCell>
                    <TableCell>{row.technology}</TableCell>
                    <TableCell className="font-mono">
                      {row.version}
                    </TableCell>
                  </TableRow>
                ))}
          </TableBody>
        </Table>
      </div>
    </section>
  )
}
