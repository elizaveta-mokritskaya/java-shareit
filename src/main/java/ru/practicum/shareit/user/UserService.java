package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto addUser(Long id, String email, String name);

    UserDto updateUser(long userId, User user);

    UserDto getUserById(long userId);

    void deleteUserById(long userId);
}
