import type { APIRoute } from 'astro';

const SITE_URL = 'https://cambg.dyzulk.com';

/**
 * Generates llms.txt content following the llmstxt.org standard.
 * @see https://llmstxt.org/
 */
function generateLlmsTxt(): string {
  return [
    '# CamBG Record',
    '',
    '> A native Android application for persistent background video recording with Material Design 3 aesthetics.',
    '',
    '## Key Features',
    '',
    '- **Background Video Recording**: Record video secretly or while using other apps, even when the screen is off.',
    '- **Material Design 3**: Modern, clean, and beautiful user interface.',
    '- **Optimized Size**: Lightweight application footprint (approx. 50 MB installation size).',
    '',
    '## System Requirements',
    '',
    '- **Operating System**: Android 7.0+ (Min SDK 24 - Nougat)',
    '- **Supported Architectures (ABIs)**: ARM & ARM64',
    '- **Permissions**: Hard camera access required',
    '',
    '## Docs',
    '',
    `- [Downloads](${SITE_URL}/download): Download the latest version of CamBG Record`,
    `- [Changelog](${SITE_URL}/changelog): View the complete release history and changes`,
    `- [About & Documentation](${SITE_URL}/about): Learn more about CamBG Record architecture and technology`,
    '',
    '## Links',
    '',
    `- [Main Website](${SITE_URL}/)`,
    '- [Source Code](https://github.com/twinpath/cambg)',
    '- [Releases](https://github.com/twinpath/cambg/releases)',
    '',
  ].join('\n');
}

export const GET: APIRoute = () => {
  return new Response(generateLlmsTxt(), {
    status: 200,
    headers: {
      'Content-Type': 'text/plain; charset=utf-8',
      'Cache-Control': 'public, max-age=86400',
    },
  });
};
