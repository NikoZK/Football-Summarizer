package com.example.chatgptjokes.api;

import com.example.chatgptjokes.dtos.MatchSummaryResponse;
import com.example.chatgptjokes.dtos.PlayerPerformanceResponse;
import com.example.chatgptjokes.service.FootballService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/football")
@CrossOrigin(origins = "*")
public class FootballController {

    private final FootballService footballService;

    public FootballController(FootballService footballService) {
        this.footballService = footballService;
    }

    @GetMapping("/matches")
    public Map<String, Object> getMatches(@RequestParam(required = false) String date) {
        return footballService.getMatches(date);
    }

    @GetMapping("/match/{fixtureId}/summary")
    public MatchSummaryResponse getMatchSummary(@PathVariable String fixtureId) {
        return footballService.getMatchSummary(fixtureId);
    }

    @GetMapping("/match/{fixtureId}/players")
    public PlayerPerformanceResponse getPlayerPerformance(
            @PathVariable String fixtureId,
            @RequestParam String type) {
        return footballService.getPlayerPerformance(fixtureId, type);
    }
}
