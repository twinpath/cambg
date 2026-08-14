import { ARCHITECTURE_MAP } from "@/data/architectures"

export function getArchitecture(fileName: string): string {
  const lower = fileName.toLowerCase()
  for (const [key, label] of Object.entries(ARCHITECTURE_MAP)) {
    if (lower.includes(key)) {
      return label
    }
  }
  return "Universal"
}

export async function getClientArch(): Promise<string> {
  if (typeof window === "undefined" || !window.navigator) return "universal";
  
  const nav = window.navigator as any;
  if (nav.userAgentData && nav.userAgentData.getHighEntropyValues) {
    try {
      const hints = await nav.userAgentData.getHighEntropyValues(["architecture", "bitness"]);
      const arch = (hints.architecture || "").toLowerCase();
      const bitness = (hints.bitness || "").toLowerCase();
      if (arch === "arm") {
        return bitness === "64" ? "arm64-v8a" : "armeabi-v7a";
      }
      if (arch === "x86") {
        return bitness === "64" ? "x86_64" : "x86";
      }
    } catch (e) {}
  }

  const ua = nav.userAgent.toLowerCase();
  const platform = (nav.platform || "").toLowerCase();
  
  try {
    const canvas = document.createElement("canvas");
    const gl = canvas.getContext("webgl") || canvas.getContext("experimental-webgl");
    if (gl) {
      const debugInfo = gl.getExtension("WEBGL_debug_renderer_info");
      if (debugInfo) {
        const renderer = gl.getParameter(debugInfo.UNMASKED_RENDERER_WEBGL).toLowerCase();
        if (renderer.includes("apple") && !renderer.includes("intel")) {
          return "arm64-v8a";
        }
      }
    }
  } catch (e) {}

  if (ua.includes("arm64") || ua.includes("aarch64")) return "arm64-v8a";
  if (ua.includes("arm") || ua.includes("armeabi")) return "armeabi-v7a";
  if (ua.includes("x86_64") || ua.includes("amd64") || platform.includes("win64") || platform.includes("macintel")) return "x86_64";
  if (ua.includes("x86") || ua.includes("i686")) return "x86";
  
  return "universal";
}
