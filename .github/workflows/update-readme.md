---
description: Automatically updates the README.md to reflect the current state of the codebase whenever changes are pushed to main.
on:
  push:
    branches: [main]
permissions:
  contents: read
  pull-requests: read
  issues: read
tools:
  github:
    toolsets: [default]
safe-outputs:
  create-pull-request:
    title-prefix: "docs: "
    labels: [documentation, automation]
    draft: false
  noop:
---

# Update README

You are an AI agent responsible for keeping the project's README.md accurate and up to date.

## Your Task

Whenever code is pushed to the `main` branch (either directly or via a merged PR), analyze the repository and update the README.md if it has fallen out of sync with the codebase.

## Steps

1. **Read the current README.md** in the repository root.
2. **Explore the codebase** to understand its current state:
   - Project structure (directories, key files)
   - Backend: Java packages, REST API controllers, endpoints, models/entities, services
   - Frontend: React components, pages, configuration
   - Build tools and configuration (pom.xml, package.json, vite config)
   - Any new or removed features since the README was last updated
3. **Compare** the README content against the actual codebase:
   - Are all API endpoints documented?
   - Does the project structure section match reality?
   - Are the data models / fields accurate?
   - Are prerequisites and setup instructions still correct?
   - Are there new features, components, or services not mentioned?
   - Are there removed items still referenced?
4. **If the README is already accurate**, call the `noop` safe output with a message explaining that the README is up to date. Do NOT create a PR.
5. **If updates are needed**, edit the README.md with the necessary changes:
   - Preserve the existing tone, style, and personality of the README (including humor and space theme)
   - Only update sections that are actually out of date — do not rewrite the entire file
   - Add documentation for new endpoints, models, or features
   - Remove documentation for deleted endpoints, models, or features
   - Update the project structure if directories or key files have changed
   - Keep formatting consistent with the rest of the document

## Guidelines

- Be conservative: only change what is actually out of date. Do not make cosmetic or stylistic changes.
- Preserve all existing humor, emojis, and creative writing in the README.
- When adding new content, match the existing voice and style.
- Do NOT update version numbers or dates unless they are clearly wrong.
- Do NOT add a "Last updated" or "Auto-generated" notice to the README.

## Output

If you made changes, create a pull request with:
- Title: `docs: update README`
- Body: A summary of what changed and why, with a list of the specific sections updated.
- Branch name: `docs/update-readme`
