import {
  Card,
  CardHeader,
  CardTitle,
  CardDescription,
  CardContent,
} from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Separator } from "@/components/ui/separator"
import { Monitor, Cpu, Database } from "lucide-react"

const ARCHITECTURE_LAYERS = [
  {
    icon: Monitor,
    title: "Presentation Layer",
    description: "UI screens and ViewModels managing application state.",
    components: [
      "CameraScreen",
      "GalleryScreen",
      "DetectionScreen",
      "SettingsScreen",
      "CameraViewModel",
      "GalleryViewModel",
      "DetectionViewModel",
      "SettingsViewModel",
    ],
  },
  {
    icon: Cpu,
    title: "Domain Layer",
    description:
      "Business logic and service orchestration using StateFlow and Coroutines.",
    components: [
      "BackgroundRecordingService",
      "CameraXRecordingManager",
    ],
  },
  {
    icon: Database,
    title: "Data Layer",
    description: "Persistence and local storage for application data.",
    components: [
      "Room Database (VideoItem)",
      "DataStore (AppSettings)",
    ],
  },
] as const

export function ArchitectureSection() {
  return (
    <section id="architecture" className="px-4 py-16">
      <div className="mx-auto max-w-5xl">
        <div className="mb-10 flex flex-col items-center gap-2 text-center">
          <h2 className="font-heading text-2xl font-bold tracking-tight">
            Architecture
          </h2>
          <p className="max-w-lg text-sm text-muted-foreground">
            CamBG Record follows the MVVM (Model-View-ViewModel) pattern,
            cleanly separating concerns across three layers.
          </p>
        </div>

        <div className="flex flex-col gap-4">
          {ARCHITECTURE_LAYERS.map((layer, index) => (
            <div key={layer.title} className="flex flex-col gap-4">
              <Card>
                <CardHeader>
                  <div className="flex items-center gap-3">
                    <layer.icon className="size-5 text-primary" />
                    <div>
                      <CardTitle>{layer.title}</CardTitle>
                      <CardDescription>{layer.description}</CardDescription>
                    </div>
                  </div>
                </CardHeader>
                <CardContent>
                  <div className="flex flex-wrap gap-1.5">
                    {layer.components.map((comp) => (
                      <Badge key={comp} variant="outline">
                        {comp}
                      </Badge>
                    ))}
                  </div>
                </CardContent>
              </Card>
              {index < ARCHITECTURE_LAYERS.length - 1 && (
                <div className="flex justify-center">
                  <div className="h-6 w-px bg-border" />
                </div>
              )}
            </div>
          ))}
        </div>
      </div>
    </section>
  )
}
