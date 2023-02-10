package dms.controller;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
