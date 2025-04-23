package com.ql.BlogApplication.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@RequiredArgsConstructor
@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendOtpEmail(String toEmail, String otp){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Your OTP for Email Verification");
        message.setText("Your OTP is: " + otp + "\nIt is valid for 10 minutes.");
        message.setFrom("your_email@gmail.com"); // same as spring.mail.username

        javaMailSender.send(message);
    }
}
