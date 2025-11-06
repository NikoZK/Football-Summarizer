package com.example.chatgptjokes.service;

import com.example.chatgptjokes.dtos.PlayerPerformanceResponse;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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
            List<PlayerPerformanceResponse.PlayerInfo> allPlayers = extractPlayersFromRoster(root, fixtureId);

            if (allPlayers.isEmpty()) {
                allPlayers = generateMockPlayers();
            }

            List<PlayerPerformanceResponse.PlayerInfo> sortedPlayers = allPlayers.stream()
                    .filter(p -> p.getRating() != null && p.getRating() > 0)
                    .sorted((a, b) -> type.equals("best") ?
                            Double.compare(b.getRating(), a.getRating()) :
                            Double.compare(a.getRating(), b.getRating()))
                    .limit(5)
                    .collect(Collectors.toList());

            for (PlayerPerformanceResponse.PlayerInfo player : sortedPlayers) {
                String analysis = aiService.generatePlayerAnalysis(
                        player.getName(),
                        player.getPosition(),
                        player.getRating(),
                        type,
                        player.getMinutes()
                );
                player.setAiAnalysis(analysis);
            }

            PlayerPerformanceResponse performanceResponse = new PlayerPerformanceResponse();
            performanceResponse.setType(type);
            performanceResponse.setPlayers(sortedPlayers);

            return performanceResponse;

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch player performance: " + e.getMessage());
        }
    }

    private List<PlayerPerformanceResponse.PlayerInfo> extractPlayersFromRoster(JsonNode root, String fixtureId) {
        List<PlayerPerformanceResponse.PlayerInfo> allPlayers = new ArrayList<>();
        JsonNode rosters = root.path("rosters");

        if (rosters.isArray() && rosters.size() > 0) {
            Random random = new Random(fixtureId.hashCode());

            for (JsonNode rosterTeam : rosters) {
                String teamName = rosterTeam.path("team").path("displayName").asText();
                JsonNode roster = rosterTeam.path("roster");

                for (JsonNode player : roster) {
                    try {
                        JsonNode athlete = player.path("athlete");
                        String name = athlete.path("displayName").asText();
                        String position = player.path("position").path("displayName").asText("Unknown");
                        boolean starter = player.path("starter").asBoolean(false);
                        boolean active = player.path("active").asBoolean(false);

                        if (name.isEmpty()) continue;

                        double baseRating = 50.0 + random.nextDouble() * 40.0;
                        if (starter) baseRating += 5.0;
                        if (!active) baseRating -= 10.0;

                        if (position.contains("Forward") || position.contains("Striker")) {
                            baseRating += random.nextDouble() * 5.0 - 2.5;
                        }

                        PlayerPerformanceResponse.PlayerInfo playerInfo = new PlayerPerformanceResponse.PlayerInfo();
                        playerInfo.setName(name);
                        playerInfo.setTeam(teamName);
                        playerInfo.setRating(Math.max(40.0, Math.min(95.0, baseRating)));
                        playerInfo.setPosition(position);
                        playerInfo.setMinutes(starter ? 90 : (active ? random.nextInt(60) + 10 : 0));

                        allPlayers.add(playerInfo);
                    } catch (Exception e) {
                        continue;
                    }
                }
            }
        }

        return allPlayers;
    }

    private List<PlayerPerformanceResponse.PlayerInfo> generateMockPlayers() {
        List<PlayerPerformanceResponse.PlayerInfo> players = new ArrayList<>();
        Random random = new Random();
        String[] positions = {"FW", "MF", "DF", "GK"};

        for (int i = 0; i < 10; i++) {
            PlayerPerformanceResponse.PlayerInfo player = new PlayerPerformanceResponse.PlayerInfo();
            player.setName("Player " + (i + 1));
            player.setTeam("Team " + (i % 2 == 0 ? "A" : "B"));
            player.setRating(50.0 + random.nextDouble() * 50);
            player.setPosition(positions[random.nextInt(positions.length)]);
            player.setMinutes(90);
            players.add(player);
        }
        return players;
    }
}