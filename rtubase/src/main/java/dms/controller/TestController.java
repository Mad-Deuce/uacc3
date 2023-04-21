package dms.controller;


import dms.filter.FilterAbs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/test")
public class TestController {

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
    public ResponseEntity<?> findDevicesByFilterSpec(Pageable pageable, @RequestBody(required = false) List<FilterAbs<Object>> filters) {

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(filters);
    }
}
