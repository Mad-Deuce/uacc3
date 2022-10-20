package dms.standing.data.controller;

import dms.standing.data.entity.DRtuEntity;
import dms.standing.data.service.drtu.DRtuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rtu")
public class DRtuController {

    private final DRtuService dRtuService;

    @Autowired
    public DRtuController(DRtuService dRtuService) {
        this.dRtuService = dRtuService;
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/")
    public List<DRtuEntity> findAll() {
        return dRtuService.getAll();
    }
}
