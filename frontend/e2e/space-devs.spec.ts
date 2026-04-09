import { test, expect } from '@playwright/test';

// Helper: create a dev via form and return its call sign
async function registerDev(page: any, callSign: string, realName: string) {
  await page.getByRole('button', { name: '+ Register Space Dev' }).click();
  await page.getByLabel('Call Sign *').fill(callSign);
  await page.getByLabel('Real Name *').fill(realName);
  await page.getByRole('button', { name: 'Launch Into Registry' }).click();
  await expect(page.locator('.modal-overlay')).not.toBeVisible();
}

test.describe('Page load', () => {
  test('shows the SPACE DEVS header', async ({ page }) => {
    await page.goto('/');
    await expect(page.locator('h1')).toHaveText('SPACE DEVS');
    await expect(page.getByText('Intergalactic Developer Registry')).toBeVisible();
  });

  test('renders at least one dev card from seeded data', async ({ page }) => {
    await page.goto('/');
    await page.waitForSelector('.dev-card');
    const cards = page.locator('.dev-card');
    expect(await cards.count()).toBeGreaterThan(0);
  });

  test('shows the joke banner', async ({ page }) => {
    await page.goto('/');
    await expect(page.locator('.joke-banner')).toBeVisible();
    await expect(page.getByText('📡 Incoming Transmission')).toBeVisible();
  });
});

test.describe('Register (create) flow', () => {
  test('opens form when clicking Register Space Dev', async ({ page }) => {
    await page.goto('/');
    await page.getByRole('button', { name: '+ Register Space Dev' }).click();
    await expect(page.locator('.modal')).toBeVisible();
    await expect(page.getByText('🚀 Register New Space Dev')).toBeVisible();
  });

  test('cancels registration without creating dev', async ({ page }) => {
    await page.goto('/');
    await page.waitForSelector('.dev-card');
    const countBefore = await page.locator('.dev-card').count();

    await page.getByRole('button', { name: '+ Register Space Dev' }).click();
    await page.getByRole('button', { name: 'Cancel' }).click();
    await expect(page.locator('.modal-overlay')).not.toBeVisible();

    const countAfter = await page.locator('.dev-card').count();
    expect(countAfter).toBe(countBefore);
  });

  test('registers new dev and shows card in grid', async ({ page }) => {
    await page.goto('/');
    await page.waitForSelector('.dev-card');

    await registerDev(page, 'E2ETestPilot', 'E2E Tester');

    await expect(page.locator('.dev-card').filter({ hasText: 'E2ETestPilot' })).toBeVisible();
  });

  test('form requires call sign', async ({ page }) => {
    await page.goto('/');
    await page.getByRole('button', { name: '+ Register Space Dev' }).click();
    await page.getByLabel('Real Name *').fill('No Sign Dev');
    await page.getByRole('button', { name: 'Launch Into Registry' }).click();
    // HTML5 validation should prevent submission - modal stays open
    await expect(page.locator('.modal')).toBeVisible();
  });

  test('form requires real name', async ({ page }) => {
    await page.goto('/');
    await page.getByRole('button', { name: '+ Register Space Dev' }).click();
    await page.getByLabel('Call Sign *').fill('SignOnly');
    await page.getByRole('button', { name: 'Launch Into Registry' }).click();
    // HTML5 validation prevents submission - modal stays open
    await expect(page.locator('.modal')).toBeVisible();
  });
});

test.describe('Edit flow', () => {
  test('opens edit form pre-populated with dev data', async ({ page }) => {
    await page.goto('/');
    await page.waitForSelector('.dev-card');

    const firstCard = page.locator('.dev-card').first();
    const callSignText = await firstCard.locator('.call-sign').textContent();

    await firstCard.getByRole('button', { name: 'Edit' }).click();
    await expect(page.locator('.modal')).toBeVisible();
    await expect(page.getByText('✏️ Edit Space Dev')).toBeVisible();
    await expect(page.getByLabel('Call Sign *')).toHaveValue(callSignText!);
  });

  test('updates dev and shows new data in card', async ({ page }) => {
    await page.goto('/');
    await page.waitForSelector('.dev-card');

    // Register a dev to edit so we don't pollute other tests
    await registerDev(page, 'EditTarget', 'Edit Me Please');

    const targetCard = page.locator('.dev-card').filter({ hasText: 'EditTarget' });
    await targetCard.getByRole('button', { name: 'Edit' }).click();

    await page.getByLabel('Call Sign *').fill('EditedCallSign');
    await page.getByRole('button', { name: 'Update Coordinates' }).click();

    await expect(page.locator('.modal-overlay')).not.toBeVisible();
    await expect(page.locator('.dev-card').filter({ hasText: 'EditedCallSign' })).toBeVisible();
  });
});

