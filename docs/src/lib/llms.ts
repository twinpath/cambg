import type { LlmsData } from "@/types/llms"
import { SITE_URL } from "@/data/site"

/**
 * Generates llms.txt content following the llmstxt.org standard.
 * @see https://llmstxt.org/
 */
export function generateLlmsTxt(data: LlmsData): string {
  const lines: string[] = [
    `# ${data.name}`,
    "",
    `> ${data.tagline}`,
    "",
    "## Key Features",
    "",
    ...data.features.map((f) => `- **${f.name}**: ${f.description}`),
    "",
    "## System Requirements",
    "",
    ...data.requirements.map((r) => `- **${r.label}**: ${r.detail}`),
    "",
    "## Docs",
    "",
    ...data.docs.map(
      (d) => `- [${d.title}](${SITE_URL}${d.path}): ${d.description}`
    ),
    "",
    "## Links",
    "",
    ...data.externalLinks.map((l) =>
      l.url.startsWith("/")
        ? `- [${l.title}](${SITE_URL}${l.url})`
        : `- [${l.title}](${l.url})`
    ),
    "",
  ]

  return lines.join("\n")
}
