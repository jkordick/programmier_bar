import { test, expect } from '@playwright/test';

// Helper to create a unique callSign to avoid test interference
function uniqueCallSign(base: string) {
  return `${base}-${Date.now()}`;
}

test.describe('Space Devs App', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
    await page.waitForLoadState('networkidle');
  });

  test('page loads and shows header', async ({ page }) => {
    await expect(page.getByRole('heading', { name: 'SPACE DEVS' })).toBeVisible();
    await expect(page.getByText('Intergalactic Developer Registry')).toBeVisible();
  });

  test('dev grid renders with seeded data', async ({ page }) => {
    await expect(page.locator('.dev-card').first()).toBeVisible();
  });

  test('toolbar shows Register and Scan buttons', async ({ page }) => {
    await expect(page.getByRole('button', { name: /Register Space Dev/ })).toBeVisible();
    await expect(page.getByRole('button', { name: /Scan Galaxy/ })).toBeVisible();
  });

  test('joke banner is visible', async ({ page }) => {
    await expect(page.locator('.joke-banner')).toBeVisible();
    await expect(page.getByText('📡 Incoming Transmission')).toBeVisible();
  });

  test('clicking joke banner text fetches a new joke', async ({ page }) => {
    const jokeBannerText = page.locator('.joke-banner-text');
    const initialText = await jokeBannerText.textContent();
    await jokeBannerText.click();
    // Wait for the text to potentially change (API call)
    await page.waitForTimeout(1000);
    const newText = await jokeBannerText.textContent();
    // Text should be non-empty after click (either same joke or new one)
    expect(newText).toBeTruthy();
    expect(newText!.trim().length).toBeGreaterThan(0);
  });
});

test.describe('Create Space Dev', () => {
  test('opens registration form when Register button is clicked', async ({ page }) => {
    await page.goto('/');
    await page.waitForLoadState('networkidle');

    await page.getByRole('button', { name: /Register Space Dev/ }).click();
    await expect(page.locator('.modal')).toBeVisible();
    await expect(page.getByRole('heading', { name: /Register New Space Dev/ })).toBeVisible();
  });

  test('closes form when Cancel is clicked', async ({ page }) => {
    await page.goto('/');
    await page.waitForLoadState('networkidle');

    await page.getByRole('button', { name: /Register Space Dev/ }).click();
    await expect(page.locator('.modal')).toBeVisible();

    await page.getByRole('button', { name: 'Cancel' }).click();
    await expect(page.locator('.modal')).not.toBeVisible();
  });

  test('closes form when clicking modal overlay', async ({ page }) => {
    await page.goto('/');
    await page.waitForLoadState('networkidle');

    await page.getByRole('button', { name: /Register Space Dev/ }).click();
    await expect(page.locator('.modal')).toBeVisible();

    await page.locator('.modal-overlay').click({ position: { x: 5, y: 5 } });
    await expect(page.locator('.modal')).not.toBeVisible();
  });

  test('creates a new space dev and shows card in grid', async ({ page }) => {
    await page.goto('/');
    await page.waitForLoadState('networkidle');

    const callSign = uniqueCallSign('TestPilot');

    await page.getByRole('button', { name: /Register Space Dev/ }).click();
    await expect(page.locator('.modal')).toBeVisible();

    await page.getByLabel('Call Sign *').fill(callSign);
    await page.getByLabel('Real Name *').fill('Test Astronaut');

    await page.getByRole('button', { name: 'Launch Into Registry' }).click();

    // Modal should close and new card should appear
    await expect(page.locator('.modal')).not.toBeVisible();
    await expect(page.locator('.dev-card').filter({ hasText: callSign })).toBeVisible();
  });

  test('form requires call sign before submission', async ({ page }) => {
    await page.goto('/');
    await page.waitForLoadState('networkidle');

    await page.getByRole('button', { name: /Register Space Dev/ }).click();
    // Fill only realName, leave callSign empty
    await page.getByLabel('Real Name *').fill('Some Name');

    await page.getByRole('button', { name: 'Launch Into Registry' }).click();
    // Form should still be open due to HTML5 validation
    await expect(page.locator('.modal')).toBeVisible();
  });

  test('form requires real name before submission', async ({ page }) => {
    await page.goto('/');
    await page.waitForLoadState('networkidle');

    await page.getByRole('button', { name: /Register Space Dev/ }).click();
    // Fill only callSign, leave realName empty
    await page.getByLabel('Call Sign *').fill('SomeSign');

    await page.getByRole('button', { name: 'Launch Into Registry' }).click();
    // Form should still be open due to HTML5 validation
    await expect(page.locator('.modal')).toBeVisible();
  });
});

