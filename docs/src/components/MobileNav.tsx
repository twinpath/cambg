import { useState } from "react"
import { Button } from "@/components/ui/button"
import {
  Sheet,
  SheetContent,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from "@/components/ui/sheet"
import { Separator } from "@/components/ui/separator"
import { Menu } from "lucide-react"
import { NAV_LINKS, SITE_NAME } from "@/data/site"
import type { MobileNavProps } from "@/types/navigation"

export function MobileNav({ currentPath }: MobileNavProps) {
  const [open, setOpen] = useState(false)

  return (
    <Sheet open={open} onOpenChange={setOpen}>
      <SheetTrigger asChild>
        <Button variant="ghost" size="icon" aria-label="Open menu">
          <Menu />
        </Button>
      </SheetTrigger>
      <SheetContent side="left">
        <SheetHeader>
          <SheetTitle>{SITE_NAME}</SheetTitle>
        </SheetHeader>
        <Separator />
        <nav className="flex flex-col gap-1 p-4">
          {NAV_LINKS.map((link) => {
            const isActive = currentPath === link.href
            return (
              <a key={link.href} href={link.href} onClick={() => setOpen(false)}>
                <Button
                  variant={isActive ? "secondary" : "ghost"}
                  className="w-full justify-start"
                >
                  {link.label}
                </Button>
              </a>
            )
          })}
        </nav>
      </SheetContent>
    </Sheet>
  )
}
