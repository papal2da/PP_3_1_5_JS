package ru.kata.spring.boot_security.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;
import ru.kata.spring.boot_security.demo.exceptions_handlers.UserNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository
            ,PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findOne(Long id) {
        Optional<User> findUser = userRepository.findById(id);
        return findUser.orElseThrow(UserNotFoundException::new);
    }

    @Transactional
    public void save(User user) {
        String savingdUserPassword = user.getPassword();
        user.setPassword(passwordEncoder.encode(savingdUserPassword));
        userRepository.save(user);
    }

    @Transactional
    public void update(Long id, User updatedUser) {
//        long countInt = userRepository.findAll().stream().filter(user -> user.getId() == updatedUser.getId()).count();
        updatedUser.setId(id);
        String repositoryUserPassword = userRepository.getById(id).getPassword();
        String updatedUserPassword = updatedUser.getPassword();
        if (!updatedUserPassword.equals(repositoryUserPassword)) {
            updatedUser.setPassword(passwordEncoder.encode(updatedUserPassword));
        }
        userRepository.save(updatedUser);
    }
    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }
}
