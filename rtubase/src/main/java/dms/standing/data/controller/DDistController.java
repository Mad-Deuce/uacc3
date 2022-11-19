package dms.standing.data.controller;

import dms.standing.data.entity.SubdivisionEntity;
import dms.standing.data.service.ddist.DDistService;
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
    public List<SubdivisionEntity> findAll() {
        return dDistService.getAll();
    }
}
