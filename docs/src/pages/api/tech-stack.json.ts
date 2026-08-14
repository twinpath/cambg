import type { APIRoute } from 'astro';
import fs from 'fs';
import path from 'path';
import { getTechStackPrompt } from '../../data/prompts';

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

    const prompt = getTechStackPrompt(tomlContent);

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
