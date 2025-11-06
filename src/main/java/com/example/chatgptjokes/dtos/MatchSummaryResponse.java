package com.example.chatgptjokes.dtos;

public class MatchSummaryResponse {
    private String fixtureId;
    private String homeTeam;
    private String awayTeam;
    private String score;
    private String aiSummary;
    private MatchStatistics statistics;
    private boolean isUpcoming;
    private MatchPreview preview;

    public String getFixtureId() { return fixtureId; }
    public void setFixtureId(String fixtureId) { this.fixtureId = fixtureId; }
    public String getHomeTeam() { return homeTeam; }
    public void setHomeTeam(String homeTeam) { this.homeTeam = homeTeam; }
    public String getAwayTeam() { return awayTeam; }
    public void setAwayTeam(String awayTeam) { this.awayTeam = awayTeam; }
    public String getScore() { return score; }
    public void setScore(String score) { this.score = score; }
    public String getAiSummary() { return aiSummary; }
    public void setAiSummary(String aiSummary) { this.aiSummary = aiSummary; }
    public MatchStatistics getStatistics() { return statistics; }
    public void setStatistics(MatchStatistics statistics) { this.statistics = statistics; }
    public boolean isUpcoming() { return isUpcoming; }
    public void setUpcoming(boolean upcoming) { isUpcoming = upcoming; }
    public MatchPreview getPreview() { return preview; }
    public void setPreview(MatchPreview preview) { this.preview = preview; }

    public static class MatchStatistics {
        private Integer possessionHome;
        private Integer possessionAway;
        private Double expectedGoalsHome;
        private Double expectedGoalsAway;
        private Integer shotsOnGoalHome;
        private Integer shotsOnGoalAway;
        private Integer bigChancesHome;
        private Integer bigChancesAway;
        private Integer yellowCardsHome;
        private Integer yellowCardsAway;
        private Integer redCardsHome;
        private Integer redCardsAway;

        public Integer getPossessionHome() { return possessionHome; }
        public void setPossessionHome(Integer possessionHome) { this.possessionHome = possessionHome; }
        public Integer getPossessionAway() { return possessionAway; }
        public void setPossessionAway(Integer possessionAway) { this.possessionAway = possessionAway; }
        public Double getExpectedGoalsHome() { return expectedGoalsHome; }
        public void setExpectedGoalsHome(Double expectedGoalsHome) { this.expectedGoalsHome = expectedGoalsHome; }
        public Double getExpectedGoalsAway() { return expectedGoalsAway; }
        public void setExpectedGoalsAway(Double expectedGoalsAway) { this.expectedGoalsAway = expectedGoalsAway; }
        public Integer getShotsOnGoalHome() { return shotsOnGoalHome; }
        public void setShotsOnGoalHome(Integer shotsOnGoalHome) { this.shotsOnGoalHome = shotsOnGoalHome; }
        public Integer getShotsOnGoalAway() { return shotsOnGoalAway; }
        public void setShotsOnGoalAway(Integer shotsOnGoalAway) { this.shotsOnGoalAway = shotsOnGoalAway; }
        public Integer getBigChancesHome() { return bigChancesHome; }
        public void setBigChancesHome(Integer bigChancesHome) { this.bigChancesHome = bigChancesHome; }
        public Integer getBigChancesAway() { return bigChancesAway; }
        public void setBigChancesAway(Integer bigChancesAway) { this.bigChancesAway = bigChancesAway; }
        public Integer getYellowCardsHome() { return yellowCardsHome; }
        public void setYellowCardsHome(Integer yellowCardsHome) { this.yellowCardsHome = yellowCardsHome; }
        public Integer getYellowCardsAway() { return yellowCardsAway; }
        public void setYellowCardsAway(Integer yellowCardsAway) { this.yellowCardsAway = yellowCardsAway; }
        public Integer getRedCardsHome() { return redCardsHome; }
        public void setRedCardsHome(Integer redCardsHome) { this.redCardsHome = redCardsHome; }
        public Integer getRedCardsAway() { return redCardsAway; }
        public void setRedCardsAway(Integer redCardsAway) { this.redCardsAway = redCardsAway; }
    }

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

        public String getStadium() { return stadium; }
        public void setStadium(String stadium) { this.stadium = stadium; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public String getTime() { return time; }
        public void setTime(String time) { this.time = time; }
        public String[] getStreamingServices() { return streamingServices; }
        public void setStreamingServices(String[] streamingServices) { this.streamingServices = streamingServices; }
        public String getPrediction() { return prediction; }
        public void setPrediction(String prediction) { this.prediction = prediction; }
        public String getReferee() { return referee; }
        public void setReferee(String referee) { this.referee = referee; }
        public Integer getAttendance() { return attendance; }
        public void setAttendance(Integer attendance) { this.attendance = attendance; }
        public String getWeather() { return weather; }
        public void setWeather(String weather) { this.weather = weather; }
        public TeamForm getHomeForm() { return homeForm; }
        public void setHomeForm(TeamForm homeForm) { this.homeForm = homeForm; }
        public TeamForm getAwayForm() { return awayForm; }
        public void setAwayForm(TeamForm awayForm) { this.awayForm = awayForm; }
    }

    public static class TeamForm {
        private String lastFiveGames;
        private Integer position;
        private Integer points;
        private Integer wins;
        private Integer draws;
        private Integer losses;

        public String getLastFiveGames() { return lastFiveGames; }
        public void setLastFiveGames(String lastFiveGames) { this.lastFiveGames = lastFiveGames; }
        public Integer getPosition() { return position; }
        public void setPosition(Integer position) { this.position = position; }
        public Integer getPoints() { return points; }
        public void setPoints(Integer points) { this.points = points; }
        public Integer getWins() { return wins; }
        public void setWins(Integer wins) { this.wins = wins; }
        public Integer getDraws() { return draws; }
        public void setDraws(Integer draws) { this.draws = draws; }
        public Integer getLosses() { return losses; }
        public void setLosses(Integer losses) { this.losses = losses; }
    }
}
