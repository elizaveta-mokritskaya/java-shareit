package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {

    @Test
    void toUserDto() {
        User owner = new User(2L, "owner@mail.ru", "owner");
        UserDto userDto = new UserDto(2L, owner.getEmail(), owner.getName());

        UserDto result = UserMapper.toUserDto(owner);

        assertEquals(userDto, result);

    }

    @Test
    void toUser() {
        User owner = new User(2L, "owner@mail.ru", "owner");
        UserDto userDto = new UserDto(2L, owner.getEmail(), owner.getName());

        User result = UserMapper.toUser(userDto);

        assertEquals(owner, result);
    }
}