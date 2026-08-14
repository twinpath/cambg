import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from "@/components/ui/accordion"
import { FAQ_CONTENT } from "@/data/about"

export function FaqSection() {
  return (
    <section id="faq" className="px-4 py-16">
      <div className="mx-auto max-w-3xl">
        <div className="mb-10 flex flex-col items-center gap-2 text-center">
          <h2 className="font-heading text-2xl font-bold tracking-tight">
            {FAQ_CONTENT.heading}
          </h2>
          <p className="max-w-lg text-sm text-muted-foreground">
            {FAQ_CONTENT.description}
          </p>
        </div>

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
      </div>
    </section>
  )
}

