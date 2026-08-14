import type { Release } from '../types/releases';
import type { GradleVersion } from './gradle';

/**
 * Processes releases fetched from GitHub, sorting them and injecting sequentially computed
 * versionCodes based on the local Gradle version.
 * 
 * @param releases List of releases fetched from GitHub API.
 * @param gradle The local gradle version and code configuration.
 * @returns Sorted list of releases (newest first) with versionCodes assigned.
 */
export function getReleasesWithVersionCodes(releases: Release[], gradle: GradleVersion): Release[] {
  // Sort releases from oldest to newest using semantic version comparison
  const sorted = [...releases].sort((a, b) => {
    const cleanA = a.version.replace(/^v/i, '');
    const cleanB = b.version.replace(/^v/i, '');
    return cleanA.localeCompare(cleanB, undefined, { numeric: true, sensitivity: 'base' });
  });

  // Find the index of the release matching the local versionName
  const localVersionTag = `v${gradle.versionName}`;
  const matchIndex = sorted.findIndex(
    (r) => r.version === localVersionTag || r.version === gradle.versionName
  );

  // Assign versionCode to each release sequentially
  if (matchIndex >= 0) {
    // Local version exists in GitHub releases
    for (let i = 0; i < sorted.length; i++) {
      sorted[i].versionCode = gradle.versionCode - (matchIndex - i);
    }
  } else {
    // Local version not yet released; latest GitHub release = localVersionCode - 1
    for (let i = 0; i < sorted.length; i++) {
      sorted[i].versionCode = (gradle.versionCode - 1) - (sorted.length - 1 - i);
    }
  }

  // Return in newest-first order for the API consumer
  sorted.reverse();
  return sorted;
}
