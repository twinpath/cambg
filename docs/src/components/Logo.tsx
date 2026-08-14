import { SITE_NAME } from "@/data/site"
import type { LogoProps } from "@/types/navigation"

export function Logo({ className = "size-5" }: LogoProps) {
  return (
    <svg
      xmlns="http://www.w3.org/2000/svg"
      viewBox="-15 -15 130 130"
      className={`${className} text-zinc-900 dark:text-zinc-50 transition-colors duration-200`}
      aria-label={`${SITE_NAME} Logo`}
      style={{
        filter: `
          drop-shadow(1px 0px 0px currentColor)
          drop-shadow(-1px 0px 0px currentColor)
          drop-shadow(0px 1px 0px currentColor)
          drop-shadow(0px -1px 0px currentColor)
        `
      }}
    >
      <defs>
        <linearGradient id="blueGrad" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" stop-color="#4285F4"/>
          <stop offset="100%" stop-color="#2B65D9"/>
        </linearGradient>
        <linearGradient id="greenGrad" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" stop-color="#34A853"/>
          <stop offset="100%" stop-color="#22853C"/>
        </linearGradient>
        <linearGradient id="yellowGrad" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" stop-color="#FBBC05"/>
          <stop offset="100%" stop-color="#D99B00"/>
        </linearGradient>
        <linearGradient id="redGrad" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" stop-color="#EA4335"/>
          <stop offset="100%" stop-color="#B3281E"/>
        </linearGradient>
      </defs>

      <path
        d="M 50 10 L 82 23 L 82 50 C 82 70 68 85 50 92 C 32 85 18 70 18 50 L 18 23 Z" 
        fill="none"
        stroke="url(#greenGrad)"
        stroke-width="7"
        stroke-linejoin="round"
        stroke-linecap="round"
      />

      <rect x="30" y="38" width="28" height="24" rx="6" fill="url(#blueGrad)" />

      <path
        d="M 56 50 L 70 41 C 72 40 75 41 75 44 L 75 56 C 75 59 72 60 70 59 L 56 50 Z" 
        fill="url(#yellowGrad)"
      />

      <circle cx="39" cy="50" r="4" fill="url(#redGrad)" />
    </svg>
  )
}

