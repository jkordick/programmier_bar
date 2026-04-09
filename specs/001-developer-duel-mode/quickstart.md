# Quickstart: Developer Duel Mode Implementation

**Date**: 2026-04-09  
**Feature**: Developer Duel Mode  
**Audience**: Developers implementing the feature

---

## Overview

This guide walks through the implementation of the Developer Duel Mode feature in 7 sequential steps.

**Estimated effort**: 8–10 hours  
**Files to create**: 4 components + 1 utility + styles  
**Files to modify**: 1 (App.tsx)  
**Dependencies to add**: 1 (react-router-dom)

---

## Step 1: Add React Router

React Router enables the `/duel` page route.

### 1.1 Install dependency

```bash
cd frontend
npm install react-router-dom@^6
```

### 1.2 Wrap App.tsx with BrowserRouter

**File**: `frontend/src/App.tsx`

```typescript
import { BrowserRouter, Routes, Route } from 'react-router-dom'
import DuelView from './pages/DuelView'  // Not yet created

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/duel" element={<DuelView />} />
      </Routes>
    </BrowserRouter>
  )
}

function Home() {
  // Existing App content moved here
  // Add "Duel" button/link to navbar → navigate to /duel
}
```

---

## Step 2: Implement Comparison Logic

Create utility functions for determining winners and computing duel results.

**File**: `frontend/src/utils/duelLogic.ts`

```typescript
import type { SpaceDeveloper, Winner, DuelResult } from '../types'

export function calculateCategoryWinner(
  leftValue: number,
  rightValue: number
): Winner {
  if (leftValue > rightValue) return 'left'
  if (rightValue > leftValue) return 'right'
  return 'draw'
}

export function calculateDuelResult(
  left: SpaceDeveloper,
  right: SpaceDeveloper
): DuelResult {
  const categoryWinners = {
    debuggingPowerLevel: calculateCategoryWinner(
      left.debuggingPowerLevel,
      right.debuggingPowerLevel
    ),
    coffeesPerDayInLiters: calculateCategoryWinner(
      left.coffeesPerDayInLiters,
      right.coffeesPerDayInLiters
    ),
    gitCommitStreak: calculateCategoryWinner(
      left.gitCommitStreak,
      right.gitCommitStreak
    ),
    stackOverflowReputation: calculateCategoryWinner(
      left.stackOverflowReputation,
      right.stackOverflowReputation
    ),
  }

  const leftWins = Object.values(categoryWinners).filter(w => w === 'left').length
  const rightWins = Object.values(categoryWinners).filter(w => w === 'right').length

  let overallWinner: Winner
  if (leftWins > rightWins) overallWinner = 'left'
  else if (rightWins > leftWins) overallWinner = 'right'
  else overallWinner = 'draw'

  return {
    leftDeveloper: left,
    rightDeveloper: right,
    categoryWinners,
    overallWinner,
    categoriesWon: { left: leftWins, right: rightWins },
  }
}

export function parseDuelUrl(searchParams: URLSearchParams) {
  return {
    leftId: searchParams.get('left') ? parseInt(searchParams.get('left')!) : null,
    rightId: searchParams.get('right') ? parseInt(searchParams.get('right')!) : null,
  }
}

export function formatShareUrl(left: SpaceDeveloper, right: SpaceDeveloper): string {
  return `/duel?left=${left.id}&right=${right.id}`
}
```

---

## Step 3: Create DeveloperSelector Component

The selector allows users to pick two developers.

**File**: `frontend/src/components/DeveloperSelector.tsx`

