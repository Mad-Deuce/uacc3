package dms.controller;

import dms.entity.standing.data.DDistEntity;
import dms.service.ddist.DDistService;
import dms.service.drtu.DRtuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dist")
public class DDistController {

    private final DDistService dDistService;

    @Autowired
    public DDistController(DDistService dDistService) {
        this.dDistService = dDistService;
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/")
    public List<DDistEntity> findAll() {
        return dDistService.getAll();
    }
}