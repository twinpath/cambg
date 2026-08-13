import { useState, useEffect } from "react"
import { Button } from "@/components/ui/button"
import { Sun, Moon, Monitor } from "lucide-react"
import {
  Tooltip,
  TooltipContent,
  TooltipTrigger,
} from "@/components/ui/tooltip"

type Theme = "system" | "dark" | "light"

const THEME_KEY = "cambg-theme"

function getStoredTheme(): Theme {
  if (typeof window === "undefined") return "system"
  return (localStorage.getItem(THEME_KEY) as Theme) ?? "system"
}

function applyTheme(theme: Theme) {
  const root = document.documentElement
  if (theme === "system") {
    const prefersDark = window.matchMedia(
      "(prefers-color-scheme: dark)"
    ).matches
    root.classList.toggle("dark", prefersDark)
  } else {
    root.classList.toggle("dark", theme === "dark")
  }
}

const THEME_CYCLE: Theme[] = ["system", "dark", "light"]
const THEME_LABELS: Record<Theme, string> = {
  system: "System",
  dark: "Dark",
  light: "Light",
}

export function ThemeToggle() {
  const [theme, setTheme] = useState<Theme>("system")

  useEffect(() => {
    const stored = getStoredTheme()
    setTheme(stored)
    applyTheme(stored)

    const mediaQuery = window.matchMedia("(prefers-color-scheme: dark)")
    const handler = () => {
      if (getStoredTheme() === "system") {
        applyTheme("system")
      }
    }
    mediaQuery.addEventListener("change", handler)
    return () => mediaQuery.removeEventListener("change", handler)
  }, [])

  const cycleTheme = () => {
    const currentIndex = THEME_CYCLE.indexOf(theme)
    const next = THEME_CYCLE[(currentIndex + 1) % THEME_CYCLE.length]
    setTheme(next)
    localStorage.setItem(THEME_KEY, next)
    applyTheme(next)
  }

  const icon =
    theme === "dark" ? (
      <Moon />
    ) : theme === "light" ? (
      <Sun />
    ) : (
      <Monitor />
    )

  return (
    <Tooltip>
      <TooltipTrigger asChild>
        <Button
          variant="ghost"
          size="icon"
          onClick={cycleTheme}
          aria-label={`Theme: ${THEME_LABELS[theme]}`}
        >
          {icon}
        </Button>
      </TooltipTrigger>
      <TooltipContent>{THEME_LABELS[theme]}</TooltipContent>
    </Tooltip>
  )
}
