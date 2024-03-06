package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserMapper;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoTest {
    @Autowired
    private JacksonTester<ItemRequestDto> jacksonTester;

    @Test
    void itemRequestDtoTest() throws Exception {
        User booker = new User(1L, "user1@mail.ru", "user1");
        LocalDateTime created = LocalDateTime.now();
        ItemRequest request1 = new ItemRequest(1L, "request1", booker, created);
        ItemRequestDto dto = new ItemRequestDto(1L, request1.getDescription(), UserMapper.toUserDto(booker), created);

        JsonContent<ItemRequestDto> result = jacksonTester.write(dto);
        assertThat(result).hasJsonPath("$.created");
    }
}