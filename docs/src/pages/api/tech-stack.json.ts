import type { APIRoute } from 'astro';
import fs from 'fs';
import path from 'path';

export const GET: APIRoute = async () => {
  // Load .env if GEMINI_API_KEY is not set in environment (for local testing)
  if (!process.env.GEMINI_API_KEY) {
    const envPath = path.resolve('../.env');
    if (fs.existsSync(envPath)) {
      const envContent = fs.readFileSync(envPath, 'utf8');
      const match = envContent.match(/^GEMINI_API_KEY=(.+)$/m);
      if (match) {
        process.env.GEMINI_API_KEY = match[1].trim();
      }
    }
  }

  const apiKey = process.env.GEMINI_API_KEY;
  if (!apiKey) {
    console.warn("No GEMINI_API_KEY found, returning empty tech-stack");
    return new Response(JSON.stringify([]), {
      status: 200,
      headers: {
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*',
      },
    });
  }

  try {
    const tomlPath = path.resolve('../gradle/libs.versions.toml');
    console.log(`[DEBUG] TOML Path resolved to: ${tomlPath}`);
    console.log(`[DEBUG] TOML File exists: ${fs.existsSync(tomlPath)}`);
    console.log(`[DEBUG] GEMINI_API_KEY length: ${apiKey ? apiKey.length : 0}`);
    
    if (!fs.existsSync(tomlPath)) {
      throw new Error(`TOML file not found at ${tomlPath}`);
    }
    const tomlContent = fs.readFileSync(tomlPath, 'utf8');

    const prompt = `You are a build assistant. You are given a \`libs.versions.toml\` file from an Android project.
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
\`\`\``;

    const requestBody = {
      contents: [
        {
          parts: [
            { text: prompt }
          ]
        }
      ],
      generationConfig: {
        responseMimeType: "application/json"
      }
    };

    const candidateModels = ['gemini-3.5-flash', 'gemini-3.6-flash', 'gemini-flash-latest', 'gemini-2.5-flash'];
    let generatedText = null;

    for (const modelName of candidateModels) {
      const url = `https://generativelanguage.googleapis.com/v1beta/models/${modelName}:generateContent?key=${apiKey}`;
      try {
        const response = await fetch(url, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(requestBody)
        });

        if (response.ok) {
          const data = await response.json() as any;
          generatedText = data.candidates?.[0]?.content?.parts?.[0]?.text;
          if (generatedText) {
            break;
          }
        }
      } catch (err) {
        console.warn(`Failed with model ${modelName}:`, err);
      }
    }

    if (!generatedText) {
      throw new Error("All Gemini models failed to return output");
    }

    // Clean up markdown block wraps and trailing commas
    let cleanedJson = generatedText.trim();
    if (cleanedJson.startsWith("```")) {
      cleanedJson = cleanedJson.replace(/^```(?:json)?\n?/, "").replace(/\n?```$/, "").trim();
    }
    // Remove trailing commas before closing braces/brackets
    cleanedJson = cleanedJson.replace(/,\s*([\]}])/g, "$1");

    // Parse the generated JSON text
    const parsedData = JSON.parse(cleanedJson);

    return new Response(JSON.stringify(parsedData), {
      status: 200,
      headers: {
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*',
      },
    });

  } catch (error: any) {
    console.error("Failed to generate tech stack:", error);
    return new Response(JSON.stringify([]), {
      status: 200,
      headers: {
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*',
      },
    });
  }
};
