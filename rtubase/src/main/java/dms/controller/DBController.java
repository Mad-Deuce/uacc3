package dms.controller;

import dms.service.schema.SchemaService;
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
    SchemaService schemaService;

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/check-pd-dir", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> isPDDirEmpty() throws Exception {

//        boolean result = schemaService.isPDDirEmpty();

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(true);
    }

//    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
//    @GetMapping(value = "/receive-pd-files", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> receivePDFiles() throws Exception {
//
//        schemaService.receivePDFiles();
//
//        return ResponseEntity
//                .ok()
////                .contentType(MediaType.TEXT_PLAIN)
////                .body("Files Received")
//                .build()
//                ;
//    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/schema/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getDatesOfExistingSchemas() {

        List<LocalDate> result = schemaService.getDatesOfExistingSchemas();

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(result);
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/schema", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getActiveSchemaDate() {

        LocalDate result = schemaService.getDateOfActiveSchema();

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(result);
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.POST)
    @PostMapping(value = "/schema", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> setActiveSchemaDate(@RequestBody HashMap<String, LocalDate> options) {

        LocalDate result = schemaService.setActiveSchemaDate(options.get("schemaDate"));

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(result);
    }


}
