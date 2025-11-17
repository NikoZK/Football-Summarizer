package com.example.footballsummarizer.service;

import com.example.footballsummarizer.dtos.MatchSummaryResponse;
import com.example.footballsummarizer.dtos.MyResponse;
import org.springframework.stereotype.Service;

@Service
public class AIService {

    private final OpenAiService openAiService;

    public AIService(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    public String generateMatchSummary(String homeTeam, String awayTeam, String score,
                                       Double possessionHome, Double possessionAway,
                                       Integer shotsOnGoalHome, Integer shotsOnGoalAway,
                                       Double xGHome, Double xGAway,
                                       Integer tacklesHome, Integer tacklesAway,
                                       Integer interceptionsHome, Integer interceptionsAway,
                                       Integer savesHome, Integer savesAway) {
        try {
            String prompt = String.format(
                    "You are a football analyst. Summarize the match %s vs %s (Final Score: %s) in 3-4 sentences. " +
                            "Include insights such as: which team controlled possession, attacking vs defensive play, " +
                            "efficiency in front of goal, defensive efforts, key momentum shifts, and any notable tactical patterns. " +
                            "Stats: Possession %.1f%% vs %.1f%%, Shots on target: %d vs %d, Expected Goals (xG): %.2f vs %.2f, " +
                            "Tackles: %d vs %d, Interceptions: %d vs %d, Saves: %d vs %d. " +
                            "Make it engaging for readers, but do NOT invent players or events, only interpret the given stats.",
                    homeTeam, awayTeam, score,
                    possessionHome, possessionAway,
                    shotsOnGoalHome, shotsOnGoalAway,
                    xGHome, xGAway,
                    tacklesHome, tacklesAway,
                    interceptionsHome, interceptionsAway,
                    savesHome, savesAway
            );

            MyResponse aiResponse = openAiService.makeRequest(prompt, "You are a football analyst providing tactical match insights.");
            return aiResponse.getAnswer();

        } catch (Exception e) {
            System.err.println("AI summary generation failed: " + e.getMessage());

            return String.format(
                    "%s faced %s in a match that ended %s. Possession: %.1f%% vs %.1f%%, shots on target: %d vs %d, xG: %.2f vs %.2f. " +
                            "Defensive efforts: Tackles %d vs %d, Interceptions %d vs %d, Saves %d vs %d. " +
                            "The team with higher possession had more control, but defensive work kept the match balanced.",
                    homeTeam, awayTeam, score,
                    possessionHome, possessionAway,
                    shotsOnGoalHome, shotsOnGoalAway,
                    xGHome, xGAway,
                    tacklesHome, tacklesAway,
                    interceptionsHome, interceptionsAway,
                    savesHome, savesAway
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
            System.err.println("AI summary generation failed: " + e.getMessage());
            return String.format(
                    "%s faced %s in a match that ended %s.",
                    homeTeam, awayTeam, score
            );
        }
    }

    public String generateMatchPreview(String homeTeam, String awayTeam, String stadium,
                                       MatchSummaryResponse.TeamForm homeForm,
                                       MatchSummaryResponse.TeamForm awayForm) {
        try {
            String prompt = String.format(
                    "You are a football analyst. Write a concise 2-3 sentence preview for the upcoming match between %s (home) and %s (away) at %s. " +
                            "Include league form and current table position for context. " +
                            "%s form: %dW-%dD-%dL, %d points, position %d.\n" +
                            "%s form: %dW-%dD-%dL, %d points, position %d.\n" +
                            "Analyze their momentum, consistency, and any trends, but DO NOT invent player news or fake stats. " +
                            "End with a realistic insight about who might have the upper hand.",
                    homeTeam, awayTeam, stadium,
                    homeTeam, homeForm.getWins(), homeForm.getDraws(), homeForm.getLosses(),
                    homeForm.getPoints(), homeForm.getPosition(),
                    awayTeam, awayForm.getWins(), awayForm.getDraws(), awayForm.getLosses(),
                    awayForm.getPoints(), awayForm.getPosition()
            );

            MyResponse aiResponse = openAiService.makeRequest(
                    prompt,
                    "You are a football analyst providing tactical pre-match insights."
            );
            return aiResponse.getAnswer();

        } catch (Exception e) {
            System.err.println("AI prediction generation failed: " + e.getMessage());
            return String.format(
                    "This promises to be an exciting match between %s and %s at %s. " +
                            "Both sides will aim to capitalize on recent form.",
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