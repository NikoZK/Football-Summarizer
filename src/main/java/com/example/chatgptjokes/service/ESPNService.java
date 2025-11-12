package com.example.chatgptjokes.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ESPNService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    private static final String ESPN_API_BASE = "http://site.api.espn.com/apis/site/v2/sports/soccer";
    private static final String DEFAULT_LEAGUE = "eng.1"; // English Premier League

    public ESPNService() {
        this.webClient = WebClient.builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(5 * 1024 * 1024)) // 5MB buffer
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public JsonNode fetchScoreboard(String date) {
        String url = ESPN_API_BASE + "/" + DEFAULT_LEAGUE + "/scoreboard?dates=" + date;

        try {
            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return objectMapper.readTree(response);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch scoreboard from ESPN: " + e.getMessage(), e);
        }
    }

    public JsonNode fetchMatchSummary(String fixtureId) {
        String url = ESPN_API_BASE + "/" + DEFAULT_LEAGUE + "/summary?event=" + fixtureId;

        try {
            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return objectMapper.readTree(response);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch match summary from ESPN: " + e.getMessage(), e);
        }
    }

    public Double extractStat(JsonNode teamStats, String statName) {
        JsonNode statistics = teamStats.path("statistics");
        for (JsonNode stat : statistics) {
            if (stat.path("name").asText().equals(statName)) {
                String displayValue = stat.path("displayValue").asText();
                try {
                    double value = Double.parseDouble(displayValue.replace("%", "").trim());
                    return value;

                } catch (NumberFormatException e) {
                    return 50.0;
                }
            }
        }
        return 50.0;
    }

    public Integer extractStatInt(JsonNode teamStats, String statName) {
        JsonNode statistics = teamStats.path("statistics");
        for (JsonNode stat : statistics) {
            if (stat.path("name").asText().equals(statName)) {
                String displayValue = stat.path("displayValue").asText();
                try {
                    return Integer.parseInt(displayValue);
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        }
        return 0;
    }
}