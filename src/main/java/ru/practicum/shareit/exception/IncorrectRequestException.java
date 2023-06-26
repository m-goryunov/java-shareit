package ru.practicum.shareit.exception;

public class IncorrectRequestException extends RuntimeException {
    public IncorrectRequestException(String message, String s) {
        super(message);
    }
}
