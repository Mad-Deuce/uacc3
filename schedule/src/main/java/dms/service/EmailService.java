package dms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;


@Service
public class EmailService {

    JavaMailSender emailSender;

    EmailService(@Autowired JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

//    @Async
    @PostConstruct
    @Scheduled(initialDelay = 3000, fixedDelay = 10000000)
    public void send() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("zolotukhinpv@gmail.com");
        message.setTo("serhij.zolotukhin@gmail.com");
        message.setSubject("Scheduled message");
        message.setText("Service");
        emailSender.send(message);
        System.out.println("sending message--------");
    }

}
