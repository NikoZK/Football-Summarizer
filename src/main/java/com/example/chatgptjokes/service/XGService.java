package com.example.chatgptjokes.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

@Service
public class XGService {

    private final ESPNService espnService;

    public XGService(ESPNService espnService) {
        this.espnService = espnService;
    }

    public Double calculateEstimatedXG(JsonNode teamStats) {
        int shotsOnTarget = espnService.extractStatInt(teamStats, "shotsOnTarget");
        int totalShots = espnService.extractStatInt(teamStats, "totalShots");
        int blockedShots = espnService.extractStatInt(teamStats, "blockedShots");

        if (totalShots <= 0) return 0.0;

        int offTargetShots = Math.max(0, totalShots - shotsOnTarget - blockedShots);

        // Hovedformularen
        double xg = (shotsOnTarget * 0.32)
                + (blockedShots * 0.08)
                + (offTargetShots * 0.06);

        // Reducering af xg hvis spammer skud
        if (totalShots > 15) {
            double correction = 1.0 - Math.min(0.3, (totalShots - 15) * 0.02);
            xg *= correction;
        }

        // Reducer topenden med 0.85
        xg *= 0.85;

        // Afrund
        return Math.round(xg * 100.0) / 100.0;
    }

}