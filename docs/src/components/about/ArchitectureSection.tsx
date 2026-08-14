import {
  Card,
  CardHeader,
  CardTitle,
  CardDescription,
  CardContent,
} from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { ARCHITECTURE_CONTENT } from "@/data/about"

export function ArchitectureSection() {
  return (
    <section id="architecture" className="px-4 py-16">
      <div className="mx-auto max-w-5xl">
        <div className="mb-10 flex flex-col items-center gap-2 text-center">
          <h2 className="font-heading text-2xl font-bold tracking-tight">
            {ARCHITECTURE_CONTENT.heading}
          </h2>
          <p className="max-w-lg text-sm text-muted-foreground">
            {ARCHITECTURE_CONTENT.description}
          </p>
        </div>

        <div className="flex flex-col gap-4">
          {ARCHITECTURE_CONTENT.layers.map((layer, index) => (
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
              {index < ARCHITECTURE_CONTENT.layers.length - 1 && (
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

