export const SITE_NAME = "CamBG Record"
export const SITE_DESCRIPTION =
  "A native Android application for persistent background video recording with intelligent detection capabilities and Material Design 3 aesthetics."
export const SITE_TAGLINE = "Background Video Recording, Perfected."
export const GITHUB_URL = "https://github.com/twinpath/cambg"
export const GITHUB_RELEASES_URL = `${GITHUB_URL}/releases`

export const NAV_LINKS = [
  { label: "Home", href: "/" },
  { label: "Download", href: "/download" },
  { label: "About", href: "/about" },
] as const

export const FOOTER_SECTIONS = [
  {
    title: "Product",
    links: [
      { label: "Features", href: "/#features" },
      { label: "Download", href: "/download" },
      { label: "Release Archive", href: "/download#archive" },
    ],
  },
  {
    title: "Resources",
    links: [
      { label: "Documentation", href: "/about#architecture" },
      { label: "FAQ", href: "/about#faq" },
      { label: "Source Code", href: GITHUB_URL, external: true },
    ],
  },
  {
    title: "Project",
    links: [
      { label: "About", href: "/about" },
      { label: "Releases", href: GITHUB_RELEASES_URL, external: true },
      { label: "License", href: "/about#license" },
    ],
  },
] as const

export const SYSTEM_REQUIREMENTS = [
  { label: "Android 7.0+", detail: "Min SDK 24 (Nougat)" },
  { label: "ARM / ARM64", detail: "ABI support" },
  { label: "Camera Required", detail: "Hardware camera access" },
  { label: "50 MB", detail: "Approximate install size" },
] as const
