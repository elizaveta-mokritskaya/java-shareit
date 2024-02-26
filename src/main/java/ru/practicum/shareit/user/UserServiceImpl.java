package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto addUser(Long id, String email, String name) {
        return UserMapper.toUserDto(userRepository.save(new User(id, email, name)));
    }

    @Override
    public UserDto updateUser(long userId, User user) {
        if (user == null) {
            throw new DataNotFoundException("Пользователь не найден.");
        }
        if (user.getId() == null) {
            user.setId(userId);
        }
        User userUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователь с ID=" + userId + " не найден!"));
        if (userUpdate == null) {
            throw new DataNotFoundException("Пользователь не найден.");
        }
        if (user.getName() != null) {
            userUpdate.setName(user.getName());
        }
        if (user.getEmail() != null) {
            User userByEmail = userRepository.getUserByEmail(user.getEmail());
            if (userByEmail == null) {
                userUpdate.setEmail(user.getEmail());
            } else if (!userByEmail.getId().equals(userId)) {
                throw new RuntimeException("Пользователь с таким email уже существует.");
            }
        }
        return UserMapper.toUserDto(userRepository.save(userUpdate));
    }

    @Override
    public UserDto getUserById(long userId) {
        return UserMapper.toUserDto(userRepository.findById(userId).orElseThrow(
                () -> new DataNotFoundException("Пользователь не найден"))
        );
    }

    @Override
    public void deleteUserById(long userId) {
        userRepository.deleteById(userId);
    }
}
