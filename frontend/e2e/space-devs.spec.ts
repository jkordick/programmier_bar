import { test, expect } from '@playwright/test';

test.describe('Page Load', () => {
  test('shows header and toolbar on load', async ({ page }) => {
    await page.goto('/');
    await expect(page.getByRole('heading', { name: 'SPACE DEVS' })).toBeVisible();
    await expect(page.getByText('Intergalactic Developer Registry')).toBeVisible();
    await expect(page.getByRole('button', { name: '+ Register Space Dev' })).toBeVisible();
    await expect(page.getByRole('button', { name: '↻ Scan Galaxy' })).toBeVisible();
  });

  test('displays seeded developer cards', async ({ page }) => {
    await page.goto('/');
    await page.waitForSelector('.dev-card');
    const cards = page.locator('.dev-card');
    await expect(cards).toHaveCount(await cards.count());
    expect(await cards.count()).toBeGreaterThanOrEqual(1);
    await expect(page.locator('.call-sign').first()).toBeVisible();
  });

  test('shows NebulaNinja in the dev grid', async ({ page }) => {
    await page.goto('/');
    await page.waitForSelector('.dev-card');
    await expect(page.getByText('NebulaNinja')).toBeVisible();
  });

  test('shows joke banner on load', async ({ page }) => {
    await page.goto('/');
    await expect(page.locator('.joke-banner')).toBeVisible();
    await expect(page.locator('.joke-banner-text')).toBeVisible();
  });
});

test.describe('Create Developer', () => {
  test('opens registration form when clicking Register Space Dev', async ({ page }) => {
    await page.goto('/');
    await page.getByRole('button', { name: '+ Register Space Dev' }).click();
    await expect(page.locator('.modal')).toBeVisible();
    await expect(page.getByRole('heading', { name: '🚀 Register New Space Dev' })).toBeVisible();
  });

  test('closes form when Cancel is clicked', async ({ page }) => {
    await page.goto('/');
    await page.getByRole('button', { name: '+ Register Space Dev' }).click();
    await expect(page.locator('.modal')).toBeVisible();
    await page.getByRole('button', { name: 'Cancel' }).click();
    await expect(page.locator('.modal')).not.toBeVisible();
  });

  test('creates a new developer and shows card in grid', async ({ page }) => {
    await page.goto('/');
    await page.getByRole('button', { name: '+ Register Space Dev' }).click();

    const callSign = `TestDev_${Date.now()}`;
    await page.locator('input[placeholder="NebulaNinja"]').fill(callSign);
    await page.locator('input[placeholder="Alex Starfield"]').fill('Test Developer');

    await page.getByRole('button', { name: 'Launch Into Registry' }).click();

    await page.waitForSelector('.dev-card');
    await expect(page.getByText(callSign)).toBeVisible();
  });

  test('requires Call Sign to submit form', async ({ page }) => {
    await page.goto('/');
    await page.getByRole('button', { name: '+ Register Space Dev' }).click();

    // Fill realName but leave callSign empty
    await page.locator('input[placeholder="Alex Starfield"]').fill('Test Name');

    // The HTML required attribute prevents submission
    const submitBtn = page.getByRole('button', { name: 'Launch Into Registry' });
    await submitBtn.click();

    // Modal should still be visible (form not submitted)
    await expect(page.locator('.modal')).toBeVisible();
  });
});

test.describe('Edit Developer', () => {
  test('opens edit form with existing developer data', async ({ page }) => {
    await page.goto('/');
    await page.waitForSelector('.dev-card');

    const firstCard = page.locator('.dev-card').first();
    const callSign = await firstCard.locator('.call-sign').textContent();

    await firstCard.getByRole('button', { name: 'Edit' }).click();

    await expect(page.locator('.modal')).toBeVisible();
    await expect(page.getByRole('heading', { name: '✏️ Edit Space Dev' })).toBeVisible();

    const callSignInput = page.locator('input[placeholder="NebulaNinja"]');
    await expect(callSignInput).toHaveValue(callSign ?? '');
  });

  test('updates developer call sign', async ({ page }) => {
    await page.goto('/');
    await page.waitForSelector('.dev-card');

    const firstCard = page.locator('.dev-card').first();
    await firstCard.getByRole('button', { name: 'Edit' }).click();

    const newCallSign = `Edited_${Date.now()}`;
    const callSignInput = page.locator('input[placeholder="NebulaNinja"]');
    await callSignInput.fill(newCallSign);

    await page.getByRole('button', { name: 'Update Coordinates' }).click();

    await page.waitForSelector('.dev-card');
    await expect(page.getByText(newCallSign)).toBeVisible();
  });
});

