package org.codingdojo.service.impl;

import org.codingdojo.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private MailSender mailSender;

    @Override
    public void send(String email, String message) {
        Assert.notNull(email, "email should be not null");
        Assert.notNull(message, "message should be not null");

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(email);
        mail.setSubject("Task notification");
        mail.setText(message);
        mailSender.send(mail);
    }
}
