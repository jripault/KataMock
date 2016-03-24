package org.codingdojo.controller;

import lombok.extern.slf4j.Slf4j;
import org.codingdojo.domain.User;
import org.codingdojo.exception.ResourceNotFoundException;
import org.codingdojo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/api/user")
public class UserController {

    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        Assert.notNull(userRepository, "userRepository must be not null");
        this.userRepository = userRepository;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public User create(@RequestBody User user) {
        return userRepository.save(user);
    }

    @RequestMapping
    public List<User> findAll() {
        List<User> users = userRepository.findAll();
        log.debug("User(s): {} (nb: {})", users, users.size());
        return users;
    }

    @RequestMapping(path = "{id}")
    public User findById(@PathVariable Long id) {
        User user = userRepository.findOne(id);
        if (user == null) {
            throw new ResourceNotFoundException(String.format("User with id: %s not found", id));
        }

        log.debug("User found with id '{}': {}", id, user);
        return user;
    }

    @RequestMapping(path = "name/{name}")
    public List<User> findByName(@PathVariable String name) {
        List<User> users = userRepository.findByName(name);
        log.debug("User(s) with name '{}' found: {} (nb: {})", name, users, users.size());
        return users;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(path = "{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable Long id) {
        userRepository.delete(id);
    }
}
