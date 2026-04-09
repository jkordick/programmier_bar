import type { SpaceDeveloper, Mission, LeaderboardSortField, SortOrder, Seniority } from './types';

const BASE = '/api/space-devs';

export async function fetchDevs(): Promise<SpaceDeveloper[]> {
  const res = await fetch(BASE);
  if (!res.ok) throw new Error('Failed to fetch developers');
  return res.json();
}

export async function fetchLeaderboard(
  sortBy: LeaderboardSortField,
  order: SortOrder,
  seniorities: Seniority[],
  skill: string
): Promise<SpaceDeveloper[]> {
  const params = new URLSearchParams();
  params.set('sortBy', sortBy);
  params.set('order', order);
  seniorities.forEach(s => params.append('seniority', s));
  if (skill.trim()) params.set('skill', skill.trim());
  const res = await fetch(`${BASE}?${params}`);
  if (!res.ok) throw new Error('Failed to fetch leaderboard');
  return res.json();
}

export async function fetchDev(id: number): Promise<SpaceDeveloper> {
  const res = await fetch(`${BASE}/${id}`);
  if (!res.ok) throw new Error('Developer not found');
  return res.json();
}

export async function createDev(dev: SpaceDeveloper): Promise<SpaceDeveloper> {
  const res = await fetch(BASE, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(dev),
  });
  if (!res.ok) throw new Error('Failed to create developer');
  return res.json();
}

export async function updateDev(id: number, dev: SpaceDeveloper): Promise<SpaceDeveloper> {
  const res = await fetch(`${BASE}/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(dev),
  });
  if (!res.ok) throw new Error('Failed to update developer');
  return res.json();
}

export async function deleteDev(id: number): Promise<void> {
  const res = await fetch(`${BASE}/${id}`, { method: 'DELETE' });
  if (!res.ok) throw new Error('Failed to delete developer');
}

export async function fetchRandomJoke(): Promise<string> {
  const res = await fetch(`${BASE}/random-joke`);
  if (!res.ok) throw new Error('No jokes available');
  return res.text();
}

export async function generateJoke(callSign: string, skills: string): Promise<string> {
  const params = new URLSearchParams({ callSign, skills });
  const res = await fetch(`${BASE}/generate-joke?${params}`);
  if (!res.ok) throw new Error('Failed to generate joke');
  return res.text();
}

export async function fetchMissions(devId: number): Promise<Mission[]> {
  const res = await fetch(`${BASE}/${devId}/missions`);
  if (!res.ok) throw new Error('Failed to fetch missions');
  return res.json();
}

export async function createMission(devId: number, mission: Mission): Promise<Mission> {
  const res = await fetch(`${BASE}/${devId}/missions`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(mission),
  });
  if (!res.ok) throw new Error('Failed to create mission');
  return res.json();
}

export async function deleteMission(devId: number, missionId: number): Promise<void> {
  const res = await fetch(`${BASE}/${devId}/missions/${missionId}`, { method: 'DELETE' });
  if (!res.ok) throw new Error('Failed to delete mission');
}
