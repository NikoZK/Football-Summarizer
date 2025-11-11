package com.example.chatgptjokes.service;

import com.example.chatgptjokes.dtos.MyResponse;
import org.springframework.stereotype.Service;

@Service
public class AIService {

    private final OpenAiService openAiService;

    public AIService(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    public String generateMatchSummary(String homeTeam, String awayTeam, String score,
                                       Double possessionHome, Double possessionAway,
                                       Integer shotsOnGoalHome, Integer shotsOnGoalAway) {
        try {
            String prompt = String.format(
                    "Summarize this football match in 2-3 sentences: %s vs %s, Final Score: %s. " +
                            "Possession: %.1f%% vs %.1f%%, Shots on target: %d vs %d. Be concise and engaging.",
                    homeTeam, awayTeam, score,
                    possessionHome, possessionAway,
                    shotsOnGoalHome, shotsOnGoalAway
            );

            MyResponse aiResponse = openAiService.makeRequest(prompt, "You are a football commentator.");
            return aiResponse.getAnswer();
        } catch (Exception e) {
            System.err.println("⚠️  AI summary generation failed: " + e.getMessage());
            return String.format(
                    "%s faced %s in an exciting match that ended %s. " +
                            "Possession: %.1f%% vs %.1f%%, shots on target: %d vs %d.",
                    homeTeam, awayTeam, score,
                    possessionHome, possessionAway,
                    shotsOnGoalHome, shotsOnGoalAway
            );
        }
    }

    public String generateBasicMatchSummary(String homeTeam, String awayTeam, String score) {
        try {
            String prompt = String.format(
                    "Summarize this football match between %s and %s. The final score was %s. Be concise and neutral.",
                    homeTeam, awayTeam, score
            );

            MyResponse aiResponse = openAiService.makeRequest(prompt, "You are a football commentator.");
            return aiResponse.getAnswer();
        } catch (Exception e) {
            System.err.println("⚠️  AI summary generation failed: " + e.getMessage());
            return String.format(
                    "%s faced %s in a match that ended %s.",
                    homeTeam, awayTeam, score
            );
        }
    }

    public String generateMatchPreview(String homeTeam, String awayTeam, String stadium) {
        try {
            String prompt = String.format(
                    "Generate a 2-3 sentence prediction for the upcoming football match between %s (home) and %s (away). " +
                            "Consider recent form, head-to-head records, and playing styles. Be specific about who is favored and why.",
                    homeTeam, awayTeam
            );

            MyResponse aiResponse = openAiService.makeRequest(prompt, "You are a football analyst providing match predictions.");
            return aiResponse.getAnswer();
        } catch (Exception e) {
            System.err.println("⚠️  AI prediction generation failed: " + e.getMessage());
            return String.format(
                    "This promises to be an exciting match between %s and %s. " +
                            "The home team will look to leverage their advantage at %s, " +
                            "while the visitors will aim to take all three points.",
                    homeTeam, awayTeam, stadium
            );
        }
    }

    public String generatePlayerAnalysis(String playerName, String position, double rating, String type, int minutes,
                                         int goals, int assists, int shotsOnTarget, int saves,
                                         int foulsCommitted, int foulsSuffered, boolean cleanSheet) {
        try {
            String prompt = String.format(
                    "Explain in one concise sentence why %s (position: %s) was among the %s performers in this football match. " +
                            "They played %d minutes and achieved the following stats: Goals: %d, Assists: %d, Shots on target: %d, Saves: %d, " +
                            "Fouls committed: %d, Fouls suffered: %d, Clean sheet: %s. " +
                            "Do NOT invent any stats, only summarize what is given, and focus on contribution.",
                    playerName, position, type, minutes,
                    goals, assists, shotsOnTarget, saves,
                    foulsCommitted, foulsSuffered, cleanSheet ? "Yes" : "No"
            );
            MyResponse analysisResponse = openAiService.makeRequest(prompt, "You are a football analyst summarizing actual player performance.");
            return analysisResponse.getAnswer();
        } catch (Exception e) {
            String starterNote = minutes >= 60 ? "played significant minutes" : "had limited playing time";
            return String.format(
                    "%s (%s) %s and earned a rating of %.1f in this match.",
                    playerName, position, starterNote, rating
            );
        }
    }
}