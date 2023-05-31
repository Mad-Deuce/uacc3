package dms.standing.data.controller;


import dms.standing.data.dock.val.LocateType;
import dms.standing.data.dock.val.RegionType;
import dms.standing.data.dock.val.Status;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/standing-data/val")
public class ValController {

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/statuses")
    public ResponseEntity<?> getAllStatuses() {
        Status[] statuses = Status.values();
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(statuses);
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/region-types")
    public ResponseEntity<?> getAllRegionTypes() {
        RegionType[] statuses = RegionType.values();
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(statuses);
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/locate-types")
    public ResponseEntity<?> getAllLocateTypes() {
        LocateType[] statuses = LocateType.values();
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(statuses);
    }

}