import type { APIRoute } from 'astro';
import { fetchGitHubReleases } from '../../lib/github';
import { readGradleVersion } from '../../lib/gradle';
import { getReleasesWithVersionCodes } from '../../lib/releases';

export const GET: APIRoute = async () => {
  try {
    const releases = await fetchGitHubReleases();
    const gradle = readGradleVersion();
    const updatedReleases = getReleasesWithVersionCodes(releases, gradle);

    return new Response(JSON.stringify(updatedReleases), {
      status: 200,
      headers: {
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*',
        'Cache-Control': 'no-cache, no-store, must-revalidate',
        'Pragma': 'no-cache',
        'Expires': '0',
      },
    });
  } catch (error) {
    return new Response(JSON.stringify({ error: 'Failed to fetch releases' }), {
      status: 500,
      headers: {
        'Content-Type': 'application/json',
      },
    });
  }
};
