import { useState, useEffect } from "react"
import { Button } from "@/components/ui/button"
import { Sun, Moon, Monitor } from "lucide-react"
import {
  Tooltip,
  TooltipContent,
  TooltipTrigger,
} from "@/components/ui/tooltip"

import type { Theme } from "@/types/theme"
import { THEME_KEY, THEME_LABELS } from "@/data/theme"
import { getResolvedTheme, applyTheme } from "@/lib/theme"

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
