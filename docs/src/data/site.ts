const BASE_URL = import.meta.env.BASE_URL

export const API_ENDPOINTS = {
  RELEASES: `${BASE_URL}api/releases.json`,
  TECH_STACK: `${BASE_URL}api/tech-stack.json`,
} as const

export const SITE_NAME = "CamBG Record"
export const SITE_DESCRIPTION =
  "A native Android application for persistent background video recording with Material Design 3 aesthetics."
export const SITE_TAGLINE = "Background Video Recording, Perfected."
export const GITHUB_URL = "https://github.com/twinpath/cambg"
export const GITHUB_RELEASES_URL = `${GITHUB_URL}/releases`

export const NAV_LINKS = [
  { label: "Home", href: `${BASE_URL}` },
  { label: "Download", href: `${BASE_URL}download` },
  { label: "Changelog", href: `${BASE_URL}changelog` },
  { label: "About", href: `${BASE_URL}about` },
] as const

export const FOOTER_SECTIONS = [
  {
    title: "Product",
    links: [
      { label: "Features", href: `${BASE_URL}#features` },
      { label: "Download", href: `${BASE_URL}download` },
      { label: "Release Archive", href: `${BASE_URL}download#archive` },
    ],
  },
  {
    title: "Resources",
    links: [
      { label: "Documentation", href: `${BASE_URL}about#architecture` },
      { label: "FAQ", href: `${BASE_URL}about#faq` },
      { label: "Source Code", href: GITHUB_URL, external: true },
    ],
  },
  {
    title: "Project",
    links: [
      { label: "About", href: `${BASE_URL}about` },
      { label: "Releases", href: GITHUB_RELEASES_URL, external: true },
      { label: "License", href: `${BASE_URL}about#license` },
    ],
  },
] as const

export const SYSTEM_REQUIREMENTS = [
  { label: "Android 7.0+", detail: "Min SDK 24 (Nougat)" },
  { label: "ARM64 / ARMv7 / x86 / x86_64", detail: "ABI support" },
  { label: "Approx. 40 MB", detail: "Install size" },
] as const

export const FOOTER_CONTENT = {
  githubLink: "View on GitHub",
  copyright: "App is Proprietary. Web code is MIT.",
  brandingNote: "Built with precision. Designed for reliability.",
} as const

