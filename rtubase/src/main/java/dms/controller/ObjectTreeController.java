package dms.controller;


import dms.dto.ObjectTreeNodeDto;
import dms.service.structure.StructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/object-tree")
public class ObjectTreeController {

    private final StructureService structureService;

    @Autowired
    public ObjectTreeController(StructureService structureService) {
        this.structureService = structureService;
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/root")
    public ResponseEntity<?> getRoot() {
        List<ObjectTreeNodeDto> result = new ArrayList<>();

        result.add(structureService.getRootAlt());

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(result);
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/")
    public ResponseEntity<?> getChildren(ObjectTreeNodeDto structureDTO) {

        List<ObjectTreeNodeDto> result = structureService.getChildrenAlt(
                structureDTO.getId(),
                structureDTO.getClsId()
        );

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(result);
    }

}
