package com.example.footballsummarizer.restpointcontroller;

import com.example.footballsummarizer.dtos.MatchSummaryResponse;
import com.example.footballsummarizer.dtos.PlayerPerformanceResponse;
import com.example.footballsummarizer.service.FootballService;
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
    public Map<String, Object> getMatches(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String league) {
        return footballService.getMatches(date, league);
    }

    @GetMapping("/match/{fixtureId}/summary")
    public MatchSummaryResponse getMatchSummary(
            @PathVariable String fixtureId,
            @RequestParam(required = false) String league) {
        return footballService.getMatchSummary(fixtureId, league);
    }

    @GetMapping("/match/{fixtureId}/players")
    public PlayerPerformanceResponse getPlayerPerformance(
            @PathVariable String fixtureId,
            @RequestParam String type,
            @RequestParam(required = false) String league) {
        return footballService.getPlayerPerformance(fixtureId, type, league);
    }
}
