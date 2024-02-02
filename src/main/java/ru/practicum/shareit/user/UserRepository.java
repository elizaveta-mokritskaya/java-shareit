package ru.practicum.shareit.user;

import java.util.List;

public interface UserRepository {
    List<User> getAllUsers();

    User addUser(User user);

    User getUserByEmail(String email);

    User getUserById(Long userId);

    void deleteUserById(long userId);

    boolean existsByEmail(String email);
}
