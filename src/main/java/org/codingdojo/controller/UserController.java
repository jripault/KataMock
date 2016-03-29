package org.codingdojo.controller;

import lombok.extern.slf4j.Slf4j;
import org.codingdojo.domain.User;
import org.codingdojo.exception.ResourceNotFoundException;
import org.codingdojo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/api/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        Assert.notNull(userService, "userService must be not null");
        this.userService = userService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public User create(@RequestBody User user) {
        return userService.save(user);
    }

    @RequestMapping
    public List<User> findAll() {
        List<User> users = userService.findAll();
        log.debug("User(s): {} (nb: {})", users, users.size());
        return users;
    }

    @RequestMapping(path = "{id}")
    public User findById(@PathVariable Long id) {
        User user = userService.findOne(id);
        if (user == null) {
            throw new ResourceNotFoundException(String.format("User with id: %s not found", id));
        }

        log.debug("User found with id '{}': {}", id, user);
        return user;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(path = "{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }

    @RequestMapping(path = "name/{name}")
    public List<User> findByName(@PathVariable String name) {
        List<User> users = userService.findByName(name);
        log.debug("User(s) with name '{}' found: {} (nb: {})", name, users, users.size());
        return users;
    }
}
