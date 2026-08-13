import { SITE_NAME } from "@/data/site"

interface LogoProps {
  className?: string;
}

export function Logo({ className = "size-5" }: LogoProps) {
  return (
    <div className={`inline-flex items-center justify-center rounded-md bg-white p-0.5 border border-zinc-200/80 shadow-sm ${className}`}>
      <img
        src={`${import.meta.env.BASE_URL}logo.svg`}
        alt={`${SITE_NAME} Logo`}
        className="h-full w-full object-contain"
      />
    </div>
  )
}
