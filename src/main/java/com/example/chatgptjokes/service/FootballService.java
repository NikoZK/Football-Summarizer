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

    public Map<String, Object> getMatches(String date, String league) {
        return matchService.getMatches(date, league);
    }


    public MatchSummaryResponse getMatchSummary(String fixtureId, String league) {
        return matchService.getMatchSummary(fixtureId, league);
    }

    public PlayerPerformanceResponse getPlayerPerformance(String fixtureId, String type, String league) {
        return playerService.getPlayerPerformance(fixtureId, type, league);
    }

}