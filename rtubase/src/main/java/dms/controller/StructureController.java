package dms.controller;


import dms.dto.StructureDTO;
import dms.service.structure.StructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/structure")
public class StructureController {

    private final StructureService structureService;

    @Autowired
    public StructureController(StructureService structureService) {
        this.structureService = structureService;
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/root")
    public ResponseEntity<?> getRoot() {
        List<StructureDTO> result = new ArrayList<>();

        result.add(structureService.getRoot());

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(result);
    }


    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/")
    public ResponseEntity<?> getChildren(StructureDTO structureDTO) {

        List<StructureDTO> result = structureService.getChildren(structureDTO.getId(), structureDTO.getRegionType());

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(result);
    }

}
