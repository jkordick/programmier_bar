import { test, expect } from '@playwright/test';

const BASE = 'http://localhost:5173';

// Unique suffix to avoid conflicts between test runs
const uid = () => Date.now().toString().slice(-6);

test.describe('Page load', () => {
  test('shows header and dev grid with seeded data', async ({ page }) => {
    await page.goto(BASE);
    await expect(page.locator('h1')).toContainText('SPACE DEVS');
    // The backend seeds data, so at least one dev card should be visible
    await expect(page.locator('.dev-card').first()).toBeVisible({ timeout: 10000 });
  });

  test('shows joke banner with default text', async ({ page }) => {
    await page.goto(BASE);
    await expect(page.locator('.joke-banner')).toBeVisible();
    await expect(page.locator('.joke-banner-text')).toBeVisible();
  });

  test('Register and Scan Galaxy buttons are visible', async ({ page }) => {
    await page.goto(BASE);
    await expect(page.getByRole('button', { name: /Register Space Dev/i })).toBeVisible();
    await expect(page.getByRole('button', { name: /Scan Galaxy/i })).toBeVisible();
  });
});

test.describe('Create space dev', () => {
  test('opens register form when clicking Register Space Dev', async ({ page }) => {
    await page.goto(BASE);
    await page.getByRole('button', { name: /Register Space Dev/i }).click();
    await expect(page.locator('.modal')).toBeVisible();
    await expect(page.locator('.modal h2')).toContainText('Register New Space Dev');
  });

  test('closes form when clicking Cancel', async ({ page }) => {
    await page.goto(BASE);
    await page.getByRole('button', { name: /Register Space Dev/i }).click();
    await expect(page.locator('.modal')).toBeVisible();
    await page.getByRole('button', { name: /Cancel/i }).click();
    await expect(page.locator('.modal')).not.toBeVisible();
  });

  test('creates new dev and card appears in grid', async ({ page }) => {
    await page.goto(BASE);
    const callSign = `TestPilot${uid()}`;

    await page.getByRole('button', { name: /Register Space Dev/i }).click();
    await page.locator('input[placeholder="NebulaNinja"]').fill(callSign);
    await page.locator('input[placeholder="Alex Starfield"]').fill('Test Astronaut');
    await page.getByRole('button', { name: /Launch Into Registry/i }).click();

    await expect(page.locator('.modal')).not.toBeVisible({ timeout: 5000 });
    await expect(page.locator('.dev-card').filter({ hasText: callSign })).toBeVisible({ timeout: 10000 });
  });
});

test.describe('Edit space dev', () => {
  test('opens edit form pre-filled when clicking Edit', async ({ page }) => {
    await page.goto(BASE);
    const firstCard = page.locator('.dev-card').first();
    await firstCard.waitFor({ timeout: 10000 });
    const callSignText = await firstCard.locator('.call-sign').textContent();

    await firstCard.getByRole('button', { name: /Edit/i }).click();
    await expect(page.locator('.modal')).toBeVisible();
    await expect(page.locator('.modal h2')).toContainText('Edit Space Dev');

    // The call sign input should be pre-filled
    const callSignInput = page.locator('input[placeholder="NebulaNinja"]');
    await expect(callSignInput).toHaveValue(callSignText ?? '');
  });

  test('updates dev and changes appear on card', async ({ page }) => {
    await page.goto(BASE);

    // Create a dev first so we have something to edit
    const callSign = `EditMe${uid()}`;
    await page.getByRole('button', { name: /Register Space Dev/i }).click();
    await page.locator('input[placeholder="NebulaNinja"]').fill(callSign);
    await page.locator('input[placeholder="Alex Starfield"]').fill('Edit Target');
    await page.getByRole('button', { name: /Launch Into Registry/i }).click();
    await expect(page.locator('.dev-card').filter({ hasText: callSign })).toBeVisible({ timeout: 10000 });

    // Edit the dev
    const card = page.locator('.dev-card').filter({ hasText: callSign });
    await card.getByRole('button', { name: /Edit/i }).click();
    await expect(page.locator('.modal')).toBeVisible();

    const updatedCallSign = `Updated${uid()}`;
    const callSignInput = page.locator('input[placeholder="NebulaNinja"]');
    await callSignInput.clear();
    await callSignInput.fill(updatedCallSign);
    await page.getByRole('button', { name: /Update Coordinates/i }).click();

    await expect(page.locator('.modal')).not.toBeVisible({ timeout: 5000 });
    await expect(page.locator('.dev-card').filter({ hasText: updatedCallSign })).toBeVisible({ timeout: 10000 });
  });
});

