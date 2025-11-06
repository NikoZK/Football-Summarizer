# Football Match Center - Setup Guide (FREE VERSION!)

## Overview
This is a modern football/soccer website that displays match information, AI-powered match summaries, and player performance analysis using **ESPN's FREE API** - no authentication or API keys required for match data!

## Features
- 📅 View matches from any date (defaults to yesterday)
- ⚽ Beautiful match cards with team logos and scores
- 📊 Detailed match statistics including:
  - Possession
  - Shots on Goal
  - Yellow/Red Cards
- 🤖 AI-powered match summaries (OpenAI)
- ⭐ Best and worst player performance analysis
- 📱 Responsive design
- 🆓 **FREE Match Data** - No API key needed!

## API Setup

### 1. ESPN API (100% FREE!) ✅

**No setup required!** ESPN provides public API endpoints that don't require authentication.

- **URL**: `http://site.api.espn.com/apis/site/v2/sports/soccer/`
- **Cost**: FREE (no charges, no limits)
- **Authentication**: None required
- **Coverage**: Premier League, La Liga, Champions League, and more!

### 2. OpenAI API (Required for AI Features)

You need an OpenAI API key for AI-powered summaries:

1. Go to https://platform.openai.com/
2. Sign up or log in
3. Navigate to API keys section
4. Create a new API key
5. Copy the key

## Configuration

Set ONLY the OpenAI API key (Football data is FREE!):

```bash
export API_KEY=your-openai-api-key-here
```

Or on Windows:
```cmd
set API_KEY=your-openai-api-key-here
```

**That's it!** No Football API key needed! 🎉

## Running the Application

### Using Maven:

```bash
# Build the project
./mvnw clean install

# Run the application
./mvnw spring-boot:run
```

Or use the start script:
```bash
./start.sh
```

Or on Windows:
```cmd
mvnw.cmd clean install
mvnw.cmd spring-boot:run
```

### Access the Application:

Open your browser and navigate to:
```
http://localhost:8080
```

## API Endpoints

The application provides the following REST endpoints:

### Get Matches
```
GET /api/v1/football/matches?date=YYYY-MM-DD
```
Returns all matches for the specified date (defaults to yesterday).

**Example:**
```bash
curl "http://localhost:8080/api/v1/football/matches?date=2024-11-04"
```

### Get Match Summary
```
GET /api/v1/football/match/{fixtureId}/summary
```
Returns detailed statistics and AI summary for a specific match.

**Example:**
```bash
curl "http://localhost:8080/api/v1/football/match/740705/summary"
```

### Get Player Performance
```
GET /api/v1/football/match/{fixtureId}/players?type=best|worst
```
Returns top 5 best or worst performing players with AI analysis.

**Example:**
```bash
curl "http://localhost:8080/api/v1/football/match/740705/players?type=best"
```

## Available ESPN Endpoints

ESPN provides free data for various leagues:

### Soccer/Football

**Premier League (England)**:
```
http://site.api.espn.com/apis/site/v2/sports/soccer/eng.1/scoreboard
```

**La Liga (Spain)**:
```
http://site.api.espn.com/apis/site/v2/sports/soccer/esp.1/scoreboard
```

**Bundesliga (Germany)**:
```
http://site.api.espn.com/apis/site/v2/sports/soccer/ger.1/scoreboard
```

**Serie A (Italy)**:
```
http://site.api.espn.com/apis/site/v2/sports/soccer/ita.1/scoreboard
```

**Ligue 1 (France)**:
```
http://site.api.espn.com/apis/site/v2/sports/soccer/fra.1/scoreboard
```

**MLS (USA)**:
```
http://site.api.espn.com/apis/site/v2/sports/soccer/usa.1/scoreboard
```

**Champions League**:
```
http://site.api.espn.com/apis/site/v2/sports/soccer/uefa.champions/scoreboard
```

For more leagues and endpoints, see: https://gist.github.com/bhaidar/b2fdd34004250932a4a354a2cc15ddd4

## Technology Stack

- **Backend**: Spring Boot 3.2.1, Java 17
- **Frontend**: HTML5, CSS3, JavaScript (Vanilla)
- **UI Framework**: Bootstrap 5.3.2
- **Icons**: Font Awesome 6.4.0
- **APIs**: 
  - ESPN API (FREE - no authentication!)
  - OpenAI GPT-4 (for AI features)

## Customization

### Change League

Edit `FootballService.java` line 24:

```java
private static final String DEFAULT_LEAGUE = "eng.1"; // Change this!
```

Common league codes:
- `eng.1` - Premier League
- `esp.1` - La Liga
- `ger.1` - Bundesliga
- `ita.1` - Serie A
- `fra.1` - Ligue 1
- `usa.1` - MLS
- `uefa.champions` - Champions League

### Adjust AI Model

To change the OpenAI model or parameters, edit `application.properties`:

```properties
app.model=gpt-4o
app.temperature=0.8
app.max_tokens=300
```

## Cost Breakdown

### FREE Components (No Cost!)
- ✅ Match data from ESPN API
- ✅ Scores, teams, logos, venues
- ✅ Match statistics
- ✅ Application hosting (local)

### Paid Component
- 💵 OpenAI API for AI summaries
  - Cost: ~$0.01-0.02 per match analysis
  - Each match summary: ~100-200 tokens
  - Each player analysis: ~50-100 tokens

**Example Monthly Cost:**
- Viewing 10 matches/day with AI summaries: ~$3-6/month
- Viewing without AI summaries: **$0** (completely free!)

## Troubleshooting

### No matches showing
- ✅ No API key needed - should work immediately!
- Try selecting a different date
- Check the browser console for errors
- Verify internet connection
- Some dates may have no matches

### AI summaries not working
- Ensure your API_KEY (OpenAI) is set correctly
- Check your OpenAI account has credits
- Review the application logs for errors

### Build fails
```bash
# Check Java version (need 17+)
java -version

# Clean build
./mvnw clean install -DskipTests
```

## Advantages of ESPN API

1. **100% Free** - No charges ever
2. **No API Key** - Start immediately
3. **No Rate Limits** - Public endpoints
4. **Reliable** - Backed by ESPN
5. **Comprehensive** - Rich match data
6. **Multiple Sports** - Not just football!
7. **No Registration** - Zero setup required

## Testing the ESPN API

You can test the API directly in your browser or with curl:

```bash
# Get today's Premier League matches
curl "http://site.api.espn.com/apis/site/v2/sports/soccer/eng.1/scoreboard"

# Get match summary
curl "http://site.api.espn.com/apis/site/v2/sports/soccer/eng.1/summary?event=740705"

# Get teams
curl "http://site.api.espn.com/apis/site/v2/sports/soccer/eng.1/teams"
```

## Privacy & Data

- **ESPN API**: Public data, no authentication
- **OpenAI API**: Your API key is used securely
- **No User Data**: Application doesn't store user data
- **Local Only**: Runs on your machine

## Support

For issues or questions:
1. Check browser console for errors
2. Verify OpenAI API key is set (only key needed!)
3. Check application logs
4. Try a different date or league

## What's Different from Paid APIs?

**ESPN API (Free) vs API-Football (Paid)**:

| Feature | ESPN (Free) | API-Football |
|---------|-------------|--------------|
| Cost | $0 | $15-50/month |
| API Key | Not needed | Required |
| Setup Time | 0 minutes | 10-15 minutes |
| Rate Limits | None | 100-1000/day |
| Data Quality | Excellent | Excellent |
| Coverage | Good | Extensive |

## License

This project is for educational purposes.

---

**Enjoy FREE football match tracking! ⚽🆓**
