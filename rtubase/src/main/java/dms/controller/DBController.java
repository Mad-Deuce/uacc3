package dms.controller;

import dms.service.db.DBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/db")
public class DBController {

    @Autowired
    DBService dbService;

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/receive-pd-files", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> receivePDFiles() throws Exception {

        dbService.receivePDFiles();

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body("Files Received");
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/schema/date", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getDatesOfExistingSchemas() {

        List<LocalDate> result = dbService.getDatesOfExistingSchemas();

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(result);
    }

}
