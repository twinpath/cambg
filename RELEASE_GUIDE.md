# Developer Release and Versioning Guide

This document outlines the versioning scheme, release workflows, and branch management strategies used in this project to ensure consistent and automated releases.

## Release Workflow Overview

The release pipeline is fully automated using GitHub Actions. The flow goes as follows:
1. Developers commit and push code to the development branches.
2. Changes are merged into the main branch.
3. A developer creates and pushes a Git tag following the versioning rules described below.
4. GitHub Actions intercepts the tag push, initiates the build process, invokes the Gemini API to generate release notes, and publishes the release on GitHub with the generated APK and AAB binaries attached.

## Versioning Scheme (Semantic Versioning)

We follow the Semantic Versioning (SemVer 2.0.0) standard with the format `vMAJOR.MINOR.PATCH` optionally appended with a pre-release suffix:

`v<MAJOR>.<MINOR>.<PATCH>[-<SUFFIX>.<N>]`

*   **MAJOR**: Incremented when you make incompatible API changes or breaking app updates.
*   **MINOR**: Incremented when you add functionality in a backward-compatible manner (e.g. new features).
*   **PATCH**: Incremented when you make backward-compatible bug fixes.

### Release Type Suffixes

The type of release is determined by the suffix appended to the version tag.

| Release Type | Suffix Format | Example | Target Audience / Use Case |
|---|---|---|---|
| Stable | No suffix | `v1.0.0` | Production release, uploaded to Google Play store production track. |
| Beta | `-beta.N` | `v1.0.0-beta.1` | Public pre-release, used for open or closed beta testing groups. |
| Alpha | `-alpha.N` | `v1.0.0-alpha.2` | Internal pre-release, used for internal QA and developer testing. |
| Test | `-test.N` | `v1.0.0-test.1` | System validation, used to test build pipelines and CI/CD behaviors. |

Note: `N` is an integer starting from 1 (e.g., `beta.1`, `beta.2`).

## Step-by-Step Release Instructions

### 1. Ensure all changes are merged
Ensure that the `main` branch is up to date and contains all the changes you wish to release.

### 2. Update Version in `build.gradle.kts`
Before creating a release tag, you must update the version details inside the application so that the correct version is shown in the app settings (compiled via `BuildConfig.VERSION_NAME`).

Open [app/build.gradle.kts](file:///c:/Users/dyzulk/Documents/twinpath/cambg-record/app/build.gradle.kts) and update the `defaultConfig` block:
- Increment `versionCode` (integer, e.g., `3` to `4`).
- Update `versionName` (string, matching your release target, e.g., `"0.1.3-alpha.1"`).

Commit and push these changes to your release or development branch:
```bash
git add app/build.gradle.kts
git commit -m "build: bump version to <VERSION>"
git push
```

### 3. Create a Tag Locally
Create a Git tag corresponding to the release version and type.

```bash
# Example for a Stable release
git tag v1.0.0

# Example for a Beta release
git tag v1.0.0-beta.1
```

### 4. Push the Tag to GitHub
Pushing the tag to the remote repository triggers the release workflow.

```bash
# Push a specific tag
git push origin v1.0.0
```

### 5. Monitor the Release Build
Navigate to the "Actions" tab in the GitHub repository. Look for the "Android Release Build" run. 

The workflow will:
1. Verify the code base.
2. Build signed/unsigned release APKs and AABs.
3. Automatically gather commit logs since the last tag.
4. Request the Gemini API to format and structure the changelog based on the prompt template.
5. Create a GitHub Release page containing the changelog, a download link table, and the build binaries.
