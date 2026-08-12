const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

async function run() {
  const apiKey = process.env.GEMINI_API_KEY;
  if (!apiKey) {
    console.error('Error: GEMINI_API_KEY environment variable is not set.');
    process.exit(1);
  }

  // 1. Get commit history since last tag
  let commitLog = '';
  try {
    // Try to get the tag before the current one (which is HEAD)
    const lastTag = execSync('git describe --tags --abbrev=0 HEAD^ 2>/dev/null || git describe --tags --abbrev=0 HEAD~1 2>/dev/null').toString().trim();
    console.log(`Generating changelog since tag: ${lastTag}`);
    commitLog = execSync(`git log ${lastTag}..HEAD --oneline`).toString().trim();
  } catch (error) {
    console.log('No previous tag found or error fetching it. Fetching last 50 commits instead.');
    try {
      commitLog = execSync('git log --oneline -n 50').toString().trim();
    } catch (gitError) {
      console.error('Failed to retrieve git log:', gitError.message);
      commitLog = 'Initial release / No commit logs found.';
    }
  }

  if (!commitLog) {
    commitLog = 'No commits found since the last release.';
  }

  // 2. Read prompt template
  const templatePath = path.join(__dirname, '..', 'release-prompt-template.md');
  if (!fs.existsSync(templatePath)) {
    console.error(`Error: Template file not found at ${templatePath}`);
    process.exit(1);
  }

  let prompt = fs.readFileSync(templatePath, 'utf8');
  prompt = prompt.replace('{{COMMIT_LOG}}', commitLog);

  console.log('--- Sending Prompt to Gemini API ---');
  console.log(prompt);
  console.log('------------------------------------');

  // 3. Request Gemini API
  const url = `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=${apiKey}`;
  const requestBody = {
    contents: [
      {
        parts: [
          {
            text: prompt
          }
        ]
      }
    ]
  };

  const githubRepository = process.env.GITHUB_REPOSITORY || 'owner/repo';
  const releaseVersion = process.env.GITHUB_REF_NAME || 'v1.0.0';
  const artifactTable = generateArtifactTable(githubRepository, releaseVersion);

  try {
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(requestBody)
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(`Gemini API returned status ${response.status}: ${errorText}`);
    }

    const data = await response.json();
    const generatedText = data.candidates?.[0]?.content?.parts?.[0]?.text;

    if (!generatedText) {
      throw new Error('Invalid response structure or empty content from Gemini API');
    }

    let finalNotes = generatedText;

    finalNotes = finalNotes
      .replace(/\{\{GITHUB_REPOSITORY\}\}/g, githubRepository)
      .replace(/\{\{RELEASE_VERSION\}\}/g, releaseVersion)
      .replace(/\{\{ARTIFACT_TABLE\}\}/g, artifactTable);

    // 4. Output the release notes to a file for use in next steps
    const outputPath = path.join(process.cwd(), 'release_notes.md');
    fs.writeFileSync(outputPath, finalNotes, 'utf8');
    console.log(`Release notes successfully generated and written to ${outputPath}`);
  } catch (error) {
    console.error('Failed to generate release notes via Gemini API:', error.message);
    // Fallback release notes file to prevent workflow failure
    const outputPath = path.join(process.cwd(), 'release_notes.md');
    const fallbackNotes = `### Release Summary (${releaseVersion})\n\n### Commits in this Release\n\n\`\`\`\n${commitLog}\n\`\`\`\n\n### Build Artifacts\n\n${artifactTable}`;
    fs.writeFileSync(outputPath, fallbackNotes, 'utf8');
    console.log(`Fallback release notes written to ${outputPath}`);
  }
}

function generateArtifactTable(githubRepository, releaseVersion) {
  const apkDir = path.join(process.cwd(), 'app', 'build', 'outputs', 'apk', 'release');
  const aabDir = path.join(process.cwd(), 'app', 'build', 'outputs', 'bundle', 'release');

  const files = [];

  if (fs.existsSync(apkDir)) {
    const apkFiles = fs.readdirSync(apkDir).filter(f => f.endsWith('.apk'));
    for (const f of apkFiles) {
      files.push({ name: f, type: 'APK' });
    }
  }

  if (fs.existsSync(aabDir)) {
    const aabFiles = fs.readdirSync(aabDir).filter(f => f.endsWith('.aab'));
    for (const f of aabFiles) {
      files.push({ name: f, type: 'AAB' });
    }
  }

  if (files.length === 0) {
    return 'No artifacts found.';
  }

  files.sort((a, b) => a.name.localeCompare(b.name));

  let table = '| File Name | Architecture / Description | Download Link |\n| --- | --- | --- |\n';
  for (const file of files) {
    let arch = 'Universal';
    if (file.name.includes('arm64-v8a')) arch = 'ARM64 (v8a)';
    else if (file.name.includes('armeabi-v7a')) arch = 'ARMv7 (32-bit)';
    else if (file.name.includes('x86_64')) arch = 'Intel x86_64';
    else if (file.name.includes('x86')) arch = 'Intel x86';

    if (file.type === 'AAB') {
      arch += ' (App Bundle)';
    }

    const downloadUrl = `https://github.com/${githubRepository}/releases/download/${releaseVersion}/${file.name}`;
    table += `| \`${file.name}\` | ${arch} | [Download](${downloadUrl}) |\n`;
  }

  return table;
}

run();
