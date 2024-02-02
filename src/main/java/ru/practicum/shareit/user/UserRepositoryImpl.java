package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository{
    private static List<User> users = new ArrayList<>();
    private Long lastId = 1L;

    @Override
    public List<User> getAllUsers() {
        return users;
    }

    @Override
    public User addUser(User user) {
        if(user.getId() == null) {
            user.setId(generateUserId());
            if (existsByEmail(user.getEmail())) {
                throw new RuntimeException("Пользователь с таким email уже существует.");
            }
            users.add(user);
        }
        return user;
    }

    @Override
    public User getUserByEmail(String email) {
        return users.stream().filter(user -> user.getEmail().equals(email)).findAny().orElse(null);
    }

    @Override
    public User getUserById(Long userId) {
        return users.stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst().orElse(null);
    }

    @Override
    public void deleteUserById(long userId) {
        users.remove(getUserById(userId));
    }

    @Override
    public boolean existsByEmail(String email) {
        return users.stream().anyMatch(user -> user.getEmail().equals(email));
    }

    private Long generateUserId() {
        return lastId++;
    }
}