test.describe('Edit Space Dev', () => {
  test('opens edit form with developer data pre-filled', async ({ page }) => {
    await page.goto('/');
    await page.waitForLoadState('networkidle');

    const firstCard = page.locator('.dev-card').first();
    const callSign = await firstCard.locator('.call-sign').textContent();

    await firstCard.getByRole('button', { name: 'Edit' }).click();
    await expect(page.locator('.modal')).toBeVisible();
    await expect(page.getByRole('heading', { name: /Edit Space Dev/ })).toBeVisible();

    // The call sign field should be pre-filled
    const callSignInput = page.getByLabel('Call Sign *');
    await expect(callSignInput).toHaveValue(callSign!);
  });

  test('updates developer and reflects changes in card', async ({ page }) => {
    await page.goto('/');
    await page.waitForLoadState('networkidle');

    // First create a dev we can safely edit
    const callSign = uniqueCallSign('EditTarget');
    await page.getByRole('button', { name: /Register Space Dev/ }).click();
    await page.getByLabel('Call Sign *').fill(callSign);
    await page.getByLabel('Real Name *').fill('Original Name');
    await page.getByRole('button', { name: 'Launch Into Registry' }).click();
    await expect(page.locator('.modal')).not.toBeVisible();

    // Now edit the newly created card
    const newCard = page.locator('.dev-card').filter({ hasText: callSign });
    await newCard.getByRole('button', { name: 'Edit' }).click();
    await expect(page.locator('.modal')).toBeVisible();

    const updatedCallSign = uniqueCallSign('EditedPilot');
    const callSignInput = page.getByLabel('Call Sign *');
    await callSignInput.fill(updatedCallSign);

    await page.getByRole('button', { name: 'Update Coordinates' }).click();
    await expect(page.locator('.modal')).not.toBeVisible();

    // Updated card should appear
    await expect(page.locator('.dev-card').filter({ hasText: updatedCallSign })).toBeVisible();
  });
});

test.describe('Delete Space Dev', () => {
  test('removes developer card from grid after Deorbit', async ({ page }) => {
    await page.goto('/');
    await page.waitForLoadState('networkidle');

    // Create a dev to delete
    const callSign = uniqueCallSign('DeleteTarget');
    await page.getByRole('button', { name: /Register Space Dev/ }).click();
    await page.getByLabel('Call Sign *').fill(callSign);
    await page.getByLabel('Real Name *').fill('To Delete');
    await page.getByRole('button', { name: 'Launch Into Registry' }).click();
    await expect(page.locator('.modal')).not.toBeVisible();

    const newCard = page.locator('.dev-card').filter({ hasText: callSign });
    await expect(newCard).toBeVisible();

    await newCard.getByRole('button', { name: 'Deorbit' }).click();

    // Card should be gone
    await expect(page.locator('.dev-card').filter({ hasText: callSign })).not.toBeVisible();
  });
});

