import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table"
import { Badge } from "@/components/ui/badge"
import { TECH_STACK_CONTENT } from "@/data/home"

export function TechStackSection() {
  return (
    <section className="px-4 py-16">
      <div className="mx-auto max-w-5xl">
        <div className="mb-10 flex flex-col items-center gap-2 text-center">
          <h2 className="font-heading text-2xl font-bold tracking-tight">
            {TECH_STACK_CONTENT.heading}
          </h2>
          <p className="max-w-lg text-sm text-muted-foreground">
            {TECH_STACK_CONTENT.description}
          </p>
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
            {TECH_STACK_CONTENT.items.map((row) => (
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
