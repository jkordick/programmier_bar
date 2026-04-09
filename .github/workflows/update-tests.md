---
description: Keeps comprehensive unit/integration tests (backend) and Playwright E2E tests (frontend) up to date whenever code is pushed or merged to main.
on:
  push:
    branches: [main]
permissions:
  contents: read
  pull-requests: read
network:
  allowed: [defaults, java, node]
tools:
  github:
    toolsets: [default]
  playwright:
safe-outputs:
  create-pull-request:
    title-prefix: "test: "
    labels: [testing, automation]
    draft: false
  noop:
steps:
  - name: Install frontend dependencies
    run: cd frontend && npm ci
  - name: Install Playwright browsers
    run: cd frontend && npx playwright install --with-deps chromium
  - name: Start backend
    run: cd backend && mvn spring-boot:run &
  - name: Start frontend dev server
    run: cd frontend && npm run dev &
  - name: Wait for servers
    run: sleep 10
---

# Update Tests

You are an AI agent responsible for keeping the project's unit/integration tests (backend) and Playwright E2E tests (frontend) comprehensive and up to date.

## Your Task

Whenever code is pushed to the `main` branch (either directly or via a merged PR), analyze the codebase and its test suites, then add or update tests to ensure comprehensive coverage.

## Part 1: Backend Tests

### Steps

1. **Read the existing test files** under `backend/src/test/java/`.
2. **Explore the production source code** under `backend/src/main/java/` to understand:
   - All REST controllers and their endpoints (request mappings, HTTP methods, path variables, request bodies)
   - All services and their public methods
   - All repository interfaces and custom query methods
   - All model/entity classes, their fields, validation annotations, and relationships
3. **Identify testing gaps** by comparing existing tests against the production code:
   - Are all controller endpoints covered (happy path, error cases, validation)?
   - Are all service methods tested (including edge cases and error handling)?
   - Are custom repository query methods tested?
   - Are model validation constraints tested (e.g. `@NotBlank`, `@Min`, `@Max`)?
   - Are relationship behaviors tested (cascading, orphan removal)?
4. **If new or updated tests are needed**, write them:
   - Follow the existing test style and patterns (Spring Boot `@SpringBootTest` + `@AutoConfigureMockMvc` for controller tests, JUnit 5 assertions/Hamcrest matchers)
   - Place test files in the matching package structure under `backend/src/test/java/`
   - Add new test classes for untested controllers or services
   - Add new test methods to existing test classes when appropriate
   - Each test should be focused, well-named, and test a single behavior
   - Include tests for: happy paths, 404s, validation errors, boundary conditions, search/filter operations
5. **Verify the backend tests compile and pass** by running:
   ```bash
   cd backend && mvn test
   ```
   Fix any compilation errors or test failures before proceeding.

### Backend Guidelines

- Match the existing test conventions: class naming (`*Test.java`), method naming (descriptive `snake_case`), setup patterns (`@BeforeEach`).
- Use `MockMvc` for controller integration tests — do NOT use `@WebMvcTest` with mocked services; use `@SpringBootTest` with `@AutoConfigureMockMvc` to test against the real application context.
- Use the H2 in-memory database configured in `application.properties` — do NOT add separate test configuration.
- Clean up test data in `@BeforeEach` to ensure test isolation.
- Do NOT mock the `JokeGeneratorService` Copilot integration in tests — skip testing that service since it requires external AI credentials.
- Be conservative: only add tests that are genuinely missing. Do not rewrite existing passing tests.

## Part 2: Frontend E2E Tests (Playwright)

### Steps

1. **Check if Playwright is configured** in the `frontend/` directory. If `playwright.config.ts` does not exist, create one targeting `http://localhost:5173` with Chromium only.
2. **Read all frontend source files** under `frontend/src/` to understand:
   - React components (`App.tsx`, `DevCard.tsx`, `DevForm.tsx`, `MissionTimeline.tsx`)
   - API layer (`api.ts`) and its endpoints
   - TypeScript types (`types.ts`)
   - CSS selectors and class names available for test targeting
3. **Read any existing E2E test files** under `frontend/e2e/` or `frontend/tests/`.
4. **Identify E2E testing gaps** by comparing existing tests against the frontend functionality:
   - Page load: does the dev grid render with seeded data?
   - Create flow: click "Register", fill form, submit, verify new card appears
   - Edit flow: click "Edit" on a card, modify fields, submit, verify changes
   - Delete flow: click "Deorbit", verify card is removed
   - Mission timeline: open missions modal, add a mission, verify it appears, delete it
   - Validation: submit forms with invalid data, verify error feedback
   - Random joke banner: click to refresh joke
   - Empty/loading states
5. **If new or updated tests are needed**, write them:
   - Place Playwright test files under `frontend/e2e/` with `.spec.ts` extension
   - Use Playwright's `test` and `expect` from `@playwright/test`
   - Use CSS selectors (`.dev-card`, `.modal`, `.btn-primary`, etc.) and text content for locators
   - Each test should be independent and not rely on state from other tests
   - Use descriptive test names that explain the user flow being tested
6. **Verify E2E tests pass** using the Playwright tool (the backend and frontend dev servers are already running from the `steps:` block).

### Frontend Guidelines

- The frontend runs at `http://localhost:5173` and proxies `/api` to the backend at `http://localhost:8080`.
- The backend seeds the database with sample SpaceDeveloper data on startup, so tests can expect pre-existing data.
- Use Playwright locators: prefer `page.getByRole()`, `page.getByText()`, and `page.locator('.css-class')`.
- Keep tests focused on user-visible behavior, not implementation details.
- Do NOT test the AI joke generation feature (requires external credentials).
- Do NOT modify production source code. Only create or update test files and Playwright config.

## General Rules

- Do NOT modify production source code. Only create or update test files.
- If both backend and frontend tests are already comprehensive, call the `noop` safe output explaining that the test suite is up to date.

## Output

If you wrote or updated tests, create a pull request with:
- Title describing what test coverage was added (e.g. `test: add SpaceDeveloperController tests`)
- Body listing the new test classes/methods and what they cover
- Branch name: `test/update-tests`

**Important:** Do NOT create a GitHub issue as a fallback. If PR creation fails, push the branch and stop — do not open an issue describing the changes.
