package dms.standing.data.controller;

import dms.dto.DeviceDTO;
import dms.standing.data.entity.RailwayEntity;
import dms.standing.data.service.railway.RailwayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/standing-data/railways")
public class RailwayController {

    private final RailwayService railwayService;

    @Autowired
    public RailwayController(RailwayService railwayService) {
        this.railwayService = railwayService;
    }

//    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
//    @GetMapping(value = "/")
//    public List<RailwayEntity> findAll() {
//        return railwayService.getAll();
//    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/")
    public ResponseEntity<?> findAllRailways() {
        List<RailwayEntity> railways = railwayService.getAll();
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(railways);
    }


    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.POST)
    @PostMapping(value = "/import")
    public void importFromExcel (@RequestParam("file") MultipartFile files) throws IOException {
        railwayService.importFromExcel(files);
    }


}
