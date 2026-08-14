import {
  Card,
  CardHeader,
  CardTitle,
  CardDescription,
} from "@/components/ui/card"
import { FEATURES_CONTENT } from "@/data/home"

export function FeaturesSection() {
  return (
    <section id="features" className="px-4 py-16">
      <div className="mx-auto max-w-5xl">
        <div className="mb-10 flex flex-col items-center gap-2 text-center">
          <h2 className="font-heading text-2xl font-bold tracking-tight">
            {FEATURES_CONTENT.heading}
          </h2>
          <p className="max-w-lg text-sm text-muted-foreground">
            {FEATURES_CONTENT.description}
          </p>
        </div>

        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {FEATURES_CONTENT.items.map((feature) => (
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

