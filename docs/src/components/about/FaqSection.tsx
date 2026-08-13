import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from "@/components/ui/accordion"
import { FAQ_CONTENT } from "@/data/about"
import { Skeleton } from "@/components/ui/skeleton"
import { useLoading } from "@/hooks/use-loading"

export function FaqSection() {
  const loading = useLoading()

  return (
    <section id="faq" className="px-4 py-16">
      <div className="mx-auto max-w-3xl">
        <div className="mb-10 flex flex-col items-center gap-2 text-center">
          {loading ? (
            <>
              <Skeleton className="h-8 w-48 rounded" />
              <Skeleton className="h-4 w-64 rounded mt-1" />
            </>
          ) : (
            <>
              <h2 className="font-heading text-2xl font-bold tracking-tight">
                {FAQ_CONTENT.heading}
              </h2>
              <p className="max-w-lg text-sm text-muted-foreground">
                {FAQ_CONTENT.description}
              </p>
            </>
          )}
        </div>

        {loading ? (
          <div className="space-y-2">
            {Array.from({ length: 3 }).map((_, idx) => (
              <div key={idx} className="border rounded-lg p-4 flex items-center justify-between">
                <Skeleton className="h-5 w-3/4 rounded" />
                <Skeleton className="h-4 w-4 rounded" />
              </div>
            ))}
          </div>
        ) : (
          <Accordion type="single" collapsible>
            {FAQ_CONTENT.items.map((item) => (
              <AccordionItem key={item.question} value={item.question}>
                <AccordionTrigger>{item.question}</AccordionTrigger>
                <AccordionContent>
                  <p>{item.answer}</p>
                </AccordionContent>
              </AccordionItem>
            ))}
          </Accordion>
        )}
      </div>
    </section>
  )
}

