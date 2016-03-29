package org.codingdojo.service.impl;

import org.codingdojo.domain.User;
import org.codingdojo.repository.UserRepository;
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

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        Assert.notNull(userRepository, "userRepository must be not null");
        this.userRepository = userRepository;
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
    public void delete(Long id) {
        Assert.notNull(id, "id should be not null");
        userRepository.delete(id);
    }

    @Override
    public User findOne(Long id) {
        Assert.notNull(id, "id should be not null");
        return userRepository.findOne(id);
    }

    @Override
    public List<User> findByName(String name) {
        return userRepository.findByName(name);
    }

}
