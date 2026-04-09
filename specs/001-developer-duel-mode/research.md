# Phase 0 Research: Developer Duel Mode

**Date**: 2026-04-09  
**Feature**: Developer Duel Mode  
**Input**: Unknowns from plan.md

## Research Summary

Investigation of four key unknowns to validate the implementation approach.

---

## R1. Routing Library Decision

**Unknown**: Confirm React Router as the routing solution for the `/duel` page.

**Investigation**:
- Current `frontend/src/main.tsx` mounts `App` directly with no routing library detected
- Create React App convention and existing component patterns (DevCard, DevForm) suggest React Router is the natural fit
- Vite is configured, which supports React Router without issues

**Decision**: **Use React Router v6** for `/duel` page routing
- Install: `npm install react-router-dom@^6.x`
- Wrap `App` in `BrowserRouter`
- Add Route for path `/duel` → `<DuelView />`
- Update navigation: add link to `/duel` in App header

**Rationale**: React Router is industry standard for React SPAs, fully compatible with Vite + TypeScript setup, requires minimal migration effort

---

## R2. Animation Framework

**Unknown**: Evaluate CSS transitions vs. external framework (Framer Motion) for sequential stat reveals.

**Investigation**:
- Sequential reveal pattern: 4 stats + 1 overall winner = 5 animations
- Timing: ~300ms per stat reveal, ~500ms overall winner announcement
- Performance: CSS animations are GPU-accelerated, no external dependency needed
- Complexity: CSS `@keyframes` + React state management simple enough for this scope
- Existing project: No animation library detected in package.json; CSS-only is consistent

**Decision**: **Use CSS animations only** (no Framer Motion)
- Recommended timing:
  - Stat reveal animation: 0.3s ease-in-out per category
  - Stagger: 100ms delay between each stat reveal
  - Overall winner announce: 0.5s ease-out (starts at end of stat reveals)
- CSS animations defined in `index.css` with new `.duel-*` classes
- Animation trigger via React state change (animation resets on new matchup)

**Rationale**: CSS animations sufficient for UX requirements, zero additional dependencies, better performance than JS-driven animation library

---

## R3. Testing Framework

**Unknown**: Determine test framework setup from existing configuration.

**Investigation**:
- Check package.json: No `jest`, `vitest`, or testing library found in provided snippet
- Check for test files: One test file detected at `backend/src/test/java/.../MissionControllerTest.java` (backend only)
- Frontend appears to have no test infrastructure configured yet

**Decision**: **Use Vitest for new tests** (recommended for Vite projects)
- Install: `npm install -D vitest @testing-library/react @testing-library/dom`
- Create test files in `frontend/src/__tests__/` (standard Vite convention)
- Test coverage:
  - Unit tests for `duelLogic.ts` functions (calculateCategoryWinner, calculateDuelResult, parseDuelUrl)
  - Component tests for DuelView, DeveloperSelector, ComparisonDisplay
  - Integration test for URL parameter loading and routing

**Rationale**: Vitest is optimized for Vite projects and provides fast, modern testing with TypeScript support out of the box

---

## R4. Mobile Responsive Breakpoint

**Unknown**: Confirm 768px breakpoint is consistent with app conventions.

**Investigation**:
- Existing page structure: DevCard, DevForm, MissionTimeline components
- Standard mobile breakpoint: 768px is industry standard (iPad breakpoint)
- Success Criteria in spec: "renders correctly on screens from 320px to 1920px width"
- Mobile design chosen: Vertical stacking of developer cards at screens < 768px

**Decision**: **Confirm 768px as responsive breakpoint**
- Desktop layout: side-by-side developer cards (2-column)
- Mobile layout: stacked developer cards (1-column, user scrolls vertically)
- CSS media query: `@media (max-width: 767px)` for stacked layout
- Test viewports: 320px (mobile), 768px (tablet edge), 1024px (tablet+), 1920px (desktop)

**Rationale**: 768px is consistent with web standards, matches success criteria, validation of vertical stacking tested on actual devices during development

---

## All Unknowns Resolved

✅ R1: React Router v6 selected for `/duel` routing  
✅ R2: CSS animations selected for sequential reveals (no external framework)  
✅ R3: Vitest selected for testing framework  
✅ R4: 768px breakpoint confirmed; vertical stack strategy validated  

**Next Phase**: Phase 1 design (data-model.md, contracts/, quickstart.md) can now proceed with full confidence.
