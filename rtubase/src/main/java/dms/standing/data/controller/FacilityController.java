package dms.standing.data.controller;

import dms.standing.data.entity.LineFacilityEntity;
import dms.standing.data.service.facility.LineFacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/standing-data/facilities")
public class FacilityController {
    private final LineFacilityService lineFacilityService;

    @Autowired
    public FacilityController(LineFacilityService lineFacilityService) {
        this.lineFacilityService = lineFacilityService;
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/")
    public List<LineFacilityEntity> findAll() {
        return lineFacilityService.getAll();
    }
}
