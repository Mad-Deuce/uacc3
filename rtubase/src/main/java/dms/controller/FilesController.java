package dms.controller;


import dms.dto.FileDto;
import dms.dto.ResponseMessageDto;
import dms.service.files.FilesStorageService;
import org.apache.tomcat.jni.FileInfo;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "http://localhost:4200")
public class FilesController {

    private final FilesStorageService storageService;

    public FilesController(FilesStorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/upload")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<ResponseMessageDto> uploadFiles(@RequestParam("files") MultipartFile[] files) {
        String message = "";
        try {
            List<String> fileNames = new ArrayList<>();

            Arrays.asList(files).stream().forEach(file -> {
                storageService.save(file);
                fileNames.add(file.getOriginalFilename());
            });

            message = "Uploaded the files successfully: " + fileNames;
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseMessageDto(message));
        } catch (Exception e) {
            message = "Fail to upload files!";
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseMessageDto(message));
        }
    }

    @GetMapping("/")
    public ResponseEntity<List<FileDto>> getListFiles() {
        List<FileDto> fileInfos = storageService.loadAll().map(path -> {
            String filename = path.getFileName().toString();
            String url = MvcUriComponentsBuilder
                    .fromMethodName(FilesController.class, "getFile", path.getFileName().toString())
                    .build()
                    .toString();

            return new FileDto(filename, url);
        }).collect(Collectors.toList());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(fileInfos);
    }

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = storageService.load(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }
}
