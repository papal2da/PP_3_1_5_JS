package ru.kata.spring.boot_security.demo.exceptions_handlers;

// Класс кастомного сообщения об ошибке.
// Если человек не найден в базе данных, выбрасываем эксепшн.
// Данный класс exception'a нужен для повышения информативности сообщения об ошибке.
public class UserNotFoundException extends RuntimeException {

}