```typescript
import { useState } from 'react'
import type { SpaceDeveloper } from '../types'

interface Props {
  developers: SpaceDeveloper[]
  onSelect: (left: SpaceDeveloper, right: SpaceDeveloper) => void
  loading?: boolean
}

export default function DeveloperSelector({ developers, onSelect, loading }: Props) {
  const [leftId, setLeftId] = useState<number | null>(null)
  const [rightId, setRightId] = useState<number | null>(null)

  function handleSelect() {
    if (leftId === null || rightId === null) return
    if (leftId === rightId) {
      alert('Cannot duel yourself!')
      return
    }
    const left = developers.find(d => d.id === leftId)
    const right = developers.find(d => d.id === rightId)
    if (left && right) onSelect(left, right)
  }

  if (loading) return <div className="duel-loading">Loading developers...</div>
  if (developers.length < 2) {
    return <div className="duel-error">At least 2 developers required</div>
  }

  return (
    <div className="duel-selector">
      <h2>Pick Two Developers for a Duel</h2>
      <div className="duel-selector-grid">
        <div>
          <label>Left Developer</label>
          <select value={leftId ?? ''} onChange={e => setLeftId(parseInt(e.target.value) || null)}>
            <option value="">Choose a developer...</option>
            {developers.map(dev => (
              <option key={dev.id} value={dev.id}>
                {dev.callSign} ({dev.realName})
              </option>
            ))}
          </select>
        </div>
        <div>
          <label>Right Developer</label>
          <select value={rightId ?? ''} onChange={e => setRightId(parseInt(e.target.value) || null)}>
            <option value="">Choose a developer...</option>
            {developers.map(dev => (
              <option key={dev.id} value={dev.id}>
                {dev.callSign} ({dev.realName})
              </option>
            ))}
          </select>
        </div>
      </div>
      <button
        className="btn btn-primary"
        onClick={handleSelect}
        disabled={leftId === null || rightId === null}
      >
        ⚔️ Start Duel
      </button>
    </div>
  )
}
```

---

## Step 4: Create ComparisonDisplay Component

Displays the duel result with animated sequential reveals.

**File**: `frontend/src/components/ComparisonDisplay.tsx`

```typescript
import { useEffect, useState } from 'react'
import type { DuelResult } from '../types'

interface Props {
  result: DuelResult
  onNewDuel: () => void
}

export default function ComparisonDisplay({ result, onNewDuel }: Props) {
  const [animate, setAnimate] = useState(false)

  useEffect(() => {
    setAnimate(true)
  }, [result])

  const stats = [
    { label: 'Debugging Power', key: 'debuggingPowerLevel' as const },
    { label: 'Coffee Consumption (L/day)', key: 'coffeesPerDayInLiters' as const },
    { label: 'Git Commit Streak', key: 'gitCommitStreak' as const },
    { label: 'Stack Overflow Rep', key: 'stackOverflowReputation' as const },
  ]

  return (
    <div className={`duel-comparison ${animate ? 'duel-animate' : ''}`}>
      <div className="duel-overall-result">
        {result.overallWinner === 'draw' ? (
          <h2>⚔️ It's a Draw!</h2>
        ) : (
          <h2>
            🏆{' '}
            {result.overallWinner === 'left'
              ? result.leftDeveloper.callSign
              : result.rightDeveloper.callSign}{' '}
            Wins!
          </h2>
        )}
      </div>

      <div className="duel-cards">
        <div className="duel-developer-card duel-left">
          <div className="duel-dev-name">{result.leftDeveloper.callSign}</div>
          <div className="duel-dev-real">{result.leftDeveloper.realName}</div>
        </div>
        <div className="duel-developer-card duel-right">
          <div className="duel-dev-name">{result.rightDeveloper.callSign}</div>
          <div className="duel-dev-real">{result.rightDeveloper.realName}</div>
        </div>
      </div>

      <div className="duel-stat-rows">
        {stats.map((stat, idx) => {
          const winner = result.categoryWinners[stat.key]
          const leftVal = result.leftDeveloper[stat.key]
          const rightVal = result.rightDeveloper[stat.key]
          return (
            <div
              key={stat.key}
              className={`duel-stat-row duel-stat-${idx} ${winner === 'draw' ? 'duel-draw' : ''}`}
              style={{ animationDelay: `${idx * 100}ms` }}
            >
              <div className="duel-stat-label">{stat.label}</div>
              <div className={`duel-stat-left ${winner === 'left' ? 'duel-winner' : ''}`}>
                {leftVal}
              </div>
              <div className={`duel-stat-right ${winner === 'right' ? 'duel-winner' : ''}`}>
                {rightVal}
              </div>
            </div>
          )
        })}
      </div>

      <button className="btn btn-primary duel-new-duel" onClick={onNewDuel}>
        ⚔️ New Duel
      </button>
    </div>
  )
}
```

