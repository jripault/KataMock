package org.codingdojo.service;

import org.codingdojo.domain.User;

import java.util.List;

public interface UserService {

    User save(User User);

    List<User> findAll();

    User findById(Long id);

    void delete(Long id);

    User findOne(Long id);
}
