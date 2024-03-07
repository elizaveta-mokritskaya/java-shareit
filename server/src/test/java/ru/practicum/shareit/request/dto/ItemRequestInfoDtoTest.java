package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemOutcomeDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestInfoDtoTest {
    @Autowired
    private JacksonTester<ItemRequestInfoDto> jacksonTester;

    @Test
    void itemRequestInfoDtoTest() throws Exception {
        User booker = new User(1L, "user1@mail.ru", "user1");
        UserDto ownerDto = new UserDto(2L, "user2@mail.ru", "user2");
        LocalDateTime created = LocalDateTime.now();
        ItemRequest request1 = new ItemRequest(1L, "request1", booker, created);
        ItemOutcomeDto itemDto = new ItemOutcomeDto(1L, "item1", "description1", true, ownerDto, request1.getId());
        ItemRequestInfoDto dto = new ItemRequestInfoDto(1L, request1.getDescription(), created, List.of(itemDto));

        JsonContent<ItemRequestInfoDto> result = jacksonTester.write(dto);
        assertThat(result).hasJsonPath("$.created");
    }
}