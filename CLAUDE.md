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
npm start                        # Dev server at http://localhost:4200/
npm run build                    # Production build
npm run watch                    # Dev build in watch mode
npm test                         # Unit tests via Vitest
npm test -- --reporter=verbose   # Single file: npm test -- src/app/foo.spec.ts
```

### Backend (`backend/`)
```bash
./gradlew bootRun --args='--spring.profiles.active=local'   # Start API server (port 8080) with local profile
./gradlew build                                              # Compile and package
./gradlew test                                               # Run all tests
./gradlew test --tests "fr.augustinbaffou.unseen.SomeTest"  # Run a single test class
```

### Prerequisites
- PostgreSQL running on `localhost:5432` with database `unseen-db`
- Backend requires these environment variables (used in `application-local.properties`):
  - `SPRING_DATASOURCE_USERNAME` / `SPRING_DATASOURCE_PASSWORD`
  - `GOOGLE_CLIENT_ID` / `GOOGLE_CLIENT_SECRET`
  - `JWT_SECRET_KEY` (Base64-encoded HS256 key)
  - `APP_CORS_ALLOWED_ORIGIN` (e.g. `http://localhost:4200`)
- Frontend proxies API calls to `http://localhost:8080` (set in `environment.ts`)

## Architecture

### Frontend

**Stack:** Angular 21 (standalone) · Tailwind CSS 4 · Leaflet 1.9.4 · RxJS · Vitest

All components use the **standalone** pattern — no NgModule. Reactive state is handled with Angular **Signals** (`signal()`, `computed()`, `toSignal()`); async sources use **RxJS Observables** converted to signals at the component boundary.

**Routing** (`app.routes.ts`):
- `/` → `MapComponent` (carte interactive, accessible sans authentification)
- `/bars` → `BarsComponent` (liste avec recherche et filtres par type, lazy loaded)
- `/login` → `LoginComponent` (lazy loaded)
- `/oauth/callback` → `OAuthCallbackComponent` (JWT extraction from URL)
- `**` → redirect to `/`

**HTTP:** `JwtInterceptor` attaches `Authorization: Bearer <token>` to every outgoing request. Token is stored in `localStorage` under key `jwt_token`.

**Services:**
- `BarService` — `getAll()` / `getById(id)` — appelle le backend `/public/bars`
- `OverpassService` — queries the OpenStreetMap Overpass API for bars/cafes/pubs within 7.5 km of Nantes center
- `BarMarkerConverterService` (`bar-converter.service.ts`) — maps raw Overpass responses to `BarMarker` objects; **ranks are currently randomised** (75 % chance of `NA`, otherwise random S/A/B/C/D — placeholder pending real ranking logic)
- `AuthService` — manages JWT in `localStorage`, decodes payload client-side (no signature check), drives Google OAuth2 redirect flow
- `ThemeService` — dark mode toggle

**Data model** (`commun/bar.model.ts`):
```typescript
type BarMarker = { name: string; lat: number; lng: number; rank: string; description?: string; }

type BarType = 'COCKTAIL_BAR' | 'BEER_BAR' | 'CRAFT_BEER_BAR' | 'BREWPUB' | 'SPIRITS_BAR' | 'WINE_BAR'
             | 'DANCING_BAR' | 'LIVE_MUSIC_BAR' | 'NIGHTCLUB' | 'SPORTS_BAR' | 'ARCADE_BAR' | 'BOARD_GAME_BAR' | 'ESPORTS_BAR'
             | 'PUB' | 'STUDENT_BAR' | 'LOUNGE_BAR' | 'GUINGUETTE'
             | 'BRASSERIE' | 'TAPAS_BAR' | 'COFFEE_SHOP_BAR' | 'CAFE_TABAC' | 'PMU'
             | 'WATERFRONT_BAR' | 'TERRACE_BAR' | 'ROOFTOP' | 'PET_FRIENDLY';

type BarDataTrust = 'RAW_OSM' | 'COMMUNITY' | 'VERIFIED' | 'CLAIMED';

interface BarSchedule {
  id: number;
  type: 'BAR' | 'KITCHEN' | 'HAPPY_HOUR';
  dayOfWeek: string;        // 'MONDAY' … 'SUNDAY'
  is24h: boolean;
  opensAt?: string;         // 'HH:mm', ignoré si is24h
  closesAt?: string;        // peut être < opensAt (fermeture après minuit)
  happyHourDetails?: string;
}

interface Bar {
  id: number; osmId: string; lat: number; lng: number;
  name: string; altName?: string; wasName?: string; description?: string;
  priceRange?: number;       // 1=€ … 4=€€€€
  dataTrust: BarDataTrust;
  types: BarType[];
  schedules: BarSchedule[];
  games: any[];
  outdoorSeating?: string; indoorSeating?: string;
  instagram?: string; facebook?: string; website?: string;
  addrHousenumber?: string; addrStreet?: string; addrCity?: string;
}
```

**Map:** `LeafletMapComponent` uses CartoDB Voyager tile layer. Map center and radius are defined in `commun/config.ts` (`lat: 47.218371, lng: -1.553621`, radius 7500 m). Per-rank marker icons are loaded from `markers/light-svg/marker-pin-<rank>.svg`.

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
- `JwtService` — HS256 signing, expiry configured via `security.jwt.expiration-time` (ms; default 3600000 in local profile), embeds `id/name/email/role/picture` claims
- `User` entity — implements `UserDetails`; roles: `USER`, `ADMIN`

