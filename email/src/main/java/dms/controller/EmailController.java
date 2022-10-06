package dms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/email/")
public class EmailController {

//    @Autowired
    private final JavaMailSender emailSender;

    EmailController(@Autowired JavaMailSender emailSender){
        this.emailSender=emailSender;
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping("/send/")
    public void send() {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@baeldung.com");
        message.setTo("serhij.zolotukhin@gmail.com");
        message.setSubject("cccccdds");
        message.setText("gfghtest text");
        emailSender.send(message);
    }

}
