# PR Review Taxonomy

Categories to evaluate for each change group during code review. Only surface findings — do not report categories where everything is clean.

## Correctness

- Does the code do what it claims to do (per PR description / linked issue)?
- Are there logic errors, off-by-one mistakes, or incorrect conditions?
- Are edge cases handled (null, empty, boundary values)?
- Does error handling cover realistic failure modes?

## Security

> Always flag findings in this category regardless of reviewer's chosen option.

- Input validation: is user input sanitized before use?
- Authentication / authorization: are access controls correct and complete?
- Secrets: are credentials, tokens, or keys hardcoded or logged?
- Injection: SQL, command, template, or path traversal risks?
- Data exposure: does logging or error output leak sensitive data?
- Dependency changes: are new dependencies from trusted sources? Known CVEs?

## Testing

- Are new/changed behaviors covered by tests?
- Do tests assert meaningful outcomes (not just "no error")?
- Are negative / edge-case scenarios tested?
- If tests are missing, which specific scenarios need coverage?

## API & Interface Design

- Are public API changes backward-compatible (or is a breaking change documented)?
- Are function/method signatures clear and consistent?
- Are return types and error contracts well-defined?
- Is versioning handled appropriately?

## Data Model & Persistence

- Are schema changes migration-safe (additive preferred)?
- Are indexes, constraints, and foreign keys appropriate?
- Is data validation applied at the right layer?
- Are there potential data loss scenarios?

## Performance

- Are there N+1 query patterns or unbounded loops?
- Are large data sets paginated or streamed?
- Are expensive operations cached or batched where appropriate?
- Could this change degrade performance under load?

## Code Quality & Maintainability

- Is the code readable and intention-revealing?
- Are naming conventions consistent with the codebase?
- Is there unnecessary duplication that could be extracted?
- Are complex sections adequately commented?
- Does the change respect existing patterns in the project?

## Configuration & Infrastructure

- Are environment-specific values externalized (not hardcoded)?
- Are feature flags or toggles used appropriately?
- Are deployment / rollback considerations addressed?
- Do Docker, CI/CD, or IaC changes align with existing conventions?

## Documentation

- Are user-facing changes reflected in docs or changelogs?
- Are new public APIs documented?
- Are migration steps documented for breaking changes?

## Severity Levels

Use these severity levels when reporting findings:

| Severity | Meaning |
|----------|---------|
| **info** | Observation or minor suggestion — no action required. |
| **warning** | Potential issue worth discussing — reviewer should consider addressing. |
| **concern** | Likely problem that should be addressed before merge. |
