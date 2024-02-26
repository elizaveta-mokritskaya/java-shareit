package ru.practicum.shareit.exception;

public class NoEnumValueArgumentException extends RuntimeException {
    public NoEnumValueArgumentException(String message) {
        super(message);
    }
}
