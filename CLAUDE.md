# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Unseen** is a bar discovery application for Nantes, France. Users can explore hidden bars on an interactive map with tier-based rankings. The app uses Google OAuth2 for authentication.

## Repository Structure

```
unseen-app/
├── frontend/    # Angular 21 standalone components app
└── backend/     # Spring Boot 4.0.2 REST API
```

## Commands

### Frontend (`frontend/`)
```bash
npm start         # Dev server at http://localhost:4200/
npm run build     # Production build
npm run watch     # Dev build in watch mode
npm test          # Unit tests via Vitest
```

### Backend (`backend/`)
```bash
./gradlew bootRun   # Start API server (port 8080)
./gradlew build     # Compile and package
./gradlew test      # Run tests
```

### Prerequisites
- PostgreSQL running on `localhost:5432` with database `unseen-db`
- Backend uses `application-local.properties` profile for local dev
- Frontend proxies API calls to `http://localhost:8080`

## Architecture

### Frontend

**Stack:** Angular 21 (standalone) · Tailwind CSS 4 · Leaflet 1.9.4 · RxJS · Vitest

All components use the **standalone** pattern — no NgModule. Reactive state is handled with Angular **Signals** (`signal()`, `computed()`, `toSignal()`); async sources use **RxJS Observables** converted to signals at the component boundary.

**Routing** (`app.routes.ts`):
- `/` → `MapComponent` (main page)
- `/login` → `LoginComponent` (lazy loaded)
- `/oauth/callback` → `OAuthCallbackComponent` (JWT extraction from URL)
- `**` → redirect to `/`

**HTTP:** A `JwtInterceptor` automatically attaches `Authorization: Bearer <token>` to every outgoing request.

**Services:**
- `OverpassService` — queries the OpenStreetMap Overpass API for bars/cafes/pubs within 7.5 km of Nantes center
- `BarMarkerConverterService` — maps raw Overpass responses to `BarMarker` objects with tier ranks (S/A/B/C/D/NA)
- `AuthService` — manages JWT in `localStorage`, drives Google OAuth2 redirect flow

**Data model** (`commun/bar.model.ts`):
```typescript
type BarMarker = { name: string; lat: number; lng: number; rank: string; description?: string; }
```

**Map center** is hardcoded in `commun/config.ts`: `{ lat: 47.218371, lng: -1.553621 }`, radius 7500 m.

**Styling:** Custom Tailwind theme in `src/styles.scss` defines the Unseen palette (cream, terracotta, sage, charcoal…) and per-tier colors (S→purple, A→gold, B→green, C→blue, D→orange, E→red). Fonts: Playfair Display (display), Inter (body), DM Sans (accent).

### Backend

**Stack:** Spring Boot 4.0.2 · Java 25 · Spring Security · Spring Data JPA · PostgreSQL · JJWT 0.12.3

All auth logic lives under `fr.augustinbaffou.unseen.auth/`:

**Authentication flow:**
1. Frontend redirects to `/oauth2/authorization/google`
2. Google authenticates the user and calls the backend callback
3. `OAuth2AuthenticationSuccessHandler` creates/updates the `User` entity and issues a JWT
4. Backend redirects to `http://localhost:4200/oauth/callback?token=<JWT>`
5. `OAuthCallbackComponent` extracts the token and stores it in `localStorage`
6. `JwtInterceptor` includes the token in subsequent API calls

**Key classes:**
- `SecurityConfiguration` — stateless sessions, CORS, JWT filter chain, role-based rules (`/admin/**` requires ADMIN)
- `JwtAuthenticationFilter` — validates JWT on every request
- `JwtService` — HS256 signing, 24 h expiry, embeds `id/name/email/role/picture` claims
- `User` entity — implements `UserDetails`; roles: `USER`, `ADMIN`

**Public endpoints:** `/auth/**`, `/public/**`, `/oauth2/**`
**Protected:** everything else; `/admin/**` requires ADMIN role

**Database:** Hibernate `ddl-auto=update` — schema evolves automatically; no migration tool in use.
