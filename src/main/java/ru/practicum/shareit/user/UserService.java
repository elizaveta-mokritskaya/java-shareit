package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    User addUser(User user);

    User updateUser(long userId, User user);

    User getUserById(long userId);

    void deleteUserById(long userId);
}
