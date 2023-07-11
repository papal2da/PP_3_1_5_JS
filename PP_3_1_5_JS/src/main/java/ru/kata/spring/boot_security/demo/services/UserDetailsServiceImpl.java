package ru.kata.spring.boot_security.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Задача данного сервиса - просто предоставить юзера по имени
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserRepository userRepository;
    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByUserName(String name) {
        return userRepository.findByName(name);
    }
    // Ключевой метод UserDetailsService, именно ради этого функционала необходим этот класс. Тягаем по юсернейм(unique) юзера
    // для передачи DaoAuthenticationProvider и дальнейшего сравнения с полученным UAPT-токеном=представляем нашего юзера
    // в виде юзера, которого понимает Spring Security
    @Override
    // LAZY LOAD - т.к. мы достали юзера и пытаемся получить коллекцию ролей, one-to-many не грузится в EAGER,
    // поэтому необходимо взять все это в одну транзакцию для решения этой проблемы.
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new org.springframework.security.core.userdetails.User(
                user.getName(),
                user.getPassword(),
                mapRolesToAuthorities(user.getRoles())
        );
    }

    // метод мапинга ролей в права доступа. Получаем коллекцию ролей из юзера, достаем из них права доступа.
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities (Collection<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }

}
