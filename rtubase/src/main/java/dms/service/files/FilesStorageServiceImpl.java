package dms.service.files;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@Slf4j
public class FilesStorageServiceImpl implements FilesStorageService {

    @Value("${dms.upload.path}")
    private String UPLOAD_DIR_PATH_PARTS;

    private Path root;

    @Override
    public void init() {
        try {
            root = getUploadDirPath();
            if (!root.toFile().exists()) {
                Files.createDirectory(root);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    private Path getUploadDirPath() {
        Path upload_dir_path = null;
        List<String> pathParts = Arrays.stream(UPLOAD_DIR_PATH_PARTS.split(",")).toList();
        for (String part : pathParts) {
            if (upload_dir_path == null) {
                upload_dir_path = Paths.get(part);
            } else {
                upload_dir_path = Paths.get(upload_dir_path.toAbsolutePath().toString(), part);
            }
        }

        return upload_dir_path;
    }

    @Override
    public void save(MultipartFile file) {
        try {
            Files.copy(file.getInputStream(), this.root.resolve(Objects.requireNonNull(file.getOriginalFilename())));
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }

    @Override
    public Resource load(String filename) {
        try {
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(root.toFile());
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.root, 1).filter(path -> !path.equals(this.root)).map(this.root::relativize);
        } catch (IOException e) {
            throw new RuntimeException("Could not load the files!");
        }
    }

    @Override
    public File getUploadDir() throws Exception {
        String errorMessage = "dms: Directory for P,D Files (upload directory) not accessible";
        File upload_dir = null;
        List<String> pathParts = Arrays.stream(UPLOAD_DIR_PATH_PARTS.split(",")).toList();
        for (String part : pathParts) {
            if (upload_dir == null) {
                upload_dir = new File(part);
            } else {
                upload_dir = new File(upload_dir, part);
            }
            if (!upload_dir.mkdir()) {
                throw new Exception(errorMessage);
            }
        }

        if (upload_dir == null ) {
            throw new Exception(errorMessage);
        }

        if (!upload_dir.isDirectory() || !upload_dir.canRead()) {
            log.info(errorMessage);
            log.info("Try to create Dir: " + upload_dir.getAbsolutePath());
            if (!upload_dir.mkdir()) {
                throw new Exception(errorMessage);
            }
        }

        return upload_dir;
    }

}
