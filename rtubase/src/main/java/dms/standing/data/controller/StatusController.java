package dms.standing.data.controller;


import dms.standing.data.dock.val.Status;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/standing-data/statuses")
public class StatusController {

    public StatusController() {}

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/")
    public ResponseEntity<?> getAllStatuses() {
        Status[] statuses = Status.values();
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(statuses);
    }

}