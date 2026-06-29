package com.siti.sitiapi.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender emailSender;

    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage(); 
        message.setFrom("noreply@siti.com.br");
        message.setTo(to); 
        message.setSubject(subject); 
        message.setText(text);
        try {
            emailSender.send(message);
        } catch (org.springframework.mail.MailException e) {
            System.err.println("Aviso: Falha ao tentar enviar e-mail. " + e.getMessage());
        }
    }
}