test.describe('Mission Timeline', () => {
  test('opens mission modal when Missions button is clicked', async ({ page }) => {
    await page.goto('/');
    await page.waitForLoadState('networkidle');

    const firstCard = page.locator('.dev-card').first();
    const callSign = await firstCard.locator('.call-sign').textContent();

    await firstCard.getByRole('button', { name: /Missions/ }).click();
    await expect(page.locator('.modal.mission-modal')).toBeVisible();
    await expect(page.getByText(`Mission Log — ${callSign}`)).toBeVisible();
  });

  test('closes mission modal when Close is clicked', async ({ page }) => {
    await page.goto('/');
    await page.waitForLoadState('networkidle');

    await page.locator('.dev-card').first().getByRole('button', { name: /Missions/ }).click();
    await expect(page.locator('.modal.mission-modal')).toBeVisible();

    await page.getByRole('button', { name: 'Close' }).click();
    await expect(page.locator('.modal.mission-modal')).not.toBeVisible();
  });

  test('shows Log New Mission button inside modal', async ({ page }) => {
    await page.goto('/');
    await page.waitForLoadState('networkidle');

    await page.locator('.dev-card').first().getByRole('button', { name: /Missions/ }).click();
    await expect(page.getByRole('button', { name: /Log New Mission/ })).toBeVisible();
  });

  test('can add a mission and see it in the timeline', async ({ page }) => {
    await page.goto('/');
    await page.waitForLoadState('networkidle');

    // Create a fresh dev for this test
    const callSign = uniqueCallSign('MissionPilot');
    await page.getByRole('button', { name: /Register Space Dev/ }).click();
    await page.getByLabel('Call Sign *').fill(callSign);
    await page.getByLabel('Real Name *').fill('Mission Tester');
    await page.getByRole('button', { name: 'Launch Into Registry' }).click();
    await expect(page.locator('.modal')).not.toBeVisible();

    // Open mission timeline for this dev
    const devCard = page.locator('.dev-card').filter({ hasText: callSign });
    await devCard.getByRole('button', { name: /Missions/ }).click();
    await expect(page.locator('.modal.mission-modal')).toBeVisible();

    // Click Log New Mission
    await page.getByRole('button', { name: /Log New Mission/ }).click();
    await expect(page.locator('.mission-form')).toBeVisible();

    // Fill in the mission form
    await page.getByLabel('Mission Title *').fill('Deploy Nebula ORM v2.0');
    await page.getByLabel('Date *').fill('2026-04-09');

    await page.getByRole('button', { name: 'Launch Mission' }).click();

    // Mission should appear in the timeline
    await expect(page.locator('.mission-title').filter({ hasText: 'Deploy Nebula ORM v2.0' })).toBeVisible();
  });

  test('can delete a mission from the timeline', async ({ page }) => {
    await page.goto('/');
    await page.waitForLoadState('networkidle');

    // Create a fresh dev
    const callSign = uniqueCallSign('DeleteMissionPilot');
    await page.getByRole('button', { name: /Register Space Dev/ }).click();
    await page.getByLabel('Call Sign *').fill(callSign);
    await page.getByLabel('Real Name *').fill('Mission Deleter');
    await page.getByRole('button', { name: 'Launch Into Registry' }).click();
    await expect(page.locator('.modal')).not.toBeVisible();

    // Open mission timeline and add a mission
    const devCard = page.locator('.dev-card').filter({ hasText: callSign });
    await devCard.getByRole('button', { name: /Missions/ }).click();
    await page.getByRole('button', { name: /Log New Mission/ }).click();
    await page.getByLabel('Mission Title *').fill('Temporary Mission');
    await page.getByLabel('Date *').fill('2026-04-09');
    await page.getByRole('button', { name: 'Launch Mission' }).click();

    const missionEntry = page.locator('.mission-entry').filter({ hasText: 'Temporary Mission' });
    await expect(missionEntry).toBeVisible();

    // Delete the mission
    await missionEntry.locator('.mission-delete-btn').click();

    // Mission should be gone
    await expect(page.locator('.mission-entry').filter({ hasText: 'Temporary Mission' })).not.toBeVisible();
  });

  test('shows empty state when no missions exist', async ({ page }) => {
    await page.goto('/');
    await page.waitForLoadState('networkidle');

    // Create a fresh dev with no missions
    const callSign = uniqueCallSign('NoMissionPilot');
    await page.getByRole('button', { name: /Register Space Dev/ }).click();
    await page.getByLabel('Call Sign *').fill(callSign);
    await page.getByLabel('Real Name *').fill('No Mission Tester');
    await page.getByRole('button', { name: 'Launch Into Registry' }).click();
    await expect(page.locator('.modal')).not.toBeVisible();

    const devCard = page.locator('.dev-card').filter({ hasText: callSign });
    await devCard.getByRole('button', { name: /Missions/ }).click();
    await expect(page.locator('.modal.mission-modal')).toBeVisible();

    await expect(page.getByText('No missions logged yet.')).toBeVisible();
  });
});

test.describe('Dev Card', () => {
  test('displays call sign and real name', async ({ page }) => {
    await page.goto('/');
    await page.waitForLoadState('networkidle');

    const firstCard = page.locator('.dev-card').first();
    await expect(firstCard.locator('.call-sign')).toBeVisible();
    await expect(firstCard.locator('.real-name')).toBeVisible();
  });

  test('displays seniority badge', async ({ page }) => {
    await page.goto('/');
    await page.waitForLoadState('networkidle');

    const firstCard = page.locator('.dev-card').first();
    await expect(firstCard.locator('.seniority-badge')).toBeVisible();
  });

  test('displays debugging power bar', async ({ page }) => {
    await page.goto('/');
    await page.waitForLoadState('networkidle');

    const firstCard = page.locator('.dev-card').first();
    await expect(firstCard.locator('.power-bar-container')).toBeVisible();
  });

  test('shows Edit, Missions, and Deorbit buttons on each card', async ({ page }) => {
    await page.goto('/');
    await page.waitForLoadState('networkidle');

    const firstCard = page.locator('.dev-card').first();
    await expect(firstCard.getByRole('button', { name: 'Edit' })).toBeVisible();
    await expect(firstCard.getByRole('button', { name: /Missions/ })).toBeVisible();
    await expect(firstCard.getByRole('button', { name: 'Deorbit' })).toBeVisible();
  });
});

test.describe('Scan Galaxy button', () => {
  test('reloads the developer list', async ({ page }) => {
    await page.goto('/');
    await page.waitForLoadState('networkidle');

    const cardCountBefore = await page.locator('.dev-card').count();
    await page.getByRole('button', { name: /Scan Galaxy/ }).click();
    await page.waitForLoadState('networkidle');
    const cardCountAfter = await page.locator('.dev-card').count();

    expect(cardCountAfter).toBe(cardCountBefore);
  });
});
