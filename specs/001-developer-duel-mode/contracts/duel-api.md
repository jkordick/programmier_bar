# Interface Contracts: Duel Feature

**Date**: 2026-04-09  
**Feature**: Developer Duel Mode  
**Scope**: Frontend-only; internal component APIs (no backend contract changes)

---

## Component Interface Contracts

### DuelView (Page Component)

**Purpose**: Main page component for the `/duel` route  
**Responsibility**: Orchestrate developer loading, selection, and result display

**Props**: None (self-contained, uses React Router hooks)

```typescript
interface DuelViewProps {}

// Route binding:
// <Route path="/duel" element={<DuelView />} />

// Manages:
// - Loading developers from existing fetchDevs() API
// - Parsing URL query parameters (?left=X&right=Y)
// - Rendering DeveloperSelector or ComparisonDisplay
// - Handling selection and new duel flows
```

**Public Methods/Hooks**:
- `useState<DuelResult | null>` — Current duel result (null while selecting)
- `useState<SpaceDeveloper[]>` — Loaded developers
- `useSearchParams()` — Read URL query params for shareability

**Outputs**:
- DOM: Page with header, selector or comparison display
- URL state: Updates query params when duel starts
- Navigation: No navigation outside `/duel` (internal feature)

---

### DeveloperSelector

**Purpose**: Allows user to select two developers for a duel  
**Responsibility**: Validation, UX for pick-two flow, self-duel prevention

**Props**:

```typescript
interface DeveloperSelectorProps {
  developers: SpaceDeveloper[]
  onSelect: (left: SpaceDeveloper, right: SpaceDeveloper) => void
  loading?: boolean
  disabled?: boolean
}
```

**Behavior**:
- Displays two dropdown selects (left and right)
- "Start Duel" button disabled until both selections differ
- Calls `onSelect(left, right)` when button clicked
- Shows "Cannot duel yourself!" alert if left === right
- Shows "At least 2 developers required" if `developers.length < 2`

**Outputs**:
- Callback: `onSelect(left, right)` with two different SpaceDevs

---

### ComparisonDisplay

**Purpose**: Displays duel result with animated stat comparison  
**Responsibility**: Rendering result, triggering animations, new duel flow

**Props**:

```typescript
interface ComparisonDisplayProps {
  result: DuelResult
  onNewDuel: () => void
}
```

**Behavior**:
- Renders overall winner banner (with trophy emoji for left/right, "Draw!" message for ties)
- Renders developer name cards (left and right)
- Renders stat rows with sequential animations:
  - Each stat reveals 100ms apart
  - Winner in each category highlighted (green glow + bold)
  - Draws shown with yellow background
- "New Duel" button at bottom calls `onNewDuel()`

**Animations** (CSS-driven, @keyframes):
- Stat reveal: `duel-stat-reveal` (0.4s ease-in-out, opacity 0→1)
- Delay: `animation-delay: ${index * 100}ms` (stagger effect)

**Outputs**:
- Callback: `onNewDuel()` to reset selection view
- DOM: Full comparison display with animations

---

### DeveloperSelector & ComparisonDisplay Combined

**Pattern**: Toggle between selector and comparison based on `result` state

```typescript
// In DuelView:
{result ? (
  <ComparisonDisplay result={result} onNewDuel={() => setResult(null)} />
) : (
  <DeveloperSelector developers={developers} onSelect={handleSelect} />
)}
```

---

## Utility Module: duelLogic

**File**: `frontend/src/utils/duelLogic.ts`

**Purpose**: Pure business logic for calculating duel outcomes  
**Responsibility**: Stat comparison rules, overall winner determination, URL parsing

### calculateCategoryWinner()

```typescript
function calculateCategoryWinner(
  leftValue: number,
  rightValue: number
): Winner  // 'left' | 'right' | 'draw'
```

**Contract**:
- Input: Two numeric stat values (non-negative)
- Output: Winner of that category ('left', 'right', or 'draw')
- Rule: `leftValue > rightValue ? 'left' : rightValue > leftValue ? 'right' : 'draw'`
- **No side effects** (pure function)

---

### calculateDuelResult()

```typescript
function calculateDuelResult(
  left: SpaceDeveloper,
  right: SpaceDeveloper
): DuelResult
```

**Contract**:
- Input: Two SpaceDeveloper objects with all 4 stat fields defined
- Output: Full DuelResult with category winners and overall winner
- Logic:
  1. Call `calculateCategoryWinner()` for each of 4 stats
  2. Count categories won by each side
  3. Compare counts to determine overall winner
- **No side effects** (pure function)
- **Precondition**: `left.id !== right.id` (validation happens in component)

**Example Return**:
```typescript
{
  leftDeveloper: { ...left },
  rightDeveloper: { ...right },
  categoryWinners: {
    debuggingPowerLevel: 'left',
    coffeesPerDayInLiters: 'left',
    gitCommitStreak: 'right',
    stackOverflowReputation: 'right',
  },
  overallWinner: 'draw',
  categoriesWon: { left: 2, right: 2 }
}
```

