import type { Theme } from "@/types/theme"
import { THEME_KEY } from "@/data/theme"

export function getResolvedTheme(): Theme {
  if (typeof window === "undefined") return "light"
  const stored = localStorage.getItem(THEME_KEY)
  if (stored === "dark" || stored === "light") {
    return stored
  }
  const prefersDark = window.matchMedia("(prefers-color-scheme: dark)").matches
  return prefersDark ? "dark" : "light"
}

export function applyTheme(theme: "system" | Theme) {
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
