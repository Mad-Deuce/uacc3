package dms.controller;


import dms.dto.stats.OverdueDevicesStats;
import dms.dto.stats.StatsDTO;
import dms.service.stats.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



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

}