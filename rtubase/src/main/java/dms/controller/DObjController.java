package dms.controller;

import dms.entity.standing.data.DObjEntity;
import dms.service.dobj.DObjService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/obj")
public class DObjController {
    private final DObjService dObjService;

    @Autowired
    public DObjController(DObjService dObjService) {
        this.dObjService = dObjService;
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/")
    public List<DObjEntity> findAll() {
        return dObjService.getAll();
    }
}
