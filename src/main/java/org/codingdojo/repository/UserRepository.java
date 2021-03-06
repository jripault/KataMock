package org.codingdojo.repository;

import org.codingdojo.domain.Role;
import org.codingdojo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByName(String name);

    List<User> findByRole(Role admin);
}
