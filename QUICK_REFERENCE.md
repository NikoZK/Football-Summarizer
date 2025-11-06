# ⚡ Quick Reference Card (FREE VERSION!)

## 🚀 Start Application

```bash
# 1. Set OpenAI API Key (Football data is FREE!)
export API_KEY=your-openai-key

# 2. Start
./start.sh

# 3. Open Browser
http://localhost:8080
```

## 🔑 API Keys Required

| Service | Required? | Cost | URL |
|---------|-----------|------|-----|
| OpenAI | ✅ Yes | ~$0.01/match | https://platform.openai.com/api-keys |
| Football Data (ESPN) | ❌ No | FREE! | No registration needed |

## 🆓 ESPN API (FREE!)

**No API key needed!** Just use these endpoints:

```bash
# Premier League
http://site.api.espn.com/apis/site/v2/sports/soccer/eng.1/scoreboard

# La Liga  
http://site.api.espn.com/apis/site/v2/sports/soccer/esp.1/scoreboard

# Champions League
http://site.api.espn.com/apis/site/v2/sports/soccer/uefa.champions/scoreboard
```

## 🏟️ Change League

Edit `FootballService.java` line 24:

```java
private static final String DEFAULT_LEAGUE = "eng.1"; // Change here
```

### Popular Leagues (ESPN Format)
- `eng.1` = Premier League 🏴󠁧󠁢󠁥󠁮󠁧󠁿
- `esp.1` = La Liga 🇪🇸
- `ger.1` = Bundesliga 🇩🇪
- `ita.1` = Serie A 🇮🇹
- `fra.1` = Ligue 1 🇫🇷
- `usa.1` = MLS 🇺🇸
- `uefa.champions` = Champions League 🏆

## 🔌 API Endpoints

```bash
# Get matches for a date
GET /api/v1/football/matches?date=2024-11-04

# Get match summary
GET /api/v1/football/match/{fixtureId}/summary

# Get player performance
GET /api/v1/football/match/{fixtureId}/players?type=best
GET /api/v1/football/match/{fixtureId}/players?type=worst
```

## 🛠️ Useful Commands

```bash
# Build project
./mvnw clean package

# Run application
./mvnw spring-boot:run

# Quick start
./start.sh

# Clean build
./mvnw clean install
```

## 📁 Key Files

| File | Purpose |
|------|---------|
| `FootballService.java` | ESPN API integration |
| `FootballController.java` | REST endpoints |
| `index.html` | Main webpage |
| `main.js` | Frontend logic |
| `main.css` | Styles |

## 💰 Costs (Updated!)

| Service | Cost | Notes |
|---------|------|-------|
| ESPN API | **$0** | FREE forever! |
| OpenAI GPT-4 | ~$0.01-0.02 | Per match (optional) |
| **Total for 10 matches/day** | **~$3-6/month** | Just AI costs! |

## 🐛 Troubleshooting

### No matches?
```bash
# ESPN API is free - no key to check!
# Try different date
curl "http://site.api.espn.com/apis/site/v2/sports/soccer/eng.1/scoreboard"

# Check if matches exist for that date
```

### Build fails?
```bash
# Check Java version (need 17+)
java -version

# Clean build
./mvnw clean install -DskipTests
```

### AI not working?
```bash
# Check OpenAI key
echo $API_KEY

# Verify in logs
./mvnw spring-boot:run | grep ERROR
```

## 📚 Documentation

- **README.md** - Main guide
- **FOOTBALL_SETUP.md** - Setup details
- **QUICK_REFERENCE.md** - This file!

## 🎯 Quick Test

```bash
# Test ESPN API directly (no auth!)
curl "http://site.api.espn.com/apis/site/v2/sports/soccer/eng.1/scoreboard"

# Test your app
curl "http://localhost:8080/api/v1/football/matches?date=2024-11-04"
```

## 🆓 Why ESPN API is Great

1. ✅ **Zero Cost** - Completely free
2. ✅ **No Registration** - Start immediately
3. ✅ **No API Key** - No setup needed
4. ✅ **No Rate Limits** - Use as much as you want
5. ✅ **Reliable** - ESPN's infrastructure
6. ✅ **Rich Data** - Comprehensive match info

## ⚙️ Environment Variables

### Only ONE variable needed!

**Linux/Mac:**
```bash
export API_KEY=sk-your-openai-key
```

**Windows CMD:**
```cmd
set API_KEY=sk-your-openai-key
```

**Windows PowerShell:**
```powershell
$env:API_KEY="sk-your-openai-key"
```

**That's it!** No Football API key needed! 🎉

## 📊 Monitor Usage

### ESPN API
- ✅ No monitoring needed - it's free with no limits!

### OpenAI
- Usage: https://platform.openai.com/usage
- Monitor costs

## 🚀 Advantages Over Paid APIs

**Before (API-Football via RapidAPI):**
- 💰 Cost: $15-50/month
- 🔑 API key required
- ⏱️ Setup time: 10-15 minutes
- 📊 Rate limits: 100-1000 requests/day
- 📝 Registration required

**Now (ESPN API):**
- ✅ Cost: $0
- ✅ No API key needed
- ✅ Setup time: 0 minutes
- ✅ No rate limits
- ✅ No registration

## 🎨 Test Different Leagues

```bash
# Try different leagues instantly!

# Premier League
curl "http://site.api.espn.com/apis/site/v2/sports/soccer/eng.1/scoreboard"

# Champions League
curl "http://site.api.espn.com/apis/site/v2/sports/soccer/uefa.champions/scoreboard"

# MLS
curl "http://site.api.espn.com/apis/site/v2/sports/soccer/usa.1/scoreboard"
```

## 💡 Pro Tips

1. **Start with just OpenAI key** - Football data works without any key!
2. **Test ESPN API first** - Use curl to see available matches
3. **Change leagues easily** - Just edit one line of code
4. **No API limits** - Query as much as you want
5. **Zero ongoing costs** - Only pay for AI features if you use them

---

**Enjoy FREE football data! ⚽🆓**
