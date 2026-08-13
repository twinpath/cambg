import { useState, useEffect } from "react"
import { Button } from "@/components/ui/button"
import { Sun, Moon, Monitor } from "lucide-react"
import {
  Tooltip,
  TooltipContent,
  TooltipTrigger,
} from "@/components/ui/tooltip"

type Theme = "dark" | "light"

const THEME_KEY = "cambg-theme"

function getResolvedTheme(): Theme {
  if (typeof window === "undefined") return "light"
  const stored = localStorage.getItem(THEME_KEY)
  if (stored === "dark" || stored === "light") {
    return stored
  }
  const prefersDark = window.matchMedia("(prefers-color-scheme: dark)").matches
  return prefersDark ? "dark" : "light"
}

function applyTheme(theme: "system" | Theme) {
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

const THEME_LABELS: Record<Theme, string> = {
  dark: "Dark",
  light: "Light",
}

export function ThemeToggle() {
  const [theme, setTheme] = useState<Theme>("light")

  useEffect(() => {
    const resolved = getResolvedTheme()
    setTheme(resolved)

    const stored = localStorage.getItem(THEME_KEY)
    if (stored === "dark" || stored === "light") {
      applyTheme(stored)
    } else {
      applyTheme("system")
    }

    const mediaQuery = window.matchMedia("(prefers-color-scheme: dark)")
    const handler = () => {
      const storedTheme = localStorage.getItem(THEME_KEY)
      if (!storedTheme || storedTheme === "system") {
        const active = mediaQuery.matches ? "dark" : "light"
        setTheme(active)
        applyTheme("system")
      }
    }
    mediaQuery.addEventListener("change", handler)
    return () => mediaQuery.removeEventListener("change", handler)
  }, [])

  const cycleTheme = () => {
    const next = theme === "dark" ? "light" : "dark"
    setTheme(next)
    localStorage.setItem(THEME_KEY, next)
    applyTheme(next)
  }

  const icon = theme === "dark" ? <Moon /> : <Sun />

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
