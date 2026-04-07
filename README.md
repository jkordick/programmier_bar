# 🚀 Space Devs — Intergalactic Developer Registry

> Repository initially created by Julia Kordick for the programmier.bar meetup on 2026-04-09
> May contain traces of eggs, humor and examples

## Agentic SDLC scenarios
### 1. PR Review

A demo project featuring a **Java Spring Boot** REST API and a **React + TypeScript** frontend with a space-themed UI.

## What is this?

A CRUD app for managing Space Developers — intergalactic coders with call signs, spaceships, rubber ducks, and debugging power levels. Because regular developer profiles are boring.

## Project Structure

```
backend/   — Spring Boot 3.4 + Java 21 + H2 in-memory DB
frontend/  — Vite + React 19 + TypeScript
```

## Prerequisites

- **Java 21+** (for backend)
- **Node.js 20+** (for frontend)
- **Maven** (or use the wrapper if you add one)

## Quick Start

### Backend

```bash
cd backend
./mvnw spring-boot:run    # or: mvn spring-boot:run
```

API runs at **http://localhost:8080**. H2 console at **http://localhost:8080/h2-console**.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

App runs at **http://localhost:5173** (proxies `/api` to backend).

## API Endpoints

| Method   | Endpoint                  | Description               |
|----------|---------------------------|---------------------------|
| `GET`    | `/api/space-devs`         | List all space devs       |
| `GET`    | `/api/space-devs/{id}`    | Get a space dev by ID     |
| `POST`   | `/api/space-devs`         | Register a new space dev  |
| `PUT`    | `/api/space-devs/{id}`    | Update a space dev        |
| `DELETE` | `/api/space-devs/{id}`    | Deorbit a space dev       |
| `GET`    | `/api/space-devs/search?callSign=...` | Search by call sign |
| `GET`    | `/api/space-devs/random-joke`         | Random dev joke     |

## Developer Data Model

| Field                    | Type           | Fun Factor |
|--------------------------|----------------|------------|
| `callSign`               | String         | Your hacker alias |
| `realName`               | String         | Your Earth name |
| `seniority`              | Enum           | Measured in gravitational mass |
| `skills`                 | List\<String\> | Including "Telepathy" and "Meme Engineering" |
| `ossProjects`            | List\<String\> | Your open source fleet |
| `favoriteDevJoke`        | String         | Required for morale |
| `coffeesPerDayInLiters`  | int            | Max 99. We don't judge. |
| `debuggingPowerLevel`    | int            | It's over 9000!!! |
| `rubberDuckName`         | String         | Every dev needs one |
| `favoriteKeyboardShortcut` | String       | Ctrl+Z is a lifestyle |
| `gitCommitStreak`        | int            | Days of unbroken commits |
| `stackOverflowReputation`| int            | Internet clout |
| `stillUsesVim`           | boolean        | Respect++ |
| `shipName`               | String         | Your vessel |

## Seniority Levels (Gravitational Classes)

- 🌌 **Mass of the Universe** — The architect of reality
- ⭐ **Mass of a Star** — Senior principal staff distinguished fellow
- 🪐 **Mass of a Planet** — Solid and reliable
- 🌙 **Mass of a Moon** — Orbiting greatness
- ☄️ **Mass of an Asteroid** — Small but impactful
- ✨ **Mass of Space Dust** — Everyone starts somewhere