package dms.controller;


import dms.dto.stats.OverdueDevicesStats;
import dms.dto.stats.StatsDTO;
import dms.service.stats.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;


@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final StatsService statsService;

    @Autowired
    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }


    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/")
    public ResponseEntity<?> getStats() {
        StatsDTO statsDTO = new StatsDTO();

        OverdueDevicesStats overdueDevicesStats = statsService.getOverdueDevicesStats();

        statsDTO.setOverdueDevicesStats(overdueDevicesStats);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(statsDTO);
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/map")
    public ResponseEntity<?> getStatsMap() throws SQLException {
        StatsDTO statsDTO = new StatsDTO();

//        OverdueDevicesStats overdueDevicesStats = statsService.getOverdueDevicesStats();

        HashMap<LocalDate, OverdueDevicesStats> overdueDevicesStatsMap = statsService.getOverdueDevicesStatsMap();

//        statsDTO.setOverdueDevicesStats(overdueDevicesStats);
        statsDTO.setOverdueDevicesStatsMap(overdueDevicesStatsMap);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(statsDTO);
    }

}