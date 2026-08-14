import { readFileSync } from 'node:fs'
import { resolve } from 'node:path'

export interface GradleVersion {
  versionName: string
  versionCode: number
}

/**
 * Reads versionName and versionCode from app/build.gradle.kts
 * relative to the docs/ workspace (one level up).
 */
export function readGradleVersion(): GradleVersion {
  const gradlePath = resolve(import.meta.dirname, '..', '..', '..', 'app', 'build.gradle.kts')
  const content = readFileSync(gradlePath, 'utf-8')

  const codeMatch = content.match(/versionCode\s*=\s*(\d+)/)
  const nameMatch = content.match(/versionName\s*=\s*"([^"]+)"/)

  if (!codeMatch || !nameMatch) {
    throw new Error(
      `Failed to parse versionCode or versionName from ${gradlePath}`
    )
  }

  return {
    versionCode: parseInt(codeMatch[1], 10),
    versionName: nameMatch[1],
  }
}
