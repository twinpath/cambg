You are an expert release manager. Generate a concise, professional, and well-structured release changelog in English based on the following git commit messages since the last release.

Commit messages:
{{COMMIT_LOG}}

Instructions:
1. Write a brief friendly introduction/executive summary of this release.
2. Group the changes logically into categories such as:
   - New Features
   - Bug Fixes
   - Improvements & Performance
   - Other Changes
3. Use bullet points for changes.
4. Do not include commit hashes or author names.
5. Output using clean Markdown syntax without markdown code block wrappers around the entire response.
6. Always append the following section at the end of the changelog to provide build artifact details, preserving the exact placeholder `{{ARTIFACT_TABLE}}`:

### Build Artifacts

{{ARTIFACT_TABLE}}