---

## Step 5: Create DuelView Page Component

**File**: `frontend/src/pages/DuelView.tsx`

```typescript
import { useEffect, useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import type { SpaceDeveloper, DuelResult } from '../types'
import { fetchDevs } from '../api'
import DeveloperSelector from '../components/DeveloperSelector'
import ComparisonDisplay from '../components/ComparisonDisplay'
import { calculateDuelResult, parseDuelUrl } from '../utils/duelLogic'

export default function DuelView() {
  const [developers, setDevelopers] = useState<SpaceDeveloper[]>([])
  const [loading, setLoading] = useState(true)
  const [result, setResult] = useState<DuelResult | null>(null)
  const [searchParams] = useSearchParams()

  useEffect(() => {
    async function loadDevs() {
      setLoading(true)
      try {
        const devs = await fetchDevs()
        setDevelopers(devs)

        // Check if URL has params
        const { leftId, rightId } = parseDuelUrl(searchParams)
        if (leftId && rightId) {
          const left = devs.find(d => d.id === leftId)
          const right = devs.find(d => d.id === rightId)
          if (left && right && leftId !== rightId) {
            setResult(calculateDuelResult(left, right))
          }
        }
      } catch (error) {
        console.error('Failed to load developers', error)
      } finally {
        setLoading(false)
      }
    }
    loadDevs()
  }, [searchParams])

  function handleSelect(left: SpaceDeveloper, right: SpaceDeveloper) {
    const duelResult = calculateDuelResult(left, right)
    setResult(duelResult)
    // Update URL for shareability
    window.history.replaceState({}, '', `/duel?left=${left.id}&right=${right.id}`)
  }

  return (
    <div className="duel-view">
      <h1>⚔️ Developer Duel Mode</h1>
      {result ? (
        <ComparisonDisplay result={result} onNewDuel={() => setResult(null)} />
      ) : (
        <DeveloperSelector developers={developers} onSelect={handleSelect} loading={loading} />
      )}
    </div>
  )
}
```

---

## Step 6: Add CSS Styling

Add duel-specific styles to `frontend/src/index.css`:

```css
/* Duel View Layout */
.duel-view {
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.duel-selector {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 2rem;
  margin: 2rem 0;
}

.duel-selector-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 2rem;
  margin: 1.5rem 0;
}

.duel-selector select,
.duel-selector label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
}

.duel-selector select {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

/* Comparison Display */
.duel-comparison {
  margin: 2rem 0;
}

.duel-overall-result {
  text-align: center;
  padding: 2rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 8px;
  margin-bottom: 2rem;
  font-size: 2rem;
  min-height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.duel-overall-result h2 {
  margin: 0;
  font-size: 2.5rem;
}

.duel-cards {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 2rem;
  margin-bottom: 2rem;
}

.duel-developer-card {
  background: white;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  padding: 1.5rem;
  text-align: center;
}

.duel-dev-name {
  font-size: 1.5rem;
  font-weight: bold;
  color: #333;
}

.duel-dev-real {
  color: #666;
  font-size: 0.9rem;
  margin-top: 0.5rem;
}

/* Stat Rows */
.duel-stat-rows {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 2rem;
}

.duel-stat-row {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr;
  align-items: center;
  padding: 1rem;
  border-bottom: 1px solid #e0e0e0;
  animation: duel-stat-reveal 0.4s ease-in-out forwards;
  opacity: 0;
}

.duel-stat-row:last-child {
  border-bottom: none;
}

@keyframes duel-stat-reveal {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.duel-stat-label {
  font-weight: 600;
  color: #333;
}

.duel-stat-left,
.duel-stat-right {
  text-align: center;
  font-size: 1.2rem;
  font-weight: bold;
  padding: 0.5rem;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.duel-stat-left.duel-winner,
.duel-stat-right.duel-winner {
  background: #4caf50;
  color: white;
  box-shadow: 0 0 8px rgba(76, 175, 80, 0.5);
}

.duel-stat-row.duel-draw .duel-stat-left,
.duel-stat-row.duel-draw .duel-stat-right {
  background: #ffeb3b;
  color: #333;
}

.duel-new-duel {
  width: 100%;
  padding: 1rem;
  font-size: 1.1rem;
  margin-top: 1rem;
}

/* Mobile Responsive */
@media (max-width: 767px) {
  .duel-selector-grid {
    grid-template-columns: 1fr;
    gap: 1rem;
  }

  .duel-cards {
    grid-template-columns: 1fr;
    gap: 1rem;
  }

  .duel-stat-row {
    grid-template-columns: 1fr;
    gap: 0.5rem;
  }

  .duel-stat-label {
    margin-bottom: 0.5rem;
  }

  .duel-stat-left,
  .duel-stat-right {
    font-size: 1rem;
  }

  .duel-overall-result h2 {
    font-size: 1.5rem;
  }
}
```

