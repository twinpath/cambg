import { Button } from "@/components/ui/button"
import { TooltipProvider } from "@/components/ui/tooltip"
import { ThemeToggle } from "@/components/ThemeToggle"
import { MobileNav } from "@/components/MobileNav"
import { NAV_LINKS, SITE_NAME } from "@/data/site"
import { Logo } from "@/components/Logo"
import { Skeleton } from "@/components/ui/skeleton"
import { useLoading } from "@/hooks/use-loading"
import type { NavbarProps } from "@/types/navigation"

export function Navbar({ currentPath }: NavbarProps) {
  const loading = useLoading()

  return (
    <TooltipProvider>
      <header className="sticky top-0 z-50 w-full border-b bg-background/80 backdrop-blur-sm">
        <div className="mx-auto flex h-12 max-w-5xl items-center justify-between px-4">
          <div className="flex items-center gap-4">
            {/* Mobile menu */}
            <div className="md:hidden">
              {loading ? (
                <Skeleton className="h-8 w-8 rounded-md" />
              ) : (
                <MobileNav currentPath={currentPath} />
              )}
            </div>

            {/* Logo + wordmark */}
            {loading ? (
              <div className="flex items-center gap-2">
                <Skeleton className="h-6 w-6 rounded-full" />
                <Skeleton className="h-4 w-20 rounded" />
              </div>
            ) : (
              <a href={import.meta.env.BASE_URL} className="flex items-center gap-2">
                <Logo className="size-6" />
                <span className="font-heading text-sm font-medium">
                  {SITE_NAME}
                </span>
              </a>
            )}

            {/* Desktop navigation */}
            <nav className="hidden items-center gap-0.5 md:flex">
              {loading
                ? Array.from({ length: 4 }).map((_, idx) => (
                    <Skeleton key={idx} className="h-8 w-16 rounded-md mx-0.5" />
                  ))
                : NAV_LINKS.map((link) => {
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
            {loading ? (
              <Skeleton className="h-8 w-8 rounded-md" />
            ) : (
              <ThemeToggle />
            )}
          </div>
        </div>
      </header>
    </TooltipProvider>
  )
}