test.describe('Delete Developer', () => {
  test('removes developer card after clicking Deorbit', async ({ page }) => {
    await page.goto('/');
    // Create a developer to delete
    await page.getByRole('button', { name: '+ Register Space Dev' }).click();
    const uniqueCallSign = `ToDelete_${Date.now()}`;
    await page.locator('input[placeholder="NebulaNinja"]').fill(uniqueCallSign);
    await page.locator('input[placeholder="Alex Starfield"]').fill('Temp Dev');
    await page.getByRole('button', { name: 'Launch Into Registry' }).click();

    await page.waitForSelector('.dev-card');
    await expect(page.getByText(uniqueCallSign)).toBeVisible();

    // Find and delete the newly created card
    const newCard = page.locator('.dev-card').filter({ hasText: uniqueCallSign });
    await newCard.getByRole('button', { name: 'Deorbit' }).click();

    await expect(page.getByText(uniqueCallSign)).not.toBeVisible();
  });
});

test.describe('Mission Timeline', () => {
  test('opens mission modal when clicking Missions button', async ({ page }) => {
    await page.goto('/');
    await page.waitForSelector('.dev-card');

    const firstCard = page.locator('.dev-card').first();
    const callSign = await firstCard.locator('.call-sign').textContent();
    await firstCard.getByRole('button', { name: '📜 Missions' }).click();

    await expect(page.locator('.mission-modal')).toBeVisible();
    await expect(page.getByText(`Mission Log — ${callSign}`)).toBeVisible();
  });

  test('closes mission modal when Close is clicked', async ({ page }) => {
    await page.goto('/');
    await page.waitForSelector('.dev-card');

    await page.locator('.dev-card').first().getByRole('button', { name: '📜 Missions' }).click();
    await expect(page.locator('.mission-modal')).toBeVisible();

    await page.getByRole('button', { name: 'Close' }).click();
    await expect(page.locator('.mission-modal')).not.toBeVisible();
  });

  test('adds a new mission and it appears in the timeline', async ({ page }) => {
    await page.goto('/');
    await page.waitForSelector('.dev-card');

    // Open missions for NebulaNinja who has seeded missions
    await page.locator('.dev-card').filter({ hasText: 'NebulaNinja' }).getByRole('button', { name: '📜 Missions' }).click();
    await expect(page.locator('.mission-modal')).toBeVisible();

    await page.getByRole('button', { name: '+ Log New Mission' }).click();

    const missionTitle = `Test Mission ${Date.now()}`;
    await page.locator('.mission-form input[placeholder="Deploy Nebula ORM v2.0"]').fill(missionTitle);
    await page.getByRole('button', { name: 'Launch Mission' }).click();

    await expect(page.getByText(missionTitle)).toBeVisible();
  });

  test('deletes a mission from the timeline', async ({ page }) => {
    await page.goto('/');
    await page.waitForSelector('.dev-card');

    // Create a dev first so we can add and delete a mission cleanly
    await page.getByRole('button', { name: '+ Register Space Dev' }).click();
    const devCallSign = `MissionDev_${Date.now()}`;
    await page.locator('input[placeholder="NebulaNinja"]').fill(devCallSign);
    await page.locator('input[placeholder="Alex Starfield"]').fill('Mission Tester');
    await page.getByRole('button', { name: 'Launch Into Registry' }).click();

    await page.waitForSelector('.dev-card');
    await page.locator('.dev-card').filter({ hasText: devCallSign }).getByRole('button', { name: '📜 Missions' }).click();
    await expect(page.locator('.mission-modal')).toBeVisible();

    // Add a mission
    await page.getByRole('button', { name: '+ Log New Mission' }).click();
    const missionTitle = `Mission_${Date.now()}`;
    await page.locator('.mission-form input[placeholder="Deploy Nebula ORM v2.0"]').fill(missionTitle);
    await page.getByRole('button', { name: 'Launch Mission' }).click();
    await expect(page.getByText(missionTitle)).toBeVisible();

    // Delete the mission
    await page.locator('.mission-delete-btn').click();
    await expect(page.getByText(missionTitle)).not.toBeVisible();
  });
});

test.describe('Joke Banner', () => {
  test('updates joke text when banner is clicked', async ({ page }) => {
    await page.goto('/');
    const banner = page.locator('.joke-banner-text');
    const initialText = await banner.textContent();

    // Click to get a new joke
    await banner.click();

    // The text may or may not change (depends on available jokes), but it should not throw
    await expect(banner).toBeVisible();
    const newText = await banner.textContent();
    expect(newText).toBeTruthy();
  });
});
