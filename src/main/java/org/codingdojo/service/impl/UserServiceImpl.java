package org.codingdojo.service.impl;

import org.codingdojo.domain.Role;
import org.codingdojo.domain.Task;
import org.codingdojo.domain.User;
import org.codingdojo.repository.UserRepository;
import org.codingdojo.service.NotificationService;
import org.codingdojo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Transactional
@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, NotificationService notificationService) {
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Override
    public User save(User User) {
        Assert.notNull(User, "User should be not null");
        return userRepository.save(User);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(Long id) {
        Assert.notNull(id, "id should be not null");
        return userRepository.findOne(id);
    }

    @Override
    public List<User> findByName(String name) {
        return userRepository.findByName(name);
    }

    @Override
    public void delete(Long id) {
        Assert.notNull(id, "id should be not null");
        try {
            User user = this.findById(id);
            List<Task> tasks = user.getTasks();
            User admin = this.userRepository.findByRole(Role.ADMIN);

            userRepository.delete(id);

            for (Task task : tasks) {
                if (!task.isDone()) {
                    notificationService.send(admin.getEmail(), "message");
                }
            }

        } catch (Exception e) {
            LOGGER.error("Error deleting user " + id, e);
        }
    }
}
