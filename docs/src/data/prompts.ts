export const getTechStackPrompt = (tomlContent: string): string => `
You are a build assistant. You are given a \`libs.versions.toml\` file from an Android project.
Your task is to analyze the file and output a JSON array of the core technology stack items used in this project.
Each item in the array must have the following fields:
- "layer": The architectural layer (e.g. "Language", "UI Framework", "Build System", "Camera", "Navigation", "Persistence", "Preferences", "Networking", "Concurrency", "Firebase").
- "technology": The humanized name of the technology (e.g., "Kotlin", "Jetpack Compose + Material 3", "Gradle (Kotlin DSL)", "CameraX", "Navigation Compose", "Room Database", "DataStore Preferences", "Retrofit + OkHttp + Moshi", "Coil Compose", "Kotlinx Coroutines", "Firebase BOM (AI, App Check)").
- "version": The exact version number extracted from the \`[versions]\` section of the TOML file.

Rules:
1. Group related technologies where appropriate (e.g., combine retrofit + okhttp + moshi into "Retrofit + OkHttp + Moshi" with a version string like "2.12.0 / 4.10.0 / 1.15.2").
2. Only include the core technologies relevant to the user-facing documentation (do not include test-only tools like junit, espresso, roborazzi, robolectric, unless they are key development tooling).
3. Ensure every item in the JSON array is separated by a comma. Double check that there are no missing commas between elements.
4. The output must be a valid JSON array matching this schema:
[
  {
    "layer": "string",
    "technology": "string",
    "version": "string"
  }
]

Here is the TOML file content:
\`\`\`toml
${tomlContent}
\`\`\`
`.trim();
