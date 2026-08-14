import type { LlmsData } from "@/types/llms"
import {
  SITE_NAME,
  SITE_DESCRIPTION,
  GITHUB_URL,
  GITHUB_RELEASES_URL,
} from "@/data/site"

export const LLMS_DATA: LlmsData = {
  name: SITE_NAME,
  tagline: SITE_DESCRIPTION,
  features: [
    {
      name: "Background Video Recording",
      description:
        "Record video secretly or while using other apps, even when the screen is off.",
    },
    {
      name: "Material Design 3",
      description: "Modern, clean, and beautiful user interface.",
    },
    {
      name: "Optimized Size",
      description:
        "Lightweight application footprint (approx. 50 MB installation size).",
    },
  ],
  requirements: [
    { label: "Operating System", detail: "Android 7.0+ (Min SDK 24 - Nougat)" },
    { label: "Supported Architectures (ABIs)", detail: "ARM & ARM64" },
    { label: "Permissions", detail: "Hard camera access required" },
  ],
  docs: [
    {
      title: "Downloads",
      path: "/download",
      description: "Download the latest version of CamBG Record",
    },
    {
      title: "Changelog",
      path: "/changelog",
      description: "View the complete release history and changes",
    },
    {
      title: "About & Documentation",
      path: "/about",
      description:
        "Learn more about CamBG Record architecture and technology",
    },
  ],
  externalLinks: [
    { title: "Main Website", url: "/" },
    { title: "Source Code", url: GITHUB_URL },
    { title: "Releases", url: GITHUB_RELEASES_URL },
  ],
} as const
