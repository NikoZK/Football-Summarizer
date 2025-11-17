# Football Match Center

En moderne fodboldplatform, der viser live kampe, detaljerede
statistikker og AI-genererede analyser.\
Matchdata hentes fra ESPN's offentlige API, som er gratis og kræver
ingen API-nøgle.

## Funktioner

-   Dato-baseret oversigt over alle kampe.
-   Match-kort med stilling, logoer og status.
-   Detaljerede kampstatistikker (boldbesiddelse, skud, kort m.m.).
-   AI-genererede kampresuméer samt vurdering af bedste og dårligste
    spillere.
-   Responsivt layout til desktop og mobil.
-   Realtidsdata via ESPN's offentlige endpoints.

## Hurtig start

### Krav

-   Java 17 eller nyere\
-   Maven (via wrapper)\
-   OpenAI API-nøgle (kun til AI-delen; kampdata er gratis)

### Installation

1.  Klon projektet:

    ``` bash
    git clone <your-repo-url>
    cd chatgpt-jokes
    ```

2.  Angiv OpenAI API-nøgle:

    ``` bash
    export API_KEY=your-openai-api-key
    ```

    Windows:

    ``` cmd
    set API_KEY=your-openai-api-key
    ```

3.  Start applikationen:

    ``` bash
    ./start.sh
    ```

    eller:

    ``` bash
    ./mvnw spring-boot:run
    ```

4.  Åbn i browseren:

        http://localhost:8080

## Datakilder

### ESPN -- kampdata (gratis)

-   Offentlige endpoints, ingen nøgler.\
-   Dækker store ligaer som Premier League, La Liga, Serie A,
    Bundesliga, MLS m.fl.

### OpenAI -- analyser

-   Bruges til resuméer og spillerbedømmelser.\
-   Pris: ca. 0,01--0,02 USD pr. analyse.

## Brugervejledning

1.  Vælg en dato.\
2.  Gennemse kampene for den dag.\
3.  Klik på en kamp for statistik og AI-resumé.\
4.  Vælg spilleranalyse for bedste/dårligste præstationer.

## Teknologistak

**Backend:** Spring Boot 3, Java 17, WebClient\
**Frontend:** HTML, CSS, JavaScript, Bootstrap\
**APIs:** ESPN (gratis), OpenAI (betalt)

## API-endpoints

**Hent kampe:**

    GET /api/v1/football/matches?date=YYYY-MM-DD

**Hent kampresumé:**

    GET /api/v1/football/match/{fixtureId}/summary

**Hent spilleranalyse:**

    GET /api/v1/football/match/{fixtureId}/players?type=best|worst

## Tilpasning

**Skift liga:**\
`FootballService.java` linje 24:

``` java
private static final String DEFAULT_LEAGUE = "eng.1";
```

**AI-model:**\
`application.properties`:

``` properties
app.model=gpt-4o
app.temperature=0.8
app.max_tokens=300
```

## Omkostninger

-   Kampdata: gratis.\
-   AI-funktioner: afhænger af brug (typisk få dollars pr. måned).

## Fejlfinding

-   Ingen kampe: vælg en anden dato eller tjek ligaens kampplan.\
-   AI-fejl: tjek API-nøgle og saldo.\
-   Build-fejl: verificér Java-version og kør `./mvnw clean install`.

## Dokumentation

-   README.md (denne)\
-   FOOTBALL_SETUP.md (detaljeret opsætning)\
-   QUICK_REFERENCE.md (hurtigt overblik)