---

## Step 7: Update App Navigation

Add a "Duel" button to the app header linking to `/duel`:

**File**: `frontend/src/App.tsx` (navigation section)

```typescript
<nav className="navbar">
  <a href="/">Space Devs</a>
  <a href="/duel">⚔️ Duel Mode</a>
</nav>
```

---

## Testing

**Unit tests** for `duelLogic.ts`:

```typescript
// frontend/src/__tests__/duelLogic.test.ts
import { describe, it, expect } from 'vitest'
import {
  calculateCategoryWinner,
  calculateDuelResult,
} from '../utils/duelLogic'

describe('duelLogic', () => {
  it('determines category winner correctly', () => {
    expect(calculateCategoryWinner(100, 50)).toBe('left')
    expect(calculateCategoryWinner(50, 100)).toBe('right')
    expect(calculateCategoryWinner(50, 50)).toBe('draw')
  })

  it('calculates overall duel result', () => {
    const left = {
      id: 1,
      callSign: 'Alpha',
      realName: 'Alice',
      debuggingPowerLevel: 9000,
      coffeesPerDayInLiters: 50,
      gitCommitStreak: 100,
      stackOverflowReputation: 10000,
      // ... other fields
    }
    const right = {
      id: 2,
      callSign: 'Beta',
      realName: 'Bob',
      debuggingPowerLevel: 500,
      coffeesPerDayInLiters: 30,
      gitCommitStreak: 500,
      stackOverflowReputation: 20000,
      // ... other fields
    }

    const result = calculateDuelResult(left, right)
    expect(result.overallWinner).toBe('draw')
    expect(result.categoriesWon.left).toBe(2)
    expect(result.categoriesWon.right).toBe(2)
  })
})
```

---

## Verification Checklist

- [ ] React Router installed and `/duel` route accessible
- [ ] DuelView renders without errors on `/duel`
- [ ] DeveloperSelector displays all developers with no duplicates
- [ ] Selecting two different developers shows ComparisonDisplay
- [ ] Selecting same developer shows error message
- [ ] Stat winner highlighting displays correctly
- [ ] Overall winner banner displays correctly
- [ ] "New Duel" button resets to selector
- [ ] URL updates when duel begins (`?left=X&right=Y`)
- [ ] Sharing URL (copy/paste) loads saved matchup
- [ ] Mobile layout stacks properly at 768px and below
- [ ] Animations play sequentially (not instant)
- [ ] All unit tests pass

---

## Troubleshooting

**Route not found**: Ensure BrowserRouter wraps Routes in App.tsx  
**Stat values undefined**: Check SpaceDeveloper interface; all 4 stats must be defined  
**CSS not applying**: Reload page; Vite may need restart  
**Animation not showing**: Check CSS is loaded; confirm React state updates

---

**Start coding!** Estimated time: 8–10 hours
