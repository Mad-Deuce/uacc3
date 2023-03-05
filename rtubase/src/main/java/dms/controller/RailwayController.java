package dms.controller;


import dms.standing.data.dto.RailwayDTO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/railways")
@Validated
public class RailwayController {


    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/")
    public ResponseEntity<?> getAllRailways() {

        List<RailwayDTO> result = new ArrayList<>();

        for (int i = 1; i < 7; i++) {
            RailwayDTO railwayDTO = new RailwayDTO();
            railwayDTO.setId(Integer.toString(i));
            railwayDTO.setName("railway_" + i);
            railwayDTO.setCode("r_" + i);
            result.add(railwayDTO);
        }

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(result);
    }

}