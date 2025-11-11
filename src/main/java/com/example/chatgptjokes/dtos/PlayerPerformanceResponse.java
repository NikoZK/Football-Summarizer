package com.example.chatgptjokes.dtos;

import lombok.Data;
import java.util.List;

@Data
public class PlayerPerformanceResponse {
    private String type;
    private List<PlayerInfo> players;

    @Data
    public static class PlayerInfo {
        private String name;
        private String team;
        private Double rating;
        private String position;
        private Integer minutes;
        private String aiAnalysis;

        // New attributes for detailed stats
        private Integer goals;
        private Integer assists;
        private Integer saves;
        private Integer shotsOnTarget;
        private Integer foulsCommitted;
        private Integer foulsSuffered;
        private Boolean cleanSheet;
    }
}
