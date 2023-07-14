package ru.kata.spring.boot_security.demo.exceptions_handlers;




// класс для ошибки. Например, когда передаем Id несуществующего пользователя.
public class UserErrorResponse {
    private String message; // указываем сообщение об ошибке
    private long timestamp;  // указываем время ошибки в миллисекундах

    public UserErrorResponse(String message, long timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
