package ru.kata.spring.boot_security.demo.services;

import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> findAll();
    User findOne(Long id);
    void save(User user);

    void update(Long id, User updatedUser);

    void delete(Long id);
     List<Role> getRoles();
}
