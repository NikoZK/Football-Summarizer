package com.example.footballsummarizer.dtos;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchSummaryResponse {

    private String fixtureId;
    private String homeTeam;
    private String awayTeam;
    private String score;
    private String aiSummary;

    private MatchStatistics statistics;
    private boolean isUpcoming;
    private MatchPreview preview;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MatchStatistics {
        private Double possessionHome;
        private Double possessionAway;
        private Double expectedGoalsHome;
        private Double expectedGoalsAway;
        private Double totalShotsHome;
        private Double totalShotsAway;
        private Integer shotsOnGoalHome;
        private Integer shotsOnGoalAway;
        private Integer bigChancesHome;
        private Integer bigChancesAway;
        private Integer savesHome;
        private Integer savesAway;
        private Integer totalTacklesHome;
        private Integer totalTacklesAway;
        private Integer effectiveTacklesHome;
        private Integer effectiveTacklesAway;
        private Integer interceptionsHome;
        private Integer interceptionsAway;
        private Integer yellowCardsHome;
        private Integer yellowCardsAway;
        private Integer redCardsHome;
        private Integer redCardsAway;
        private Integer cornersHome;
        private Integer cornersAway;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MatchPreview {
        private String stadium;
        private String city;
        private String date;
        private String time;
        private String[] streamingServices;
        private String prediction;
        private String referee;
        private Integer attendance;
        private String weather;
        private TeamForm homeForm;
        private TeamForm awayForm;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamForm {
        private String lastFiveGames;
        private Integer position;
        private Integer points;
        private Integer wins;
        private Integer draws;
        private Integer losses;
    }
}