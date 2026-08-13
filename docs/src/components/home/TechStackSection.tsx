import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table"
import { Badge } from "@/components/ui/badge"

const TECH_STACK = [
  { layer: "Language", technology: "Kotlin", version: "2.2.10" },
  {
    layer: "UI Framework",
    technology: "Jetpack Compose + Material 3",
    version: "BOM 2024.09.00",
  },
  {
    layer: "Build System",
    technology: "Gradle (Kotlin DSL)",
    version: "AGP 9.1.1",
  },
  {
    layer: "Camera",
    technology: "CameraX (Camera2, Video, View, Lifecycle)",
    version: "1.5.0",
  },
  {
    layer: "Navigation",
    technology: "Navigation Compose",
    version: "2.8.9",
  },
  { layer: "Persistence", technology: "Room Database", version: "2.7.0" },
  {
    layer: "Preferences",
    technology: "DataStore Preferences",
    version: "1.1.7",
  },
  {
    layer: "Networking",
    technology: "Retrofit + OkHttp + Moshi",
    version: "2.12.0 / 4.10.0 / 1.15.2",
  },
  {
    layer: "Image Loading",
    technology: "Coil Compose",
    version: "2.7.0",
  },
  {
    layer: "Concurrency",
    technology: "Kotlinx Coroutines",
    version: "1.10.2",
  },
  {
    layer: "Firebase",
    technology: "Firebase BOM (AI, App Check)",
    version: "34.15.0",
  },
] as const

export function TechStackSection() {
  return (
    <section className="px-4 py-16">
      <div className="mx-auto max-w-5xl">
        <div className="mb-10 flex flex-col items-center gap-2 text-center">
          <h2 className="font-heading text-2xl font-bold tracking-tight">
            Technology Stack
          </h2>
          <p className="max-w-lg text-sm text-muted-foreground">
            Built with modern Android development tools and libraries for
            maximum reliability and performance.
          </p>
        </div>

        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Layer</TableHead>
              <TableHead>Technology</TableHead>
              <TableHead>Version</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {TECH_STACK.map((row) => (
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