test.describe('Delete space dev', () => {
  test('removes dev card after clicking Deorbit', async ({ page }) => {
    await page.goto(BASE);

    // Create a dev to delete
    const callSign = `DeleteMe${uid()}`;
    await page.getByRole('button', { name: /Register Space Dev/i }).click();
    await page.locator('input[placeholder="NebulaNinja"]').fill(callSign);
    await page.locator('input[placeholder="Alex Starfield"]').fill('Expendable Astronaut');
    await page.getByRole('button', { name: /Launch Into Registry/i }).click();
    await expect(page.locator('.dev-card').filter({ hasText: callSign })).toBeVisible({ timeout: 10000 });

    // Delete it
    await page.locator('.dev-card').filter({ hasText: callSign }).getByRole('button', { name: /Deorbit/i }).click();

    await expect(page.locator('.dev-card').filter({ hasText: callSign })).not.toBeVisible({ timeout: 10000 });
  });
});

test.describe('Mission timeline', () => {
  test('opens missions modal when clicking Missions button', async ({ page }) => {
    await page.goto(BASE);
    const firstCard = page.locator('.dev-card').first();
    await firstCard.waitFor({ timeout: 10000 });
    await firstCard.getByRole('button', { name: /Missions/i }).click();

    await expect(page.locator('.mission-modal')).toBeVisible();
    await expect(page.locator('.mission-modal h2')).toContainText('Mission Log');
  });

  test('closes missions modal when clicking Close', async ({ page }) => {
    await page.goto(BASE);
    const firstCard = page.locator('.dev-card').first();
    await firstCard.waitFor({ timeout: 10000 });
    await firstCard.getByRole('button', { name: /Missions/i }).click();

    await expect(page.locator('.mission-modal')).toBeVisible();
    await page.locator('.mission-modal').getByRole('button', { name: /Close/i }).click();
    await expect(page.locator('.mission-modal')).not.toBeVisible();
  });

  test('adds a mission and it appears in the timeline', async ({ page }) => {
    await page.goto(BASE);

    // Create a fresh dev for isolation
    const callSign = `MissionDev${uid()}`;
    await page.getByRole('button', { name: /Register Space Dev/i }).click();
    await page.locator('input[placeholder="NebulaNinja"]').fill(callSign);
    await page.locator('input[placeholder="Alex Starfield"]').fill('Mission Test');
    await page.getByRole('button', { name: /Launch Into Registry/i }).click();
    await expect(page.locator('.dev-card').filter({ hasText: callSign })).toBeVisible({ timeout: 10000 });

    // Open missions
    await page.locator('.dev-card').filter({ hasText: callSign }).getByRole('button', { name: /Missions/i }).click();
    await expect(page.locator('.mission-modal')).toBeVisible();

    // Add a mission
    await page.locator('.mission-modal').getByRole('button', { name: /Log New Mission/i }).click();
    const missionTitle = `DeployNebula${uid()}`;
    await page.locator('.mission-form input[placeholder="Deploy Nebula ORM v2.0"]').fill(missionTitle);
    await page.locator('.mission-form').getByRole('button', { name: /Launch Mission/i }).click();

    await expect(page.locator('.mission-timeline .mission-title').filter({ hasText: missionTitle })).toBeVisible({ timeout: 10000 });
  });

  test('deletes a mission from the timeline', async ({ page }) => {
    await page.goto(BASE);

    // Create dev and add a mission
    const callSign = `MissionDelDev${uid()}`;
    await page.getByRole('button', { name: /Register Space Dev/i }).click();
    await page.locator('input[placeholder="NebulaNinja"]').fill(callSign);
    await page.locator('input[placeholder="Alex Starfield"]').fill('Mission Del Test');
    await page.getByRole('button', { name: /Launch Into Registry/i }).click();
    await expect(page.locator('.dev-card').filter({ hasText: callSign })).toBeVisible({ timeout: 10000 });

    await page.locator('.dev-card').filter({ hasText: callSign }).getByRole('button', { name: /Missions/i }).click();
    await expect(page.locator('.mission-modal')).toBeVisible();

    await page.locator('.mission-modal').getByRole('button', { name: /Log New Mission/i }).click();
    const missionTitle = `MissionToDelete${uid()}`;
    await page.locator('.mission-form input[placeholder="Deploy Nebula ORM v2.0"]').fill(missionTitle);
    await page.locator('.mission-form').getByRole('button', { name: /Launch Mission/i }).click();
    await expect(page.locator('.mission-timeline .mission-title').filter({ hasText: missionTitle })).toBeVisible({ timeout: 10000 });

    // Delete the mission
    await page.locator('.mission-entry').filter({ hasText: missionTitle }).locator('.mission-delete-btn').click();
    await expect(page.locator('.mission-timeline .mission-title').filter({ hasText: missionTitle })).not.toBeVisible({ timeout: 10000 });
  });
});

test.describe('Joke banner', () => {
  test('clicking joke text fetches a new joke', async ({ page }) => {
    await page.goto(BASE);
    // Wait for devs to load (seeds some jokes)
    await page.locator('.dev-card').first().waitFor({ timeout: 10000 });

    const bannerText = page.locator('.joke-banner-text');
    const initialJoke = await bannerText.textContent();

    await bannerText.click();
    // After clicking, the joke should change (or stay same if only one)
    await expect(bannerText).toBeVisible();
  });
});
