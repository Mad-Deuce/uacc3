package dms.standing.data.controller;

import dms.standing.data.entity.SubdivisionEntity;
import dms.standing.data.service.subdivision.SubdivisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/standing-data/subdivisions")
public class SubdivisionController {

    private final SubdivisionService subdivisionService;

    @Autowired
    public SubdivisionController(SubdivisionService subdivisionService) {
        this.subdivisionService = subdivisionService;
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/")
    public List<SubdivisionEntity> findAll() {
        return subdivisionService.getAll();
    }
}
