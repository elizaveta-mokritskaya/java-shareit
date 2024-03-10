package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("GATEWAY: Получен запрос на получение списка всех пользователей");
        return userClient.getAllUsers();
    }

    @PostMapping
    public ResponseEntity<Object> addNewUser(@Valid @RequestBody UserDto userDto) {
        log.info("GATEWAY: Получен запрос на добавление нового пользователя '{}'", userDto);
        ResponseEntity<Object> response = userClient.addNewUser(userDto);
        log.info("GATEWAY: Пользователь '{}' создан", userDto);
        return response;
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable("userId") Long userId, @RequestBody UserDto userDto) {
        log.info("GATEWAY: Получен запрос на обновление данных пользователя '{}'", userId);
        if (userDto.getId() == null) {
            userDto.setId(userId);
        }
        return userClient.updateUser(userId, userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable("userId") Long userId) {
        log.info("GATEWAY: Получен запрос - показать данные пользователя '{}'", userId);
        return userClient.getUserById(userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable("userId") Long userId) {
        log.info("GATEWAY: Получен запрос - удалить данные пользователя '{}'", userId);
        ResponseEntity<Object> response = userClient.deleteUserById(userId);
        log.info("GATEWAY: Пользователь '{}' удалён", userId);
        return response;
    }
}
