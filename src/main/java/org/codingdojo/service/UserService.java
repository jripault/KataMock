package org.codingdojo.service;

import org.codingdojo.domain.User;

import java.util.List;

public interface UserService {

    User save(User User);

    List<User> findAll();

    User findById(Long id);

    List<User> findByName(String name);

    void delete(Long id);
}
