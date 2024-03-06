package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.User;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoTest {
    @Autowired
    private JacksonTester<UserDto> jacksonTester;

    @Test
    void userDtoTest() throws Exception {
        User owner = new User(2L, "owner@mail.ru", "owner");
        UserDto userDto = new UserDto(1L, owner.getEmail(), owner.getName());

        JsonContent<UserDto> result = jacksonTester.write(userDto);
        assertThat(result).hasJsonPath("$.email");
    }
}