package org.codingdojo.service.impl;

import org.codingdojo.domain.User;
import org.codingdojo.exception.ResourceNotFoundException;
import org.codingdojo.repository.UserRepository;
import org.codingdojo.service.NotificationService;
import org.codingdojo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Transactional
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, NotificationService notificationService) {
        Assert.notNull(userRepository, "userRepository must be not null");
        Assert.notNull(notificationService, "notificationService must be not null");
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Override
    public User save(User User) {
        Assert.notNull(User, "user should be not null");
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
        User user = userRepository.findOne(id);
        if (user == null) {
            throw new ResourceNotFoundException(String.format("User with id: %s not found", id));
        }
        return user;
    }

    @Override
    public List<User> findByName(String name) {
        Assert.notNull(name, "name should be not null");
        return userRepository.findByName(name);
    }

    @Override
    public void delete(Long id) {
        Assert.notNull(id, "id should be not null");
        User user = this.findById(id);
        if (user == null) {
            throw new ResourceNotFoundException(String.format("User with id: %s not found", id));
        }
        userRepository.delete(id);
    }
}
