package org.codingdojo.controller;

import org.codingdojo.domain.User;
import org.codingdojo.exception.ResourceNotFoundException;
import org.codingdojo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/user")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        Assert.notNull(userService, "userService must be not null");
        this.userService = userService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public User create(@RequestBody User user) {
        user = userService.save(user);
        LOGGER.debug("User created: {}", user);
        return user;
    }

    @GetMapping
    public List<User> findAll() {
        List<User> users = userService.findAll();
        LOGGER.debug("User(s): {} (nb: {})", users, users.size());
        return users;
    }

    @GetMapping(path = "{id}")
    public User findById(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) {
            throw new ResourceNotFoundException(String.format("User with id: %s not found", id));
        }
        LOGGER.debug("User found with id '{}': {}", id, user);
        return user;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
        LOGGER.debug("User with id '{}' was deleted", id);
    }

    @GetMapping(path = "name/{name}")
    public List<User> findByName(@PathVariable String name) {
        List<User> users = userService.findByName(name);
        LOGGER.debug("User(s) with name '{}' found: {} (nb: {})", name, users, users.size());
        return users;
    }
}
