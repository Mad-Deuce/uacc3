package dms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dms.dto.DeviceValidationDTO;
//import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@RestController
@RequestMapping("/api/test")
public class TestController {
    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping(value = "/")
    public void testMethod(HttpServletResponse response) throws IOException {





        response.setContentType("application/json");

        response.setStatus(422);
//        response.getWriter().write("{\n" +
//                "  errors: {\n" +
//                "    \"username\": \"The username is already taken\"\n" +
//                "  }\n" +
//                "}");
//        return response1;


    }
}
