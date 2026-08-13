import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from "@/components/ui/card"
import { Separator } from "@/components/ui/separator"
import {
  Download,
  Shield,
  Settings,
  CheckCircle,
} from "lucide-react"

const INSTALL_STEPS = [
  {
    icon: Download,
    title: "Download the APK",
    description:
      "Tap the download button above or select a specific version from the Release Archive. The APK file will be saved to your device's Downloads folder.",
  },
  {
    icon: Shield,
    title: "Enable Unknown Sources",
    description:
      "Navigate to Settings and then Security and then Install Unknown Apps. Select your browser or file manager and enable 'Allow from this source' to permit sideloading.",
  },
  {
    icon: Settings,
    title: "Install the Application",
    description:
      "Open the downloaded APK file using your file manager. Tap 'Install' when prompted by the system package installer. The process typically completes within seconds.",
  },
  {
    icon: CheckCircle,
    title: "Grant Permissions",
    description:
      "On first launch, grant the required permissions: Camera, Microphone, Storage, and Notification access. The app will guide you through each permission request.",
  },
] as const

export function InstallGuide() {
  return (
    <section className="px-4 py-16">
      <div className="mx-auto max-w-5xl">
        <Separator className="mb-16" />
        <div className="mb-10 flex flex-col items-center gap-2 text-center">
          <h2 className="font-heading text-2xl font-bold tracking-tight">
            Installation Guide
          </h2>
          <p className="max-w-lg text-sm text-muted-foreground">
            Follow these steps to install CamBG Record on your Android device
            via direct APK sideloading.
          </p>
        </div>

        <div className="grid gap-4 sm:grid-cols-2">
          {INSTALL_STEPS.map((step, index) => (
            <Card key={step.title}>
              <CardHeader>
                <div className="flex items-center gap-3">
                  <span className="flex size-6 shrink-0 items-center justify-center rounded-full bg-primary text-xs font-medium text-primary-foreground">
                    {index + 1}
                  </span>
                  <CardTitle>{step.title}</CardTitle>
                </div>
              </CardHeader>
              <CardContent>
                <div className="flex items-start gap-2">
                  <step.icon className="mt-0.5 size-3.5 shrink-0 text-muted-foreground" />
                  <p className="text-xs text-muted-foreground">
                    {step.description}
                  </p>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      </div>
    </section>
  )
}
