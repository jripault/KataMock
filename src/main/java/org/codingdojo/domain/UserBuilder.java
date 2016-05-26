package org.codingdojo.domain;

import java.util.List;

/**
 * User: JRI <julien.ripault@atos.net>
 * Date: 26/05/2016
 */
public class UserBuilder {
    private String email;
    private Long id;
    private String name;
    private Role role;
    private List<Task> tasks;

    private UserBuilder() {
    }

    public static UserBuilder anUser() {
        return new UserBuilder();
    }

    public UserBuilder email(String email) {
        this.email = email;
        return this;
    }

    public UserBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public UserBuilder name(String name) {
        this.name = name;
        return this;
    }

    public UserBuilder role(Role role) {
        this.role = role;
        return this;
    }

    public UserBuilder tasks(List<Task> tasks) {
        this.tasks = tasks;
        return this;
    }

    public UserBuilder but() {
        return anUser().email(email).id(id).name(name).role(role).tasks(tasks);
    }

    public User build() {
        User user = new User();
        user.setEmail(email);
        user.setId(id);
        user.setName(name);
        user.setRole(role);
        user.setTasks(tasks);
        return user;
    }
}
