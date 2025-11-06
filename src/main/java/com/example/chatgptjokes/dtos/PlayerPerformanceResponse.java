package com.example.chatgptjokes.dtos;

import java.util.List;

public class PlayerPerformanceResponse {
    private String type;
    private List<PlayerInfo> players;

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public List<PlayerInfo> getPlayers() { return players; }
    public void setPlayers(List<PlayerInfo> players) { this.players = players; }

    public static class PlayerInfo {
        private String name;
        private String team;
        private Double rating;
        private String position;
        private Integer minutes;
        private String aiAnalysis;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getTeam() { return team; }
        public void setTeam(String team) { this.team = team; }
        public Double getRating() { return rating; }
        public void setRating(Double rating) { this.rating = rating; }
        public String getPosition() { return position; }
        public void setPosition(String position) { this.position = position; }
        public Integer getMinutes() { return minutes; }
        public void setMinutes(Integer minutes) { this.minutes = minutes; }
        public String getAiAnalysis() { return aiAnalysis; }
        public void setAiAnalysis(String aiAnalysis) { this.aiAnalysis = aiAnalysis; }
    }
}
