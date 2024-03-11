package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemOutcomeDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemRequestMapperTest {

    @Test
    void toItemRequestDto() {
        User booker = new User(1L, "user1@mail.ru", "user1");
        LocalDateTime created = LocalDateTime.now();
        ItemRequest request1 = new ItemRequest(1L, "request1", booker, created);
        ItemRequestDto dto = new ItemRequestDto(1L, request1.getDescription(), UserMapper.toUserDto(booker), created);

        ItemRequestDto result = ItemRequestMapper.toItemRequestDto(request1);

        assertEquals(dto, result);
    }

    @Test
    void toItemRequestDto2() {
        User booker = new User(1L, "user1@mail.ru", "user1");
        UserDto ownerDto = new UserDto(2L, "user2@mail.ru", "user2");
        LocalDateTime created = LocalDateTime.now();
        ItemRequest request1 = new ItemRequest(1L, "request1", booker, created);
        ItemOutcomeDto itemDto = new ItemOutcomeDto(1L, "item1", "description1", true, ownerDto, request1.getId());
        ItemRequestInfoDto dto = new ItemRequestInfoDto(1L, request1.getDescription(), created, List.of(itemDto));

        ItemRequestInfoDto result = ItemRequestMapper.toItemRequestDto2(request1, List.of(itemDto));

        assertEquals(dto, result);
    }
}