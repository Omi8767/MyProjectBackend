package com.project.backend.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
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

    @Async  //  no delay
    public void sendInvoiceEmail(String to, byte[] pdf, Long orderId) {

        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);

            helper.setTo(to);
            helper.setSubject("🧾 Invoice - Order #" + orderId);

            String html = """
                <div style="font-family:Arial;padding:20px">
                    
                    <h2 style="color:#28a745">Order Placed </h2>

                    <p>Thank you for your purchase.</p>

                    <div style="background:#f1f1f1;padding:15px;border-radius:8px">
                        <b>Order ID:</b> %d
                    </div>

                    <br>

                    <p>Your invoice is attached.</p>

                    <hr>

                    <small style="color:gray">
                        This is an automated email.
                    </small>

                </div>
            """.formatted(orderId);

            helper.setText(html, true);

            helper.addAttachment(
                    "invoice_" + orderId + ".pdf",
                    new ByteArrayResource(pdf)
            );

            mailSender.send(msg);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Async
    public void sendSimpleEmail(String to,String subject,String body){
        try{
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg,false);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, false);

            mailSender.send(msg);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void sendOtp(String to, String otp) {

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("Your OTP for Password Reset");
        msg.setText("Your OTP is: " + otp);

        mailSender.send(msg);
    }
}
