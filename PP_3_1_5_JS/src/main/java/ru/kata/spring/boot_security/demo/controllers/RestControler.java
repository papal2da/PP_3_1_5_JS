package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.dto.UserDto;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.services.UserDetailsServiceImpl;
import ru.kata.spring.boot_security.demo.services.UserService;
import ru.kata.spring.boot_security.demo.util.Converter;
import ru.kata.spring.boot_security.demo.exceptions_handlers.UserNotCreatedException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api")
public class RestControler {
    /*
    Конвертер просто импортировал, потому что у нас там только стат методы
    , внедрять бин бессмысленно.
    */
    private final UserService userService;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    public RestControler(UserService userService, UserDetailsServiceImpl userDetailsServiceImpl) {
        this.userService = userService;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable("id") Long id) {
        return new ResponseEntity<>(Converter.convertToUserDto(userService.findOne(id)), HttpStatus.OK);
    }

    @GetMapping("/admin")
    public ResponseEntity<List<UserDto>> showAllUsers() {
        List<UserDto> dto = userService.findAll().stream().map(Converter::convertToUserDto).collect(Collectors.toList());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping("/admin")
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid UserDto userDto,
                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                errorMsg.append(fieldError.getField())                 // собираем красивое информативное сообщение об ошибке
                        .append("-")                                   // таская атрибуты поля
                        .append(fieldError.getDefaultMessage());
            }
            throw new UserNotCreatedException(errorMsg.toString());
        }
        userService.save(Converter.convertToUser(userDto));
        // отправляем http-ответ с пустым телом и статусом 200. СлучаЙ,когда мы не хотим отсылать обратно объект
        return ResponseEntity.ok(HttpStatus.OK);
    }


    @PatchMapping("/admin/{id}")
    public ResponseEntity<UserDto> update(@PathVariable("id") Long id
            , @RequestBody @Valid User user
            , BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError: fieldErrors) {
                errorMsg.append(fieldError.getField())
                        .append("-")
                        .append(fieldError.getDefaultMessage());
            }
            throw new RuntimeException(errorMsg.toString());
        }
        userService.update(id, user);
        return new ResponseEntity<>(Converter.convertToUserDto(user), HttpStatus.OK);
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") long id) {
        userService.delete(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/roles")
    public ResponseEntity<Set<Role>> getRoles() {
        return new ResponseEntity<>(new HashSet<>(userService.getRoles()), HttpStatus.OK);
    }

    @GetMapping("/currentuser")
    public ResponseEntity<UserDto> getCurrentUser(Principal principal) {
        return new ResponseEntity<>(Converter.convertToUserDto(userDetailsServiceImpl.findByUserName(principal.getName())),
                HttpStatus.OK);
    }


}
