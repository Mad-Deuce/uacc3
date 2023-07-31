package dms.controller;

import dms.dao.ReceiveManager;
import dms.dao.SchemaManager;
import dms.service.schema.SchemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/db")
public class SchemaController {

    @Autowired
    SchemaService ss;

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createDefaultSchema() throws Exception {

        ss.updateDBByPDFilesAlt();

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body("New Schema Created");
    }

}
