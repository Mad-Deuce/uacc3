package dms.standing.data.controller;

import dms.standing.data.entity.RtdFacilityEntity;
import dms.standing.data.service.facility.RtdFacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rtu")
public class DRtuController {

    private final RtdFacilityService rtdFacilityService;

    @Autowired
    public DRtuController(RtdFacilityService rtdFacilityService) {
        this.rtdFacilityService = rtdFacilityService;
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/")
    public List<RtdFacilityEntity> findAll() {
        return rtdFacilityService.getAll();
    }
}
