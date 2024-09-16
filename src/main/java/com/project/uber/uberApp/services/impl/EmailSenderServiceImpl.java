package com.project.uber.uberApp.services.impl;

import com.project.uber.uberApp.services.EmailSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSenderServiceImpl implements EmailSenderService {

    private final JavaMailSender javaMailSender;

    @Override
    public void sendEmail(String toEmail, String subject, String body) {

        try {
            //create Syntax for Mail
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

            //whom to send email (Receiver Email)
            simpleMailMessage.setTo(toEmail);
            //subject for email
            simpleMailMessage.setSubject(subject);
            //body of email
            simpleMailMessage.setText(body);

            //use javaMailSender Dependency to send Email
            javaMailSender.send(simpleMailMessage);
            log.info("Email Sent Successfully");
        }catch (Exception e)
        {
            log.info("Can not Send Email, "+e.getMessage());
        }
    }

    @Override
    public void sendEmail(String[] toEmail, String subject, String body) {

        try {
            //create Syntax for Mail
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

            //whom to send email (Receiver Email)
            simpleMailMessage.setTo(toEmail);
            //subject for email
            simpleMailMessage.setSubject(subject);
            //body of email
            simpleMailMessage.setText(body);

            //use javaMailSender Dependency to send Email
            javaMailSender.send(simpleMailMessage);
            log.info("Email Sent Successfully");
        }catch (Exception e)
        {
            log.info("Can not Send Email, "+e.getMessage());
        }

    }
}
