import { Badge } from "@/components/ui/badge"

export function AboutHero() {
  return (
    <section className="flex flex-col items-center gap-6 px-4 py-20 text-center">
      <Badge variant="secondary">Open Source Project</Badge>

      <h1 className="font-heading max-w-2xl text-3xl font-bold tracking-tight md:text-5xl">
        About CamBG Record
      </h1>

      <p className="max-w-xl text-sm text-muted-foreground md:text-base">
        CamBG Record is a purpose-built Android application that enables
        continuous video recording in the background, operating seamlessly even
        when the device screen is locked or other applications are in the
        foreground.
      </p>

      <p className="max-w-xl text-sm text-muted-foreground">
        Designed for scenarios that demand uninterrupted recording capabilities
        -- security monitoring, evidence capture, field documentation, and
        hands-free content creation -- all wrapped in a polished Material Design
        3 interface with dynamic theming support.
      </p>
    </section>
  )
}
