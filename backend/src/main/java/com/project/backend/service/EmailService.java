package com.project.backend.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendMail(String to,Long orderId){
        try{
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);

            helper.setTo(to);
            helper.setSubject("🧾 Regtrading your  Order #" + orderId);
            helper.setText("Dear Customer ,\n\n Thank you for ordering from our site..");


            mailSender.send(msg);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