**Bar domain** (`bar/`):
- `Bar` entity — `bars` table; champs : osmId, lat/lng, name/altName/wasName, description, priceRange (1–4), dataTrust (enum), types (Set<BarType>), schedules (List<BarSchedule>), games (List<BarGame>), contacts, adresse, rawTags (JSONB)
- `BarSchedule` entity — `bar_schedules` table; champs : type (BAR/KITCHEN/HAPPY_HOUR), dayOfWeek, is24h, opensAt, closesAt, happyHourDetails
- `BarType` enum — 25 valeurs (COCKTAIL_BAR, PUB, NIGHTCLUB, LIVE_MUSIC_BAR…)
- `BarDataTrust` enum — RAW_OSM(1) → COMMUNITY(2) → VERIFIED(3) → CLAIMED(4)
- Endpoints publics : `GET /public/bars`, `GET /public/bars/{id}`, `GET /public/bars/osm/{osmId}`, `GET /public/bars/type/{type}`
- Endpoint admin : `POST /admin/bars/import/{osmId}` — importe depuis l'API Overpass, valide le format `node/<id>` / `way/<id>`, empêche les doublons

**BarGame domain** (`bargame/`):
- `BarGame` entity — `bar_games` table; contrainte unique `(bar_id, game_type)` ; champs : gameType (enum), quantity (≥1), isFree, qualityRating (0–5)
- `BarGameType` enum — 12 valeurs : BABYFOOT, DARTS_PLASTIC, DARTS_STEEL, BILLIARDS, PETANQUE, MOLKKY, PALET, ARCADE, BOARD_GAMES, PING_PONG, BEER_PONG, FLIPPER
- Endpoints admin : `POST/PUT/DELETE/GET /admin/bars/{barId}/games[/{gameId}]`

**Public endpoints:** `/auth/**`, `/public/**`, `/oauth2/**`, `/login/oauth2/**`, `/swagger-ui/**`, `/v3/api-docs/**`
**Protected:** everything else; `/admin/**` requires ADMIN role

**Database:** Hibernate `ddl-auto=update` — schema evolves automatically; no migration tool in use.

---

## Backend Conventions

### Package structure per domain

Each domain (e.g. `bar`) follows this layout:

```
<domain>/
├── controller/
│   ├── navigation/
│   │   ├── <Domain>ApiConstants.java       # paths, Swagger strings, @ApiResponse descriptions
│   │   └── <Domain>ExceptionConstants.java # resource name, field names for exceptions
│   ├── <Domain>GetAllController.java
│   ├── <Domain>GetByIdController.java
│   └── ...
├── entity/         <Domain>.java
├── repository/     <Domain>Repository.java
└── service/
    ├── <Domain>GetAllService.java
    ├── <Domain>GetByIdService.java
    └── ...

commun/
├── config/         OpenApiConfig.java
├── exception/
│   ├── ExceptionMessages.java              # generic HTTP labels + message templates
│   ├── ResourceNotFoundException.java      # → 404
│   ├── ResourceAlreadyExistsException.java # → 409
│   ├── ExternalServiceException.java       # → 502 (Overpass rate-limit / unavailable)
│   ├── dto/   ErrorResponse.java
│   └── handler/ GlobalExceptionHandler.java
└── service/
    ├── BaseService.java                    # Use Case with input
    └── BaseQueryService.java               # Use Case without input
```

### Use Case pattern (services)

Each service represents **one operation**. It extends either:
- `BaseService<INPUT, OUTPUT>` — `execute(INPUT): OUTPUT` — when the operation takes a parameter
- `BaseQueryService<OUTPUT>` — `execute(): OUTPUT` — when the operation takes no parameter

```java
// Example
public class BarGetByIdService extends BaseService<Long, Optional<Bar>> {
    public Optional<Bar> execute(Long id) { ... }
}

public class BarGetAllService extends BaseQueryService<List<Bar>> {
    public List<Bar> execute() { ... }
}
```

### One controller per endpoint

Each HTTP endpoint is its own `@RestController` class. No multi-endpoint controllers.

```java
// Correct
BarGetAllController    → GET /public/bars
BarGetByIdController   → GET /public/bars/{id}

// Wrong
BarController with getAll() + getById() + ...
```

### Constants files

**No raw strings in controllers or exception classes.** All strings are extracted to constants:

| File | Contains |
|---|---|
| `<Domain>ApiConstants` | Paths (`BASE_PATH`, `BY_ID_PATH`…), tag name/description, `@Operation` summary/description, `@Parameter` description/example, `@ApiResponse` descriptions |
| `<Domain>ExceptionConstants` | Resource name and field names used in `ResourceNotFoundException` |
| `ExceptionMessages` (commun) | Generic HTTP error labels (`HTTP_NOT_FOUND`…), message format strings |

### Unique constraints on JPA entities

Always name constraints explicitly to avoid Hibernate hash conflicts with `ddl-auto=update`:

```java
// Correct
@Table(uniqueConstraints = @UniqueConstraint(name = "uq_bars_osm_id", columnNames = "osm_id"))

// Wrong — generates a random hash, breaks on restart
@Column(unique = true)
```

### Swagger / OpenAPI

- Dependency: `org.springdoc:springdoc-openapi-starter-webmvc-ui`
- UI available at `http://localhost:8080/swagger-ui.html`
- All entities exposed in the API must carry `@Schema` on the class and each field
- `ErrorResponse` must also carry `@Schema` (it appears in all error `@ApiResponse`)
- All `@ApiResponse`, `@Operation`, `@Parameter` strings come from `<Domain>ApiConstants` — never inline
