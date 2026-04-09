# Phase 1 Design: Data Model

**Date**: 2026-04-09  
**Feature**: Developer Duel Mode  
**Status**: Design phase complete

---

## Domain Entities

### SpaceDeveloper (Existing, Reused)

From `frontend/src/types.ts`:

```typescript
interface SpaceDeveloper {
  id: number
  callSign: string
  realName: string
  seniority: Seniority
  skills: string[]
  ossProjects: string[]
  favoriteDevJoke: string
  coffeesPerDayInLiters: number
  debuggingPowerLevel: number
  rubberDuckName: string
  favoriteKeyboardShortcut: string
  gitCommitStreak: number
  stackOverflowReputation: number
  stillUsesVim: boolean
  shipName: string
}
```

**Duel uses four stat fields**:
- `debuggingPowerLevel` (0–9001)
- `coffeesPerDayInLiters` (0–99)
- `gitCommitStreak` (0–∞)
- `stackOverflowReputation` (0–∞)

---

## Duel-Specific Entities

### DuelMatchup

**Purpose**: Represents the selection of two developers for comparison  
**Scope**: Runtime/transient (not persisted)

```typescript
interface DuelMatchup {
  leftDeveloper: SpaceDeveloper
  rightDeveloper: SpaceDeveloper
  timestamp: Date
}
```

**Constraints**:
- `leftDeveloper.id !== rightDeveloper.id` (no self-duels)
- Both developers must exist and be loaded from API
- All four stat fields must be defined (non-null)

**Lifecycle**:
- Created: when user selects two developers
- Destroyed: when user clicks "New Duel" or navigates away

---

### DuelResult

**Purpose**: Represents the computed outcome of a duel comparison  
**Scope**: Runtime/transient (not persisted)

```typescript
interface DuelResult {
  leftDeveloper: SpaceDeveloper
  rightDeveloper: SpaceDeveloper
  
  // Per-category winners
  categoryWinners: DuelCategoryWinners
  
  // Overall result
  overallWinner: Winner  // 'left' | 'right' | 'draw'
  categoriesWon: CategoriesScore
}

type Winner = 'left' | 'right' | 'draw'

type DuelCategoryWinners = {
  debuggingPowerLevel: Winner
  coffeesPerDayInLiters: Winner
  gitCommitStreak: Winner
  stackOverflowReputation: Winner
}

type CategoriesScore = {
  left: number    // count of categories won (0–4)
  right: number   // count of categories won (0–4)
}
```

**Calculation Rules**:

1. **Category Winner** (per-stat logic)
   - Compare the two developers' stat values
   - Higher value wins the category
   - Equal values → Draw
   - Formula: `left > right ? 'left' : right > left ? 'right' : 'draw'`

2. **Overall Winner** (aggregate logic)
   - Count categories won by each side
   - Side with more categories won → Overall winner
   - Tied count (2-2) → Draw
   - Formula:
     ```
     if (categoriesWon.left > categoriesWon.right) return 'left'
     if (categoriesWon.right > categoriesWon.left) return 'right'
     return 'draw'
     ```

**Example**:
```
Left: debuggingPowerLevel=9000, coffees=50, streak=100, so=15000
Right: debuggingPowerLevel=500, coffees=40, streak=500, so=20000

Category winners:
- debuggingPowerLevel: left (9000 > 500)
- coffeesPerDayInLiters: left (50 > 40)
- gitCommitStreak: right (500 > 100)
- stackOverflowReputation: right (20000 > 15000)

Score: left=2, right=2
Overall: draw
```

**Lifecycle**:
- Created: when user submits matchup selection
- Displayed: comparison view renders with animation sequence
- Referenced: "New Duel" button clears result and returns to selector

---

## Data Transformations

### URL State ↔ DuelMatchup

**Direction**: Query params → DuelMatchup

```typescript
// Input: /duel?left=3&right=7
// Parse: { leftId: 3, rightId: 7 }
// Load developers from API
// Output: DuelMatchup

function parseDuelUrl(searchParams: URLSearchParams): {
  leftId: number | null
  rightId: number | null
}

async function loadMatchupFromUrl(
  searchParams: URLSearchParams,
  developers: SpaceDeveloper[]
): Promise<DuelMatchup | null>
```

**Direction**: DuelMatchup → URL (shareability)

```typescript
function formatShareUrl(matchup: DuelMatchup): string {
  // Output: ?left={leftId}&right={rightId}
}
```

---

## Validation & Error Handling

### Input Validation

| Condition | Action | Error Message |
|-----------|--------|---|
| `leftId === rightId` | Block submission | "Cannot duel yourself!" |
| `leftId` or `rightId` not found in developers | Show error | "One or more developers not found" |
| Either developer has missing stat fields | Skip comparison | "Missing data for this developer" |
| Fewer than 2 developers in registry | Disable selection | "At least 2 developers required" |
| API fails to load developers | Show error + retry | "Failed to load developers. Try again." |

### Success Path

- ✅ Both developers exist
- ✅ Both have all four stats
- ✅ Developers are different
- → Calculate DuelResult
- → Display comparison with animations

---

## State Flow Diagram

```
┌─────────────────────────────────────────┐
│ DuelView Initial Load                   │
│ - Fetch developers from API             │
│ - Check URL params (?left=&right=)      │
└──────────────┬──────────────────────────┘
               │
         ┌─────▼─────┐
         │ URL Params?
         └─────┬─────┘
             YES/NO
               │
       ┌──────┴──────┐
     YES             NO
      │               │
      ▼               ▼
  Load from       Show Selector
  URL Params      (DeveloperSelector)
      │               │
      └───────┬───────┘
              │
              ▼
    ┌─────────────────────┐
    │ User selects 2 devs │
    └──────────┬──────────┘
               │
               ▼
    ┌─────────────────────┐
    │ Calculate DuelResult│
    │ (duelLogic.ts)      │
    └──────────┬──────────┘
               │
               ▼
    ┌─────────────────────┐
    │ Animate & Display   │
    │ (ComparisonDisplay) │
    └──────────┬──────────┘
               │
         ┌─────▼─────┐
         │ User Action
         └─────┬─────┘
       Click "New Duel"
               │
         Reset to Selector
         (loop back to top)
```

---

## Storage & Persistence

**No persistence required**. All entities are runtime/transient:
- DuelMatchup objects created/destroyed within component lifecycle
- DuelResult objects calculated on-demand
- Share URL enables re-loading a specific matchup from URL params
- No backend changes needed; existing SpaceDeveloper API reused

---

## Type Definitions Summary (TypeScript)

File: `frontend/src/types.ts` (add to existing file)

```typescript
// Existing imports
import type { SpaceDeveloper } from './types'

// New duel types
export type Winner = 'left' | 'right' | 'draw'

export interface DuelCategoryWinners {
  debuggingPowerLevel: Winner
  coffeesPerDayInLiters: Winner
  gitCommitStreak: Winner
  stackOverflowReputation: Winner
}

export interface CategoriesScore {
  left: number
  right: number
}

export interface DuelMatchup {
  leftDeveloper: SpaceDeveloper
  rightDeveloper: SpaceDeveloper
  timestamp: Date
}

export interface DuelResult {
  leftDeveloper: SpaceDeveloper
  rightDeveloper: SpaceDeveloper
  categoryWinners: DuelCategoryWinners
  overallWinner: Winner
  categoriesWon: CategoriesScore
}
```

---

## Phase 1 Complete

✅ Data entities defined  
✅ Business logic rules documented  
✅ Type signatures specified  
✅ Validation rules outlined  
✅ Error handling strategy defined  

**Next**: Implementation phase (Phase 2 tasks)
