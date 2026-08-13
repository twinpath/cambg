import {
  Card,
  CardHeader,
  CardTitle,
  CardDescription,
} from "@/components/ui/card"
import {
  Video,
  Settings,
  Radar,
  User,
  Palette,
  BatteryCharging,
} from "lucide-react"

const FEATURES = [
  {
    icon: Video,
    title: "Background Recording",
    description:
      "Persistent foreground service maintains recording continuity even when the screen is locked or other apps are in the foreground.",
  },
  {
    icon: Settings,
    title: "Camera Configuration",
    description:
      "Configurable resolution, frame rate, bitrate presets, and audio capture with CameraX hardware abstraction.",
  },
  {
    icon: Radar,
    title: "Motion Detection",
    description:
      "Frame-differencing analysis with adjustable sensitivity levels to flag movement events during active recording sessions.",
  },
  {
    icon: User,
    title: "Person Detection",
    description:
      "Confidence-threshold-based human presence identification, configurable from 50% to 95% precision.",
  },
  {
    icon: Palette,
    title: "Material Design 3",
    description:
      "Full Material You implementation with dynamic color extraction from device wallpaper and system-follow theming.",
  },
  {
    icon: BatteryCharging,
    title: "Battery Optimization",
    description:
      "Requests exemption from battery optimization constraints to prevent service interruption on long-duration sessions.",
  },
] as const

export function FeaturesSection() {
  return (
    <section id="features" className="px-4 py-16">
      <div className="mx-auto max-w-5xl">
        <div className="mb-10 flex flex-col items-center gap-2 text-center">
          <h2 className="font-heading text-2xl font-bold tracking-tight">
            Core Capabilities
          </h2>
          <p className="max-w-lg text-sm text-muted-foreground">
            Purpose-built for scenarios that demand uninterrupted recording --
            security monitoring, evidence capture, field documentation, and
            hands-free content creation.
          </p>
        </div>

        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {FEATURES.map((feature) => (
            <Card key={feature.title}>
              <CardHeader>
                <feature.icon className="mb-1 size-5 text-primary" />
                <CardTitle>{feature.title}</CardTitle>
                <CardDescription>{feature.description}</CardDescription>
              </CardHeader>
            </Card>
          ))}
        </div>
      </div>
    </section>
  )
}
