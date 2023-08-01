package dms.controller;

import dms.service.db.DBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/db")
public class DBController {

    @Autowired
    DBService ss;

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/receive-pd-files", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> receivePDFiles() throws Exception {

        ss.receivePDFiles();

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body("New Schema Created");
    }

}
