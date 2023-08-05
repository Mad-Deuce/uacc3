package dms.controller;

import dms.service.db.DBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
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
    @GetMapping(value = "/schema/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getDatesOfExistingSchemas() {

        List<LocalDate> result = dbService.getDatesOfExistingSchemas();

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(result);
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/schema", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getActiveSchemaDate() {

        LocalDate result = dbService.getDateOfActiveSchema() ;

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(result);
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.POST)
    @PostMapping(value = "/schema", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> setActiveSchemaDate(@RequestBody HashMap<String, LocalDate> options) {

//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate result = dbService.setActiveSchemaDate(options.get("schemaDate"));

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(result);
    }


}
