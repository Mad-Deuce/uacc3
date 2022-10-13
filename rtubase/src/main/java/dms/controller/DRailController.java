package dms.controller;

import dms.entity.standing.data.DRailEntity;
import dms.service.drail.DRailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rails")
public class DRailController {

    private final DRailService dRailService;

    @Autowired
    public DRailController(DRailService dRailService) {
        this.dRailService = dRailService;
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/")
    public List<DRailEntity> findAll() {
        return dRailService.getAll();
    }
}
