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
                                       Integer possessionHome, Integer possessionAway,
                                       Integer shotsOnGoalHome, Integer shotsOnGoalAway) {
        try {
            String prompt = String.format(
                    "Summarize this football match in 2-3 sentences: %s vs %s, Final Score: %s. " +
                            "Possession: %d%% vs %d%%, Shots on target: %d vs %d. Be concise and engaging.",
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
                            "Possession: %d%% vs %d%%, shots on target: %d vs %d.",
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

    public String generatePlayerAnalysis(String playerName, String position, double rating, String type, int minutes) {
        try {
            String prompt = String.format(
                    "In one sentence, explain why %s (position: %s) with an estimated rating of %.1f was among the %s performers in this football match.",
                    playerName, position, rating, type
            );
            MyResponse analysisResponse = openAiService.makeRequest(prompt, "You are a football analyst.");
            return analysisResponse.getAnswer();
        } catch (Exception e) {
            String starterNote = minutes >= 60 ? "played significant minutes" : "had limited playing time";
            return String.format(
                    "%s (%s) %s and earned an estimated rating of %.1f in this match.",
                    playerName, position, starterNote, rating
            );
        }
    }
}