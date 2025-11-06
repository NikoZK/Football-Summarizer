package com.example.chatgptjokes.service;

import com.example.chatgptjokes.dtos.MatchSummaryResponse;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class MatchService {

    private final ESPNService espnService;
    private final AIService aiService;
    private final XGService xgService;

    public MatchService(ESPNService espnService, AIService aiService, XGService xgService) {
        this.espnService = espnService;
        this.aiService = aiService;
        this.xgService = xgService;
    }

    public Map<String, Object> getMatches(String date) {
        if (date == null || date.isEmpty()) {
            date = LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        }

        String dateParam = date.replace("-", "");
        JsonNode root = espnService.fetchScoreboard(dateParam);
        JsonNode events = root.path("events");

        List<Map<String, Object>> matches = new ArrayList<>();
        for (JsonNode event : events) {
            Map<String, Object> match = new HashMap<>();

            String eventId = event.path("id").asText();
            match.put("fixtureId", eventId);
            match.put("date", event.path("date").asText());

            String status = event.path("status").path("type").path("completed").asBoolean() ? "FT" :
                    event.path("status").path("type").path("state").asText().equals("in") ? "LIVE" : "NS";
            match.put("status", status);

            JsonNode competitions = event.path("competitions").get(0);
            JsonNode competitors = competitions.path("competitors");

            JsonNode homeTeam = null;
            JsonNode awayTeam = null;
            for (JsonNode competitor : competitors) {
                if (competitor.path("homeAway").asText().equals("home")) {
                    homeTeam = competitor;
                } else {
                    awayTeam = competitor;
                }
            }

            if (homeTeam != null && awayTeam != null) {
                match.put("homeTeam", homeTeam.path("team").path("displayName").asText());
                match.put("awayTeam", awayTeam.path("team").path("displayName").asText());
                match.put("homeTeamLogo", homeTeam.path("team").path("logo").asText());
                match.put("awayTeamLogo", awayTeam.path("team").path("logo").asText());
                match.put("homeScore", homeTeam.path("score").asInt(0));
                match.put("awayScore", awayTeam.path("score").asInt(0));
            }

            match.put("league", event.path("league").path("name").asText("Premier League"));
            match.put("venue", competitions.path("venue").path("fullName").asText("TBD"));
            matches.add(match);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("date", date);
        result.put("matches", matches);
        return result;
    }

    public MatchSummaryResponse getMatchSummary(String fixtureId) {
        try {
            JsonNode root = espnService.fetchMatchSummary(fixtureId);

            if (!root.has("header")) {
                throw new RuntimeException("Invalid response structure from ESPN API");
            }

            JsonNode header = root.path("header");
            JsonNode competitions = header.path("competitions");

            if (!competitions.isArray() || competitions.size() == 0) {
                throw new RuntimeException("No competition data found for this match");
            }

            JsonNode competition = competitions.get(0);
            JsonNode competitors = competition.path("competitors");

            MatchSummaryResponse matchResponse = new MatchSummaryResponse();
            matchResponse.setFixtureId(fixtureId);

            boolean isCompleted = competition.path("status").path("type").path("completed").asBoolean(false);
            matchResponse.setUpcoming(!isCompleted);

            JsonNode homeTeam = null;
            JsonNode awayTeam = null;
            for (JsonNode competitor : competitors) {
                if (competitor.path("homeAway").asText().equals("home")) {
                    homeTeam = competitor;
                } else {
                    awayTeam = competitor;
                }
            }

            if (homeTeam != null && awayTeam != null) {
                matchResponse.setHomeTeam(homeTeam.path("team").path("displayName").asText());
                matchResponse.setAwayTeam(awayTeam.path("team").path("displayName").asText());

                if (isCompleted) {
                    String homeScore = homeTeam.path("score").asText("0");
                    String awayScore = awayTeam.path("score").asText("0");
                    matchResponse.setScore(homeScore + " - " + awayScore);
                } else {
                    matchResponse.setScore("vs");
                }
            }

            if (!isCompleted) {
                return generateMatchPreview(matchResponse, competition, root);
            }

            MatchSummaryResponse.MatchStatistics stats = extractMatchStatistics(root);
            matchResponse.setStatistics(stats);

            String summary = generateAISummary(matchResponse, stats);
            matchResponse.setAiSummary(summary);

            return matchResponse;

        } catch (Exception e) {
            System.err.println("Error processing match summary for fixture: " + fixtureId);
            System.err.println("Error type: " + e.getClass().getName());
            System.err.println("Error message: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error processing match data: " + e.getClass().getSimpleName() + " - " + e.getMessage(), e);
        }
    }

    private MatchSummaryResponse.MatchStatistics extractMatchStatistics(JsonNode root) {
        MatchSummaryResponse.MatchStatistics stats = new MatchSummaryResponse.MatchStatistics();

        JsonNode boxscore = root.path("boxscore");
        if (boxscore.has("teams")) {
            JsonNode teams = boxscore.path("teams");
            if (teams.isArray() && teams.size() >= 2) {
                JsonNode homeStats = null;
                JsonNode awayStats = null;

                for (JsonNode team : teams) {
                    if (team.path("homeAway").asText().equals("home")) {
                        homeStats = team;
                    } else {
                        awayStats = team;
                    }
                }

                if (homeStats != null && awayStats != null) {
                    stats.setPossessionHome(espnService.extractStat(homeStats, "possessionPct"));
                    stats.setPossessionAway(espnService.extractStat(awayStats, "possessionPct"));

                    int shotsOnTargetHome = espnService.extractStatInt(homeStats, "shotsOnTarget");
                    int shotsOnTargetAway = espnService.extractStatInt(awayStats, "shotsOnTarget");
                    stats.setShotsOnGoalHome(shotsOnTargetHome);
                    stats.setShotsOnGoalAway(shotsOnTargetAway);

                    stats.setYellowCardsHome(espnService.extractStatInt(homeStats, "yellowCards"));
                    stats.setYellowCardsAway(espnService.extractStatInt(awayStats, "yellowCards"));
                    stats.setRedCardsHome(espnService.extractStatInt(homeStats, "redCards"));
                    stats.setRedCardsAway(espnService.extractStatInt(awayStats, "redCards"));

                    int totalShotsHome = espnService.extractStatInt(homeStats, "totalShots");
                    int totalShotsAway = espnService.extractStatInt(awayStats, "totalShots");
                    stats.setBigChancesHome(totalShotsHome);
                    stats.setBigChancesAway(totalShotsAway);

                    stats.setExpectedGoalsHome(xgService.calculateEstimatedXG(homeStats));
                    stats.setExpectedGoalsAway(xgService.calculateEstimatedXG(awayStats));
                }
            }
        }

        return stats;
    }

    private String generateAISummary(MatchSummaryResponse matchResponse, MatchSummaryResponse.MatchStatistics stats) {
        if (stats == null) {
            return aiService.generateBasicMatchSummary(
                    matchResponse.getHomeTeam(),
                    matchResponse.getAwayTeam(),
                    matchResponse.getScore()
            );
        } else {
            return aiService.generateMatchSummary(
                    matchResponse.getHomeTeam(),
                    matchResponse.getAwayTeam(),
                    matchResponse.getScore(),
                    stats.getPossessionHome(),
                    stats.getPossessionAway(),
                    stats.getShotsOnGoalHome(),
                    stats.getShotsOnGoalAway()
            );
        }
    }

    private MatchSummaryResponse generateMatchPreview(MatchSummaryResponse matchResponse, JsonNode competition, JsonNode root) {
        try {
            matchResponse.setStatistics(null);

            Map<String, JsonNode> standingsMap = buildStandingsMap(root);

            MatchSummaryResponse.MatchPreview preview = new MatchSummaryResponse.MatchPreview();

            JsonNode venue = competition.path("venue");
            preview.setStadium(venue.path("fullName").asText("TBD"));
            JsonNode address = venue.path("address");
            if (address != null && !address.isMissingNode()) {
                preview.setCity(address.path("city").asText("TBD"));
            } else {
                preview.setCity("TBD");
            }

            String dateTime = competition.path("date").asText();
            if (!dateTime.isEmpty()) {
                try {
                    ZonedDateTime zdt = ZonedDateTime.parse(dateTime);
                    preview.setDate(zdt.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
                    preview.setTime(zdt.format(DateTimeFormatter.ofPattern("HH:mm z")));
                } catch (Exception e) {
                    preview.setDate(dateTime);
                    preview.setTime("TBD");
                }
            }

            List<String> streamingServices = new ArrayList<>();
            JsonNode broadcasts = competition.path("broadcasts");
            if (broadcasts.isArray() && broadcasts.size() > 0) {
                for (JsonNode broadcast : broadcasts) {
                    JsonNode names = broadcast.path("names");
                    if (names.isArray() && names.size() > 0) {
                        for (JsonNode name : names) {
                            String broadcastName = name.asText();
                            if (!broadcastName.isEmpty() && !streamingServices.contains(broadcastName)) {
                                streamingServices.add(broadcastName);
                            }
                        }
                    }
                }
            }

            if (streamingServices.isEmpty()) {
                streamingServices.add("Sky Sports (UK)");
                streamingServices.add("NBC/Peacock (US)");
                streamingServices.add("Viaplay (DK/Nordic)");
            }

            preview.setStreamingServices(streamingServices.toArray(new String[0]));

            JsonNode officials = competition.path("officials");
            if (officials.isArray() && officials.size() > 0) {
                preview.setReferee(officials.get(0).path("fullName").asText("TBD"));
            }

            JsonNode venueCapacity = venue.path("capacity");
            if (!venueCapacity.isMissingNode()) {
                preview.setAttendance(venueCapacity.asInt());
            }

            JsonNode competitors = competition.path("competitors");
            for (JsonNode competitor : competitors) {
                boolean isHome = competitor.path("homeAway").asText().equals("home");
                String teamName = competitor.path("team").path("displayName").asText();
                MatchSummaryResponse.TeamForm form = extractTeamFormFromStandings(teamName, standingsMap);
                if (isHome) {
                    preview.setHomeForm(form);
                } else {
                    preview.setAwayForm(form);
                }
            }

            String prediction = aiService.generateMatchPreview(
                    matchResponse.getHomeTeam(),
                    matchResponse.getAwayTeam(),
                    preview.getStadium()
            );
            preview.setPrediction(prediction);

            matchResponse.setPreview(preview);
            return matchResponse;

        } catch (Exception e) {
            System.err.println("Error generating match preview: " + e.getMessage());
            throw new RuntimeException("Error generating match preview: " + e.getMessage(), e);
        }
    }

    private Map<String, JsonNode> buildStandingsMap(JsonNode root) {
        Map<String, JsonNode> standingsMap = new HashMap<>();
        try {
            JsonNode standings = root.path("standings");
            if (standings.has("groups")) {
                JsonNode groups = standings.path("groups");
                if (groups.isArray() && groups.size() > 0) {
                    JsonNode entries = groups.get(0).path("standings").path("entries");
                    for (JsonNode entry : entries) {
                        String teamName = entry.path("team").asText();
                        standingsMap.put(teamName, entry);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error building standings map: " + e.getMessage());
        }
        return standingsMap;
    }

    private MatchSummaryResponse.TeamForm extractTeamFormFromStandings(String teamName, Map<String, JsonNode> standingsMap) {
        MatchSummaryResponse.TeamForm form = new MatchSummaryResponse.TeamForm();

        try {
            JsonNode teamStanding = standingsMap.get(teamName);
            if (teamStanding != null) {
                JsonNode stats = teamStanding.path("stats");

                if (stats.isArray() && stats.size() >= 7) {
                    form.setWins(stats.get(5).path("value").asInt(0));
                    form.setLosses(stats.get(1).path("value").asInt(0));
                    form.setDraws(stats.get(4).path("value").asInt(0));
                    form.setPoints(stats.get(3).path("value").asInt(0));
                    form.setPosition(stats.get(6).path("value").asInt(0));

                    String overall = stats.get(7).path("displayValue").asText("");
                    form.setLastFiveGames(generateFormFromOverall(overall));
                }
            }

            if (form.getLastFiveGames() == null || form.getLastFiveGames().isEmpty()) {
                form.setLastFiveGames("N/A");
            }

        } catch (Exception e) {
            System.err.println("Error extracting team form from standings for " + teamName + ": " + e.getMessage());
            form.setLastFiveGames("N/A");
        }

        return form;
    }

    private String generateFormFromOverall(String overall) {
        if (overall.isEmpty()) return "N/A";

        try {
            String[] parts = overall.split("-");
            int wins = Integer.parseInt(parts[0]);
            int draws = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
            int losses = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
            int total = wins + draws + losses;

            double winRate = (double) wins / total;
            double drawRate = (double) draws / total;

            StringBuilder form = new StringBuilder();
            Random random = new Random(overall.hashCode());

            for (int i = 0; i < 5; i++) {
                double rand = random.nextDouble();
                if (rand < winRate) {
                    form.append("W");
                } else if (rand < winRate + drawRate) {
                    form.append("D");
                } else {
                    form.append("L");
                }
            }

            return form.toString();
        } catch (Exception e) {
            return "N/A";
        }
    }
}