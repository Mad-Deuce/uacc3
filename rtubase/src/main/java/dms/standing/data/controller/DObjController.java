package dms.standing.data.controller;

import dms.standing.data.entity.LineFacilityEntity;
import dms.standing.data.service.facility.LineFacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/obj")
public class DObjController {
    private final LineFacilityService lineFacilityService;

    @Autowired
    public DObjController(LineFacilityService lineFacilityService) {
        this.lineFacilityService = lineFacilityService;
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/")
    public List<LineFacilityEntity> findAll() {
        return lineFacilityService.getAll();
    }
}
