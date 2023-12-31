package ru.kata.spring.boot_security.demo.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.dto.UserDto;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.services.UserDetailsServiceImpl;
import ru.kata.spring.boot_security.demo.services.UserService;
import ru.kata.spring.boot_security.demo.util.UserErrorResponse;
import ru.kata.spring.boot_security.demo.util.UserNotCreatedException;
import ru.kata.spring.boot_security.demo.util.UserNotFoundException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class RestControler {

    private final UserService userService;
    private final ModelMapper modelMapper;
    private final RoleRepository roleRepository;
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    @Autowired
    public RestControler(UserService userService, ModelMapper modelMapper,
    RoleRepository roleRepository, UserDetailsServiceImpl userDetailsServiceImpl) {
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }
    // хороший, отдаем в JSON DTO
    @GetMapping("/admin/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable("id") Long id) {
        return new ResponseEntity<>(convertToUserDto(userService.findOne(id)), HttpStatus.OK);
    }

    @GetMapping("/admin")
    public ResponseEntity<List<UserDto>> showAllUsers() {
        List<UserDto> dto = userService.findAll().stream().map(this::convertToUserDto).collect(Collectors.toList());
        return new ResponseEntity<>(dto, HttpStatus.OK);

    }

    @PostMapping("/admin")
    public ResponseEntity<HttpStatus> create(@Valid @RequestBody UserDto userDto,
                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            StringBuilder errorMsg = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for(FieldError fieldError: fieldErrors) {
                errorMsg.append(fieldError.getField())                 // собираем красивое информативное сообщение об ошибке
                        .append("-")                                   // таская атрибуты поля
                        .append(fieldError.getDefaultMessage());
            }
            throw new UserNotCreatedException(errorMsg.toString());
        }
        userService.save(convertToUser(userDto));
        // отправляем http-ответ с пустым телом и статусом 200. СлучаЙ,когда мы не хотим отсылать обратно объект
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/admin/{id}")
    public ResponseEntity update(@PathVariable("id") Long id
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
        return new ResponseEntity<UserDto>(convertToUserDto(user), HttpStatus.OK);
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") Long id) {
        userService.delete(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }




    // В хендлере отлавливаем кастомную ошибку, чтобы не ловить все подряд
    @ExceptionHandler
    private ResponseEntity<UserErrorResponse> handleException(UserNotFoundException e) {
        UserErrorResponse errorResponse = new UserErrorResponse(
                "User not found!",
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND); // возвращаем объект ответа с ошибкой в
        // теле и статусом 404 Not Found
    }

    @ExceptionHandler
    private ResponseEntity<UserErrorResponse> handleException(UserNotCreatedException e) {
        UserErrorResponse errorResponse = new UserErrorResponse(e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/roles")
    public ResponseEntity<Set<Role>> getRoles() {
        return new ResponseEntity<>(new HashSet<>(roleRepository.findAll()), HttpStatus.OK);
    }

    @GetMapping("/currentuser")
    public ResponseEntity<UserDto> getCurrentUser(Principal principal) {
        return new ResponseEntity<>(convertToUserDto(userDetailsServiceImpl.findByUserName(principal.getName())),
                HttpStatus.OK);
    }

    private User convertToUser(UserDto userDto) {
    return modelMapper.map(userDto, User.class);
    }
    private UserDto convertToUserDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }


}
