package com.example.chatgptjokes.service;

import com.example.chatgptjokes.dtos.MatchSummaryResponse;
import com.example.chatgptjokes.dtos.PlayerPerformanceResponse;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FootballService {

    private final MatchService matchService;
    private final PlayerService playerService;
    public FootballService(MatchService matchService, PlayerService playerService) {
        this.matchService = matchService;
        this.playerService = playerService;
    }

    public Map<String, Object> getMatches(String date) {
        return matchService.getMatches(date);
    }

    public MatchSummaryResponse getMatchSummary(String fixtureId) {
        return matchService.getMatchSummary(fixtureId);
    }

    public PlayerPerformanceResponse getPlayerPerformance(String fixtureId, String type) {
        return playerService.getPlayerPerformance(fixtureId, type);
    }
}