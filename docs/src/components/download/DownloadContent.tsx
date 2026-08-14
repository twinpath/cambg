import { DownloadHero } from "./DownloadHero"
import { ReleaseArchive } from "./ReleaseArchive"
import type { Release } from "@/types/releases"
import { useReleases } from "@/hooks/use-releases"

export function DownloadContent({ fallbackReleases }: { fallbackReleases: Release[] }) {
  const { releases, loading } = useReleases(fallbackReleases)

  return (
    <>
      <DownloadHero latest={releases[0]} isLoading={loading} />
      <ReleaseArchive releases={releases} isLoading={loading} />
    </>
  )
}
