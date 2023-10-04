package dms.service.files;

import lombok.extern.slf4j.Slf4j;
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
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@Slf4j
public class FilesStorageServiceImpl implements FilesStorageService {

    private final Path uploadsPath = Paths.get("uploads");

    private Path getUploadPath() {
        try {
            if (!this.uploadsPath.toFile().exists()) {
                Files.createDirectory(uploadsPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }

        return uploadsPath;
    }

    @Override
    public void save(MultipartFile file) {
        Path path = getUploadPath();
        try {
            Files.copy(file.getInputStream(), path.resolve(Objects.requireNonNull(file.getOriginalFilename())),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }

    @Override
    public Resource load(String filename) {
        try {
            Path path = getUploadPath().resolve(filename);
            Resource resource = new UrlResource(path.toUri());

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
        Path path = getUploadPath();
        FileSystemUtils.deleteRecursively(path.toFile());
    }

    @Override
    public Stream<Path> loadAll() {
        Path path = getUploadPath();
        try {
            return Files.walk(path, 1).filter(pathItem -> !pathItem.equals(path)).map(path::relativize);
        } catch (IOException e) {
            throw new RuntimeException("Could not load the files!");
        }
    }

    @Override
    public File getUploadDir()  {
        Path path = getUploadPath();
        return path.toFile();
    }

}
