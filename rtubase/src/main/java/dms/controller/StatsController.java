package dms.controller;


import dms.dto.stats.ExpiredDevicesStatsDto;
import dms.dto.stats.OverdueDevicesStats;
import dms.dto.stats.StatsDTO;
import dms.entity.OverdueDevsStatsEntity;
import dms.report_generator.xls.OverdueDevicesStatsHistoryReportBuilder;
import dms.service.stats.StatsService;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final StatsService statsService;
    private final OverdueDevicesStatsHistoryReportBuilder reportBuilder;

    @Autowired
    public StatsController(StatsService statsService,
                           OverdueDevicesStatsHistoryReportBuilder reportBuilder) {
        this.statsService = statsService;
        this.reportBuilder = reportBuilder;
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/collect")
    public ResponseEntity<?> collectSchemaStats() {

        statsService.saveAllSchemaOverdueDevsStats();

        return ResponseEntity
                .ok()
                .build();
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/expired-devices/export/xls")
    public ResponseEntity<?> exportExpiredDevicesStats(@RequestParam String nodeId) throws IOException {

        List<OverdueDevsStatsEntity> values = statsService.getOverdueDevicesStatsEntityList(nodeId);

        Workbook workbook = reportBuilder.getOverdueDevicesStatsHistoryReport(values);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        byte[] barray = bos.toByteArray();
        workbook.close();
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(barray));

        return ResponseEntity
                .ok()
                .header("Content-Disposition", "attachment; filename=rep_devices.xlsx")
                .contentLength(barray.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);

    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/expired-devices")
    public ResponseEntity<?> getExpiredDevicesStats(@RequestParam String nodeId) {

        List<OverdueDevsStatsEntity> entityList = statsService.getOverdueDevicesStatsEntityList(nodeId);
        Map<String, ExpiredDevicesStatsDto> dtoMap = ExpiredDevicesStatsDto.toDtoList(entityList);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(dtoMap.values());
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/expired-devices/{schemaDate}")
    public ResponseEntity<?> getExpiredDevicesStats(@RequestParam String nodeId, @PathVariable("schemaDate") String schemaDateStr) {
        LocalDate schemaDate = LocalDate.parse(schemaDateStr);
        List<OverdueDevsStatsEntity> entityList = statsService.getOverdueDevicesStatsEntityList(nodeId, schemaDate);
        Map<String, ExpiredDevicesStatsDto> dtoMap = ExpiredDevicesStatsDto.toDtoList(entityList);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(dtoMap.values());
    }

}