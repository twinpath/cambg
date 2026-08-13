import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from "@/components/ui/card"
import { Separator } from "@/components/ui/separator"
import { INSTALL_GUIDE_CONTENT } from "@/data/download"

export function InstallGuide() {
  return (
    <section className="px-4 py-16">
      <div className="mx-auto max-w-5xl">
        <Separator className="mb-16" />
        <div className="mb-10 flex flex-col items-center gap-2 text-center">
          <h2 className="font-heading text-2xl font-bold tracking-tight">
            {INSTALL_GUIDE_CONTENT.heading}
          </h2>
          <p className="max-w-lg text-sm text-muted-foreground">
            {INSTALL_GUIDE_CONTENT.description}
          </p>
        </div>

        <div className="grid gap-4 sm:grid-cols-2">
          {INSTALL_GUIDE_CONTENT.steps.map((step, index) => (
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