test.describe('Delete (Deorbit) flow', () => {
  test('removes dev card after clicking Deorbit', async ({ page }) => {
    await page.goto('/');
    await page.waitForSelector('.dev-card');

    // Register a dev to delete
    await registerDev(page, 'ToBeDeorbited', 'Doomed Dev');

    const card = page.locator('.dev-card').filter({ hasText: 'ToBeDeorbited' });
    await expect(card).toBeVisible();

    await card.getByRole('button', { name: 'Deorbit' }).click();
    await expect(page.locator('.dev-card').filter({ hasText: 'ToBeDeorbited' })).not.toBeVisible();
  });
});

test.describe('Mission timeline', () => {
  test('opens mission modal for a dev', async ({ page }) => {
    await page.goto('/');
    await page.waitForSelector('.dev-card');

    await page.locator('.dev-card').first().getByRole('button', { name: '📜 Missions' }).click();
    await expect(page.locator('.mission-modal')).toBeVisible();
    await expect(page.getByText(/Mission Log/)).toBeVisible();
  });

  test('closes mission modal', async ({ page }) => {
    await page.goto('/');
    await page.waitForSelector('.dev-card');

    await page.locator('.dev-card').first().getByRole('button', { name: '📜 Missions' }).click();
    await expect(page.locator('.mission-modal')).toBeVisible();

    await page.getByRole('button', { name: 'Close' }).click();
    await expect(page.locator('.mission-modal')).not.toBeVisible();
  });

  test('adds a mission and shows it in the timeline', async ({ page }) => {
    await page.goto('/');
    await page.waitForSelector('.dev-card');

    // Create a fresh dev to avoid polluting seeded data
    await registerDev(page, 'MissionDev', 'Mission Tester');
    const card = page.locator('.dev-card').filter({ hasText: 'MissionDev' });
    await card.getByRole('button', { name: '📜 Missions' }).click();

    await page.getByRole('button', { name: '+ Log New Mission' }).click();
    await page.getByLabel('Mission Title *').fill('Test Mission Alpha');
    await page.getByRole('button', { name: 'Launch Mission' }).click();

    await expect(page.locator('.mission-timeline')).toBeVisible();
    await expect(page.locator('.mission-title').filter({ hasText: 'Test Mission Alpha' })).toBeVisible();
  });

  test('deletes a mission from the timeline', async ({ page }) => {
    await page.goto('/');
    await page.waitForSelector('.dev-card');

    // Create dev and mission
    await registerDev(page, 'DeleteMissionDev', 'Delete Mission Tester');
    const card = page.locator('.dev-card').filter({ hasText: 'DeleteMissionDev' });
    await card.getByRole('button', { name: '📜 Missions' }).click();

    await page.getByRole('button', { name: '+ Log New Mission' }).click();
    await page.getByLabel('Mission Title *').fill('Mission To Delete');
    await page.getByRole('button', { name: 'Launch Mission' }).click();

    await expect(page.locator('.mission-title').filter({ hasText: 'Mission To Delete' })).toBeVisible();

    await page.locator('.mission-delete-btn').click();
    await expect(page.locator('.mission-title').filter({ hasText: 'Mission To Delete' })).not.toBeVisible();
  });
});

test.describe('Scan Galaxy (refresh)', () => {
  test('refresh button reloads the dev list', async ({ page }) => {
    await page.goto('/');
    await page.waitForSelector('.dev-card');

    await page.getByRole('button', { name: '↻ Scan Galaxy' }).click();
    await page.waitForSelector('.dev-card');
    const cards = page.locator('.dev-card');
    expect(await cards.count()).toBeGreaterThan(0);
  });
});
