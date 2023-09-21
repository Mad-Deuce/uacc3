package dms;

import dms.service.files.FilesStorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.Resource;

@SpringBootApplication
@EnableScheduling
public class Application implements CommandLineRunner {

    @Resource
    FilesStorageService storageService;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... arg) throws Exception {
        storageService.deleteAll();
        storageService.init();
    }

}
