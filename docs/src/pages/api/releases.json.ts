import type { APIRoute } from 'astro';
import { fetchGitHubReleases } from '../../lib/github';

export const GET: APIRoute = async () => {
  try {
    const releases = await fetchGitHubReleases();
    return new Response(JSON.stringify(releases), {
      status: 200,
      headers: {
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*',
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
