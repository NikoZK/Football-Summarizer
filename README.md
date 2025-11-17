# Football Summarizer – Technical Overview

This document is aimed at developers who want to clone and run the project from GitHub. It describes requirements, how to start the app, all available REST endpoints, and a few important implementation details.

## 1. Stack and Architecture
- Backend: Spring Boot 3 (Java 17), Maven
- Frontend: Static `index.html` + `main.js` (Bootstrap, vanilla JS)
- Data: In‑memory database by default (JPA, H2); optional MySQL
- External APIs:
  - ESPN public API for match data (no key required)
  - OpenAI Chat Completions API for AI summaries

The backend serves both the REST API and the static frontend from `src/main/resources/static`.

## 2. Requirements
- Java 17 or newer
- Git
- Internet access to:
  - `api.openai.com` (only if AI features are enabled)
  - ESPN’s public football endpoints (used internally by the backend)

No football data API key is needed. An OpenAI API key is required for AI analysis.

## 3. Configuration
Main configuration: `src/main/resources/application.properties`

- OpenAI API key (required for AI features):
  - Property: `app.api-key=${API_KEY}`
  - Environment variable: `API_KEY`
- OpenAI model and tuning:
  - `app.url=https://api.openai.com/v1/chat/completions`
  - `app.model=gpt-4o`
  - `app.temperature`, `app.max_tokens`, etc.
- JPA / database:
  - `spring.jpa.hibernate.ddl-auto=create-drop` (schema recreated on start/stop)
  - `spring.datasource.*` is currently commented out → defaults to in‑memory DB.

If you want persistent data or MySQL, you must configure `spring.datasource.url`, `spring.datasource.username`, and `spring.datasource.password` yourself.

## 4. Building and Running

Clone the repository and start the application locally:

```bash
git clone <REPO_URL>
cd football-summarizer

# Set OpenAI key (bash/zsh)
export API_KEY=your-openai-api-key

# or on Windows (cmd)
set API_KEY=your-openai-api-key

# Run with Maven wrapper
./mvnw spring-boot:run
```

After startup:
- Backend base URL: `http://localhost:8080`
- Frontend: `http://localhost:8080/` (serves `index.html`)

## 5. REST API – Base Path

All endpoints are under the same base path:

- Base: `/api/v1/football`
- CORS: `@CrossOrigin(origins = "*")` → can be called directly from browser/JS.

Date format in all examples: `YYYY-MM-DD` (ISO‑8601).

### 5.1 Get Matches

**Endpoint**

- `GET /api/v1/football/matches`

**Query parameters**

- `date` (optional, string): Match date in `YYYY-MM-DD`.
  - If omitted, backend defaults to “yesterday”.
- `league` (optional, string): ESPN league code.
  - Common values (see also `MatchService.LEAGUE_NAMES`):
    - `eng.1` – Premier League  
    - `esp.1` – La Liga  
    - `ita.1` – Serie A  
    - `fra.1` – Ligue 1  
    - `ger.1` – Bundesliga  
    - `UEFA.CHAMPIONS` – Champions League  
    - `den.1` – Superligaen  

**Example request**

```http
GET /api/v1/football/matches?date=2024-11-04&league=eng.1
```

**Response (simplified)**

```json
{
  "date": "2024-11-04",
  "matches": [
    {
      "fixtureId": "123456",
      "date": "2024-11-04T15:00:00Z",
      "status": "FT",
      "homeTeam": "Team A",
      "awayTeam": "Team B",
      "homeTeamLogo": "https://...",
      "awayTeamLogo": "https://...",
      "homeScore": 2,
      "awayScore": 1,
      "league": "Premier League",
      "venue": "Some Stadium"
    }
  ]
}
```

The frontend (`main.js`) calls this endpoint via:

```js
GET http://localhost:8080/api/v1/football/matches?date=...&league=...
```

### 5.2 Get Match Summary + AI Analysis

**Endpoint**

- `GET /api/v1/football/match/{fixtureId}/summary`

**Path variable**

- `fixtureId` (string): ESPN event/match id from `/matches`.

**Query parameters**

- `league` (optional, string): ESPN league code (same as above).

**Example request**

```http
GET /api/v1/football/match/123456/summary?league=eng.1
```

**Response (structure)**

- Basic match info (teams, score, status)
- Detailed statistics (possession, shots, saves, tackles, interceptions, corners, xG)
- AI-generated text summary (if OpenAI is configured)
- For upcoming matches: a preview instead of full stats

The response type is `MatchSummaryResponse`.

### 5.3 Get Player Performance (Best/Worst)

**Endpoint**

- `GET /api/v1/football/match/{fixtureId}/players`

**Path variable**

- `fixtureId` (string): ESPN match id from `/matches`.

**Query parameters**

- `type` (required, string): `best` or `worst`
- `league` (optional, string): ESPN league code

**Example request**

```http
GET /api/v1/football/match/123456/players?type=best&league=eng.1
```

**Response**

- A list of players with:
  - Name, position, minutes
  - Basic stats (goals, assists, shots on target, saves, fouls, etc.)
  - An AI-generated one-line explanation of why they were among the best/worst performers

The response type is `PlayerPerformanceResponse`.

## 6. Frontend Behaviour

- Static assets:
  - `src/main/resources/static/index.html`
  - `src/main/resources/static/js/main.js`
  - `src/main/resources/static/css/main.css`
- `main.js` uses:
  - `SERVER_URL = 'http://localhost:8080/api/v1/football/';`
  - If you deploy backend on another host/port, update this constant.
- Browser UI:
  - Date picker and league selector
  - Match cards with basic info
  - Modal dialog with match summary and “Best/Worst performers” buttons

## 7. Deployment Notes

- Default port: `8080` (standard Spring Boot)
- CORS is open for the football endpoints – lock this down before public production use.
- DB schema is recreated on each run (`create-drop`) – change this if you need persistence.
- MySQL dependency is present, but not configured by default.

## 8. Things to Know Before Cloning

- You must provide a valid OpenAI API key via the `API_KEY` environment variable if you want AI summaries and player analysis to work. Without it, the app will fall back to simpler text or may log errors, depending on the call.
- ESPN data is free and does not require any key, but you need internet access from the backend server.
- The project `artifactId` in `pom.xml` is `chatgpt-jokes`, even though the app is a football summarizer – this affects the name of the built JAR but not functionality.
- All API endpoints are versioned under `/api/v1/football` so that future versions can coexist if needed.

This file (`readmeNew.md`) is intended as a technical companion to `README.md` with more details for developers and integrators.

