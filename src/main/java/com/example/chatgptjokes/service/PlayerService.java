package com.example.chatgptjokes.service;

import com.example.chatgptjokes.dtos.PlayerPerformanceResponse;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PlayerService {

    private final ESPNService espnService;
    private final AIService aiService;

    public PlayerService(ESPNService espnService, AIService aiService) {
        this.espnService = espnService;
        this.aiService = aiService;
    }

    public PlayerPerformanceResponse getPlayerPerformance(String fixtureId, String type) {
        try {
            JsonNode root = espnService.fetchMatchSummary(fixtureId);

            System.out.println("========== ESPN API RESPONSE ==========");
            System.out.println("Has rosters: " + root.has("rosters"));
            System.out.println("=======================================\n");

            Map<String, Integer> teamGoalsConceded = extractTeamGoalsConceded(root);
            List<PlayerPerformanceResponse.PlayerInfo> allPlayers = extractPlayersWithStats(root, teamGoalsConceded);

            if (allPlayers.isEmpty()) {
                System.err.println("⚠️ No players found — returning fallback");
                return createFallbackResponse(type);
            }

            List<PlayerPerformanceResponse.PlayerInfo> activePlayers = allPlayers.stream()
                    .filter(p -> p.getMinutes() > 0)
                    .toList();

            if (activePlayers.isEmpty()) {
                System.err.println("⚠️ No active players found");
                return createFallbackResponse(type);
            }

            List<PlayerPerformanceResponse.PlayerInfo> sortedPlayers = activePlayers.stream()
                    .sorted((a, b) -> type.equals("best")
                            ? Double.compare(b.getRating(), a.getRating())
                            : Double.compare(a.getRating(), b.getRating()))
                    .limit(5)
                    .toList();

            for (PlayerPerformanceResponse.PlayerInfo player : sortedPlayers) {
                String analysis = aiService.generatePlayerAnalysis(
                        player.getName(),
                        player.getPosition(),
                        player.getRating(),
                        type,
                        player.getMinutes(),
                        player.getGoals(),
                        player.getAssists(),
                        player.getShotsOnTarget(),
                        player.getSaves(),
                        player.getFoulsCommitted(),
                        player.getFoulsSuffered(),
                        player.getCleanSheet()
                );
                player.setAiAnalysis(analysis);
            }

            PlayerPerformanceResponse response = new PlayerPerformanceResponse();
            response.setType(type);
            response.setPlayers(sortedPlayers);
            return response;

        } catch (Exception e) {
            System.err.println("⚠️ Failed to fetch performance: " + e.getMessage());
            e.printStackTrace();
            return createFallbackResponse(type);
        }
    }

    private PlayerPerformanceResponse createFallbackResponse(String type) {
        PlayerPerformanceResponse fallback = new PlayerPerformanceResponse();
        fallback.setType(type);

        PlayerPerformanceResponse.PlayerInfo info = new PlayerPerformanceResponse.PlayerInfo();
        info.setName("Data Unavailable");
        info.setTeam("");
        info.setPosition("");
        info.setMinutes(0);
        info.setRating(0.0);
        info.setAiAnalysis("Player performance data is not available for this match.");

        fallback.setPlayers(List.of(info));
        return fallback;
    }

    private Map<String, Integer> extractTeamGoalsConceded(JsonNode root) {
        Map<String, Integer> map = new HashMap<>();
        try {
            JsonNode competitions = root.path("header").path("competitions");
            if (competitions.isArray() && competitions.size() > 0) {
                JsonNode competition = competitions.get(0);
                JsonNode competitors = competition.path("competitors");
                String homeTeam = "", awayTeam = "";
                int homeScore = 0, awayScore = 0;

                for (JsonNode competitor : competitors) {
                    String teamName = competitor.path("team").path("displayName").asText();
                    int score = competitor.path("score").asInt(0);
                    String homeAway = competitor.path("homeAway").asText();

                    if ("home".equals(homeAway)) {
                        homeTeam = teamName;
                        homeScore = score;
                    } else {
                        awayTeam = teamName;
                        awayScore = score;
                    }
                }

                map.put(homeTeam, awayScore);
                map.put(awayTeam, homeScore);

                System.out.println("Goals conceded → " + homeTeam + ": " + awayScore + ", " + awayTeam + ": " + homeScore);
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error extracting goals conceded: " + e.getMessage());
        }
        return map;
    }

    private List<PlayerPerformanceResponse.PlayerInfo> extractPlayersWithStats(
            JsonNode root, Map<String, Integer> teamGoalsConceded) {

        List<PlayerPerformanceResponse.PlayerInfo> allPlayers = new ArrayList<>();
        JsonNode rosters = root.path("rosters");

        if (!rosters.isArray() || rosters.size() == 0) {
            System.err.println("⚠️ No roster data");
            return allPlayers;
        }

        // Pass 1: determine minutes played based on plays
        Map<String, Integer> playerMinutesMap = new HashMap<>();

        for (JsonNode rosterTeam : rosters) {
            JsonNode roster = rosterTeam.path("roster");
            if (!roster.isArray()) continue;

            for (JsonNode playerNode : roster) {
                JsonNode athlete = playerNode.path("athlete");
                String athleteId = athlete.path("id").asText("");
                if (athleteId.isEmpty()) continue;

                boolean starter = playerNode.path("starter").asBoolean(false);
                boolean active = playerNode.path("active").asBoolean(true);
                boolean didNotPlay = playerNode.path("didNotPlay").asBoolean(false);
                if (!active || didNotPlay) continue;

                JsonNode plays = playerNode.path("plays");
                int minutes = 0;

                if (plays.isArray() && plays.size() > 0) {
                    for (int i = 0; i < plays.size(); i++) {
                        JsonNode play = plays.get(i);
                        boolean substitution = play.path("substitution").asBoolean(false);
                        String clockText = play.path("clock").path("displayValue").asText("");

                        if (substitution) {
                            if (playerNode.path("subbedOut").asBoolean(false)) {
                                minutes = parseClockToMinute(clockText);
                            } else if (playerNode.path("subbedIn").asBoolean(false)) {
                                minutes = 90 - parseClockToMinute(clockText);
                            }
                        }
                    }
                    if (minutes == 0 && starter) minutes = 90;
                } else {
                    minutes = starter ? 90 : 0;
                }

                playerMinutesMap.put(athleteId, minutes);
            }
        }

        System.out.println("Player minutes map: " + playerMinutesMap);

        // Pass 2: build player objects
        for (JsonNode rosterTeam : rosters) {
            String teamName = rosterTeam.path("team").path("displayName").asText();
            JsonNode roster = rosterTeam.path("roster");
            if (!roster.isArray()) continue;

            int goalsAgainst = teamGoalsConceded.getOrDefault(teamName, 0);
            boolean cleanSheet = goalsAgainst == 0;

            System.out.println("\nProcessing team: " + teamName);

            for (JsonNode playerNode : roster) {
                try {
                    JsonNode athlete = playerNode.path("athlete");
                    String name = athlete.path("displayName").asText();
                    String athleteId = athlete.path("id").asText("");
                    if (name.isEmpty()) continue;

                    int minutes = playerMinutesMap.getOrDefault(athleteId, 0);
                    if (minutes <= 0) continue;

                    String position = playerNode.path("position").path("displayName").asText("Unknown");

                    JsonNode statsArray = playerNode.path("stats");
                    PlayerStats stats = new PlayerStats();
                    int shotsOnTarget = 0;
                    int foulsSuffered = 0;
                    int foulsCommitted = 0;

                    if (statsArray.isArray()) {
                        for (JsonNode stat : statsArray) {
                            String statName = stat.path("name").asText("");
                            double value = stat.path("value").asDouble(0);
                            switch (statName) {
                                case "totalGoals" -> stats.goals = (int) value;
                                case "goalAssists" -> stats.assists = (int) value;
                                case "saves" -> stats.saves = (int) value;
                                case "goalsConceded" -> stats.goalsConceded = (int) value;
                                case "foulsCommitted" -> stats.foulsCommitted = (int) value;
                                case "foulsSuffered" -> stats.foulsSuffered = (int) value;
                                case "shotsOnTarget" -> stats.shotsOnTarget = (int) value;
                            }
                        }
                    }

                    double rating = calculatePlayerRating(
                            position,
                            minutes,
                            stats.goals,
                            stats.assists,
                            stats.saves,
                            cleanSheet,
                            stats.goalsConceded,
                            stats.shotsOnTarget,
                            stats.foulsSuffered,
                            stats.foulsCommitted
                    );

                    PlayerPerformanceResponse.PlayerInfo info = new PlayerPerformanceResponse.PlayerInfo();
                    info.setName(name);
                    info.setTeam(teamName);
                    info.setPosition(position);
                    info.setMinutes(minutes);
                    info.setRating(rating);
                    info.setGoals(stats.goals);
                    info.setAssists(stats.assists);
                    info.setSaves(stats.saves);
                    info.setShotsOnTarget(stats.shotsOnTarget);
                    info.setFoulsCommitted(stats.foulsCommitted);
                    info.setFoulsSuffered(stats.foulsSuffered);
                    info.setCleanSheet(cleanSheet);

                    allPlayers.add(info);

                    System.out.printf("✓ %s (%s) [%s] - %d min | G:%d A:%d SV:%d | Shots:%d FoulsSuffered:%d FoulsCommitted:%d | Rating: %.1f%n",
                            name, teamName, position, minutes, stats.goals, stats.assists, stats.saves, stats.shotsOnTarget, stats.foulsSuffered, stats.foulsCommitted, rating);

                } catch (Exception e) {
                    System.err.println("⚠️ Error processing player: " + e.getMessage());
                }
            }
        }

        System.out.println("\nTotal players extracted: " + allPlayers.size());
        return allPlayers;
    }

    private int parseClockToMinute(String clockText) {
        if (clockText == null || clockText.isEmpty()) return 0;
        try {
            clockText = clockText.replace("'", "");
            if (clockText.contains("+")) {
                String[] parts = clockText.split("\\+");
                return Integer.parseInt(parts[0]) + Integer.parseInt(parts[1]);
            }
            return Integer.parseInt(clockText);
        } catch (Exception e) {
            return 0;
        }
    }

    private double calculatePlayerRating(String position, int minutes, int goals, int assists,
                                         int saves, boolean cleanSheet, int goalsAgainst,
                                         int shotsOnTarget, int foulsSuffered, int foulsCommitted) {

        double rating = 40.0; // minimum rating
        double performanceFactor = (goals * 25 + assists * 15 + saves * 0.5 + shotsOnTarget * 1.5 + foulsSuffered * 2);
        rating += performanceFactor * (minutes / 90.0);


        String pos = position.toUpperCase();
        boolean isGK = pos.contains("GOALKEEPER") || pos.equals("GK");
        boolean isDF = pos.contains("DEFENDER") || pos.equals("DF") || pos.contains("BACK");
        boolean isMF = pos.contains("MIDFIELDER") || pos.equals("MF") || pos.contains("MID");
        boolean isFW = pos.contains("FORWARD") || pos.equals("FW") || pos.contains("STRIKER") || pos.contains("ATTACK");
        boolean isWing = pos.contains("LEFT") || pos.contains("RIGHT"); // for wingers

        if (isGK) {
            if (cleanSheet) rating += 18;
            rating += saves * 1.5;
            rating -= goalsAgainst * 3;
            rating += goals * 27;
            rating += assists * 22;
        } else if (isDF) {
            if (cleanSheet) rating += 15;
            rating -= goalsAgainst * 2;
            rating += goals * 21;
            rating += assists * 17;
            rating += foulsSuffered * 1.1;
            rating -= foulsCommitted * 1.5;
        } else if (isMF) {
            if (cleanSheet) rating += 6;
            rating += goals * 17;
            rating += shotsOnTarget * 1.6;
            rating += assists * 14;
            rating += foulsSuffered * 3;
            rating -= foulsCommitted * 1.5;
        } else if (isFW) {
            if (isWing) {
                rating += goals * 16;
                rating += shotsOnTarget * 1.5;
            } else { // central striker
                rating += goals * 10;
                rating += shotsOnTarget * 1.4;
            }
            rating += assists * 12;
            rating += foulsSuffered * 3;
            rating -= foulsCommitted * 1.2;

            // Penalize low shooting activity
            double expectedShots = minutes / 90.0 * 4; // e.g., 3 shots per full match expected
            if (shotsOnTarget < expectedShots) {
                double shotPenalty = (expectedShots - shotsOnTarget) * 3; // 2 points per missing shot
                rating -= shotPenalty;
            }
        } else {
            rating += goals * 15;
            rating += assists * 10;
        }

        rating = 40 + (rating - 40) * 0.75;

        rating = Math.max(30, Math.min(100, rating));
        return Math.round(rating * 10.0) / 10.0;
    }


    private static class PlayerStats {
        int goals = 0;
        int assists = 0;
        int saves = 0;
        int goalsConceded = 0;
        int foulsCommitted = 0;
        int foulsSuffered = 0;
        int shotsOnTarget = 0;
    }
}