---

### parseDuelUrl()

```typescript
function parseDuelUrl(searchParams: URLSearchParams): {
  leftId: number | null
  rightId: number | null
}
```

**Contract**:
- Input: URLSearchParams object from React Router's useSearchParams()
- Output: Object with extracted developer IDs (null if not present)
- Example input: `new URLSearchParams("?left=3&right=7")`
- Example output: `{ leftId: 3, rightId: 7 }`
- **No side effects** (pure function)

---

### formatShareUrl()

```typescript
function formatShareUrl(
  left: SpaceDeveloper,
  right: SpaceDeveloper
): string
```

**Contract**:
- Input: Two SpaceDeveloper objects
- Output: Formatted URL string ready for sharing (e.g., `/duel?left=3&right=7`)
- Used to update browser URL on duel start
- **No side effects** (pure function)

---

## Type Definitions

**File**: `frontend/src/types.ts` (extensions)

```typescript
// Existing: SpaceDeveloper (from backend/types)

// New for duel feature:
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

## API / Backend Contracts

**No new backend endpoints required!**

Duel feature reuses:
- **Existing API**: `GET /api/space-devs` (via `fetchDevs()` in api.ts)
- **Existing data**: SpaceDeveloper entity with 4 stat fields
- **No mutations**: No POST/PUT/DELETE needed

---

## State Flow Contracts

### DuelView State Management

```typescript
const [developers, setDevelopers] = useState<SpaceDeveloper[]>([])
const [loading, setLoading] = useState(true)
const [result, setResult] = useState<DuelResult | null>(null)
```

**State Transitions**:

| State | Condition | Action |
|-------|-----------|--------|
| `loading=true, result=null` | Initial load, error fetching | Show spinner/error |
| `loading=false, result=null` | Ready, no selection | Show DeveloperSelector |
| `loading=false, result={...}` | Selection made | Show ComparisonDisplay |
| `loading=false, result=null` | "New Duel" clicked | Back to selector |
| URL `?left=X&right=Y` | Page loaded with params | Auto-load and show result |

---

## Navigation Contracts

### Routing Structure

```
/ (Home)
  ├─ Header with "Space Devs" logo + links
  ├─ Toolbar with actions
  └─ Main content (DevCard grid, forms, etc.)

/duel (Duel Page)
  ├─ Page header "⚔️ Developer Duel Mode"
  ├─ DeveloperSelector OR ComparisonDisplay (toggled)
  └─ Link back to home in header
```

**Entry Points**:
- Click "⚔️ Duel Mode" in header → Navigate to `/duel`
- Paste shared URL like `/duel?left=3&right=7` → Auto-load duel
- "New Duel" button → Reload same page, reset state

---

## CSS Contract

**Namespace**: All duel-specific CSS classes prefixed with `duel-`

**Class Names**:
- `.duel-view` — Page container
- `.duel-selector` — Developer picker section
- `.duel-comparison` — Result display section
- `.duel-overall-result` — Winner banner
- `.duel-cards` — Developer name cards container
- `.duel-stat-rows` — Stat comparison rows
- `.duel-stat-row` — Individual stat row
- `.duel-stat-label`, `.duel-stat-left`, `.duel-stat-right` — Stat cells
- `.duel-winner` — Winner highlight (green)
- `.duel-draw` — Draw highlight (yellow)
- `.duel-animate` — Animation trigger class

**Animation Classes**:
- `@keyframes duel-stat-reveal` — Stat row fade-in (0.4s)
- `animation-delay` — Stagger delay per stat (100ms × index)

---

## Error Handling Contract

| Error Case | UI Response | Message |
|-----------|-----------|---------|
| Loading developers fails | Error state | "Failed to load developers. Try again." |
| Fewer than 2 developers exist | Disabled selector | "At least 2 developers required" |
| User selects same developer twice | Alert popup | "Cannot duel yourself!" |
| URL has invalid developer ID | Ignore params | Default to selector (no error) |
| A developer missing a stat field | Skip that field | (Handled gracefully in logic) |

---

## Testing Contracts

**What to test**:

1. **Unit tests (duelLogic.ts)**
   - `calculateCategoryWinner()` with various inputs
   - `calculateDuelResult()` with sample developer pairs
   - `parseDuelUrl()` with various query strings
   - `formatShareUrl()` output format

2. **Component tests (DuelView, selectors, displays)**
   - DeveloperSelector renders correctly with dev list
   - DeveloperSelector validates same-dev selection
   - ComparisonDisplay shows correct winner
   - Animations trigger on result render
   - URL updates on selection

3. **Integration tests**
   - Navigation to `/duel` works
   - Selecting devs updates URL
   - Sharing URL (reload) loads matchup
   - Mobile layout stacks properly

---

**All contracts defined and ready for implementation!**
