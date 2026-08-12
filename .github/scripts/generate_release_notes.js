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
  const url = `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=${apiKey}`;
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
    const githubRepository = process.env.GITHUB_REPOSITORY || 'owner/repo';
    const releaseVersion = process.env.GITHUB_REF_NAME || 'v1.0.0';

    finalNotes = finalNotes
      .replace(/\{\{GITHUB_REPOSITORY\}\}/g, githubRepository)
      .replace(/\{\{RELEASE_VERSION\}\}/g, releaseVersion);

    // 4. Output the release notes to a file for use in next steps
    const outputPath = path.join(process.cwd(), 'release_notes.md');
    fs.writeFileSync(outputPath, finalNotes, 'utf8');
    console.log(`Release notes successfully generated and written to ${outputPath}`);
  } catch (error) {
    console.error('Failed to generate release notes:', error.message);
    // Fallback release notes file to prevent workflow failure
    const outputPath = path.join(process.cwd(), 'release_notes.md');
    fs.writeFileSync(outputPath, `### Commits in this Release\n\n\`\`\`\n${commitLog}\n\`\`\``, 'utf8');
    console.log(`Fallback release notes written to ${outputPath}`);
  }
}

run();
