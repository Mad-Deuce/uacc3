package dms.controller;


import dms.dao.SchemaManager;
import dms.filter.Filter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    SchemaManager sm;

    private final ApplicationEventPublisher eventPublisher;

    public TestController(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/")
    public ResponseEntity<?> testMethod() {

        this.eventPublisher.publishEvent("----EVENT-----");

        return ResponseEntity.unprocessableEntity()
                .contentType(MediaType.APPLICATION_JSON)
                .body("info");
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.POST)
    @PostMapping(value = "/by-filter-spec", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findDevicesByFilterSpec(Pageable pageable, @RequestBody(required = false) List<Filter<Object>> filters) {

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(filters);
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/create-schema", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createSchema() {

        sm.createSchema("test_schema");

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body("New Schema Created");
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/rename-schema", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> renameSchema() {

        sm.renameSchema("test_schema", "new_test_schema");

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body("Schema Renamed");
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/remove-schema", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> removeSchema() {

        sm.removeSchema("drtu");
        sm.removeSchema("dock");

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body("Schema Removed");
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/restore-schema", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> restoreSchema() {

        sm.restoreEmpty();

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body("Schema Removed");
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/read-file", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> readFile() {

//        sm.readFileLineByLine();

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body("File Read");
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/unzip-file", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> unzipFile() throws Exception {

        String fileZip = "rtubase/src/main/resources/pd_files/d1110714.001";
        String destDir = "rtubase/src/main/resources/pd_files/tttw";

        File fz = new File(fileZip);
        log.info("exists: " + String.valueOf(fz.exists()));
        log.info("is dir: " + String.valueOf(fz.isDirectory()));
        log.info("is file: " + String.valueOf(fz.isFile()));
        log.info("can read: " + String.valueOf(fz.canRead()));

        File dd = new File(destDir);
        log.info("exists: " + String.valueOf(dd.exists()));
        log.info("is dir: " + String.valueOf(dd.isDirectory()));
        log.info("is file: " + String.valueOf(dd.isFile()));
        log.info("can read: " + String.valueOf(dd.canWrite()));

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body("File UnRAR");
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/parse-d-file", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> parseDFile() throws Exception {

        sm.receiveDFile();

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body("res");
    }

}
