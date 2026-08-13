import { Button } from "@/components/ui/button"
import { TooltipProvider } from "@/components/ui/tooltip"
import { ThemeToggle } from "@/components/ThemeToggle"
import { MobileNav } from "@/components/MobileNav"
import { NAV_LINKS, SITE_NAME } from "@/data/site"

interface NavbarProps {
  currentPath: string
}

export function Navbar({ currentPath }: NavbarProps) {
  return (
    <TooltipProvider>
      <header className="sticky top-0 z-50 w-full border-b bg-background/80 backdrop-blur-sm">
        <div className="mx-auto flex h-12 max-w-5xl items-center justify-between px-4">
          <div className="flex items-center gap-4">
            {/* Mobile menu */}
            <div className="md:hidden">
              <MobileNav currentPath={currentPath} />
            </div>

            {/* Logo + wordmark */}
            <a href={import.meta.env.BASE_URL} className="flex items-center gap-2">
              <img
                src={`${import.meta.env.BASE_URL}logo.svg`}
                alt={`${SITE_NAME} Logo`}
                className="size-5"
              />
              <span className="font-heading text-sm font-medium">
                {SITE_NAME}
              </span>
            </a>

            {/* Desktop navigation */}
            <nav className="hidden items-center gap-0.5 md:flex">
              {NAV_LINKS.map((link) => {
                const isActive = currentPath === link.href
                return (
                  <a key={link.href} href={link.href}>
                    <Button
                      variant={isActive ? "secondary" : "ghost"}
                      size="sm"
                    >
                      {link.label}
                    </Button>
                  </a>
                )
              })}
            </nav>
          </div>

          {/* Actions */}
          <div className="flex items-center gap-1">
            <ThemeToggle />
          </div>
        </div>
      </header>
    </TooltipProvider>
  )
}
