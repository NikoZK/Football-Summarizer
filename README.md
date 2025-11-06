# ⚽ Football Match Center

A modern, AI-powered football (soccer) match tracking website that displays live match information, detailed statistics, and intelligent player performance analysis using **ESPN's FREE API** - no API keys required for match data!

## 🌟 Features

- **📅 Date-based Match Browsing** - View matches from any date with an intuitive date picker
- **🏟️ Beautiful Match Cards** - Elegant display with team logos, scores, and match status
- **📊 Detailed Match Statistics**:
  - Ball Possession (visual progress bars)
  - Shots on Goal
  - Yellow/Red Cards
  - And more...
- **🤖 AI-Powered Analysis**:
  - Automatic match summaries using GPT-4
  - Best performers analysis with AI insights
  - Worst performers analysis with constructive feedback
- **📱 Fully Responsive** - Works beautifully on desktop, tablet, and mobile devices
- **⚡ Real-time Data** - Powered by ESPN's FREE API with comprehensive match data
- **🆓 No Football API Key Required** - Uses ESPN's public API endpoints!

## 🚀 Quick Start

### Prerequisites

1. **Java 17** or higher
2. **Maven** (included via wrapper)
3. **API Key** (only OpenAI):
   - OpenAI API Key ([Get it here](https://platform.openai.com/api-keys))
   - ✅ **Football API is FREE** - No key needed!

### Installation

1. **Clone the repository**
```bash
git clone <your-repo-url>
cd chatgpt-jokes
```

2. **Set up OpenAI API key** (Football data is FREE!)
```bash
export API_KEY=your-openai-api-key
```

Or on Windows:
```cmd
set API_KEY=your-openai-api-key
```

3. **Run the application**
```bash
./start.sh
```

Or manually:
```bash
./mvnw spring-boot:run
```

4. **Open your browser**
```
http://localhost:8080
```

## 📖 Data Sources

### Match Data - ESPN API (100% FREE!)
- **Source**: ESPN's public API endpoints
- **Cost**: $0 - Completely free
- **Rate Limit**: None (public API)
- **Coverage**: Premier League, Champions League, and more
- **No API Key Required** ✅

### AI Analysis - OpenAI GPT-4
- **Source**: OpenAI API
- **Cost**: ~$0.01-0.02 per match analysis
- **API Key**: Required

## 🎮 How to Use

1. **Select a Date**: Use the date picker to choose which day's matches you want to view
2. **Browse Matches**: Scroll through the beautiful match cards showing scores and team logos
3. **View Details**: Click on any match card to open detailed statistics and AI summary
4. **Analyze Players**: Click "Best Performers" or "Worst Performers" to see AI-powered player analysis

## 🛠️ Technology Stack

### Backend
- **Spring Boot 3.2.1** - Modern Java framework
- **Java 17** - Latest LTS version
- **WebClient** - Reactive HTTP client

### Frontend
- **HTML5, CSS3, JavaScript** - Pure vanilla JS
- **Bootstrap 5.3.2** - Responsive UI components
- **Font Awesome 6.4** - Beautiful icons

### APIs
- **ESPN API** - FREE football data (no authentication!)
- **OpenAI GPT-4** - AI-powered summaries and analysis

## 🔌 API Endpoints

### Get Matches
```
GET /api/v1/football/matches?date=2024-11-04
```
Returns all matches for the specified date.

### Get Match Summary
```
GET /api/v1/football/match/{fixtureId}/summary
```
Returns detailed statistics and AI summary for a match.

### Get Player Performance
```
GET /api/v1/football/match/{fixtureId}/players?type=best|worst
```
Returns top 5 best or worst performing players with AI analysis.

## 🎨 Customization

### Change League
Edit `FootballService.java` line 24:

```java
private static final String DEFAULT_LEAGUE = "eng.1"; // English Premier League
```

### Available Leagues (ESPN Format)
- `eng.1` - Premier League (England)
- `esp.1` - La Liga (Spain)
- `ger.1` - Bundesliga (Germany)
- `ita.1` - Serie A (Italy)
- `fra.1` - Ligue 1 (France)
- `usa.1` - MLS (USA)
- `uefa.champions` - Champions League

### Adjust AI Model
Edit `application.properties`:
```properties
app.model=gpt-4o
app.temperature=0.8
app.max_tokens=300
```

## 💰 Cost Breakdown

### Completely FREE Components:
- ✅ **Match Data (ESPN API)** - $0
- ✅ **Basic Application** - $0
- ✅ **Hosting (local)** - $0

### Paid Component:
- 💵 **OpenAI GPT-4** - ~$0.01-0.02 per match analysis (optional)

**Total Monthly Cost (viewing 10 matches/day)**: ~$3-6 for AI features only!

## 🐛 Troubleshooting

### No matches showing?
- Try a different date (match availability varies)
- Check if the league has matches on that date
- Check browser console for errors
- Verify internet connection

### AI summaries not working?
- Verify your `API_KEY` (OpenAI) is valid
- Check your OpenAI account has available credits
- Review application logs for errors

### Build failures?
- Ensure Java 17 or higher is installed: `java -version`
- Run `./mvnw clean install -DskipTests`

## 🎯 Benefits of ESPN API

1. **No API Key Required** - Start immediately!
2. **No Rate Limits** - Public endpoints
3. **No Cost** - Completely free
4. **Reliable** - Backed by ESPN
5. **Comprehensive** - Rich match data
6. **Multiple Leagues** - Various competitions available

## �� Documentation

- **README.md** - This file
- **FOOTBALL_SETUP.md** - Detailed setup guide
- **QUICK_REFERENCE.md** - Quick commands & tips

## 📝 License

This project is for educational purposes.

## 🤝 Contributing

Contributions are welcome! Feel free to submit issues and pull requests.

---

**Enjoy tracking your favorite football matches with AI-powered insights - now with FREE match data! ⚽🎉**
