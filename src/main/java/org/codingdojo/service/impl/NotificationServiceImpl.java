package org.codingdojo.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.codingdojo.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    @Override
    public void send(String email, String message) {
        Assert.notNull(email, "email should be not null");
        Assert.notNull(message, "message should be not null");

        log.info("New message for {}: {]}", email, message);
    }
}
