export interface SpaceDeveloper {
  id?: number;
  callSign: string;
  realName: string;
  seniority: Seniority;
  skills: string[];
  ossProjects: string[];
  favoriteDevJoke: string;
  coffeesPerDayInLiters: number;
  debuggingPowerLevel: number;
  rubberDuckName: string;
  favoriteKeyboardShortcut: string;
  gitCommitStreak: number;
  stackOverflowReputation: number;
  stillUsesVim: boolean;
  shipName: string;
}

export type Seniority =
  | 'MASS_OF_THE_UNIVERSE'
  | 'MASS_OF_A_STAR'
  | 'MASS_OF_A_PLANET'
  | 'MASS_OF_A_MOON'
  | 'MASS_OF_AN_ASTEROID'
  | 'MASS_OF_SPACE_DUST';

export const SENIORITY_LABELS: Record<Seniority, string> = {
  MASS_OF_THE_UNIVERSE: '🌌 Mass of the Universe',
  MASS_OF_A_STAR: '⭐ Mass of a Star',
  MASS_OF_A_PLANET: '🪐 Mass of a Planet',
  MASS_OF_A_MOON: '🌙 Mass of a Moon',
  MASS_OF_AN_ASTEROID: '☄️ Mass of an Asteroid',
  MASS_OF_SPACE_DUST: '✨ Mass of Space Dust',
};
