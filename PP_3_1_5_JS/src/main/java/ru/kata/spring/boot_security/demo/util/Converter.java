package ru.kata.spring.boot_security.demo.util;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.dto.UserDto;
import ru.kata.spring.boot_security.demo.model.User;

@Component
public class Converter {
    private static ModelMapper modelMapper;

    public Converter(ModelMapper modelMapper) {
        Converter.modelMapper = modelMapper;
    }

    public static User convertToUser(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }
    public static UserDto convertToUserDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }
}
