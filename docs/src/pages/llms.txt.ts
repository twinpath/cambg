import type { APIRoute } from "astro"
import { generateLlmsTxt } from "@/lib/llms"
import { LLMS_DATA } from "@/data/llms"

export const GET: APIRoute = () => {
  return new Response(generateLlmsTxt(LLMS_DATA), {
    status: 200,
    headers: {
      "Content-Type": "text/plain; charset=utf-8",
      "Cache-Control": "public, max-age=86400",
    },
  })
}
