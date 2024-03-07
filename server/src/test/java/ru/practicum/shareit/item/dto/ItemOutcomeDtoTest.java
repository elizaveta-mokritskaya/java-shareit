package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.Status;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserMapper;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemOutcomeDtoTest {
    @Autowired
    private JacksonTester<ItemOutcomeDto> jacksonTester;

    @Test
    void getId() throws Exception {
        User booker = new User(1L, "booker@mail.ru", "booker");
        User owner = new User(2L, "owner@mail.ru", "owner");
        LocalDateTime created = LocalDateTime.now();
        ItemRequest request1 = new ItemRequest(1L, "request1", booker, created);
        Item item1 = new Item(1L, "item1", "description1", Status.AVAILABLE, owner, request1);
        ItemOutcomeDto itemOutcomeDto = new ItemOutcomeDto(1L, item1.getName(), item1.getDescription(),
                true, UserMapper.toUserDto(owner), request1.getId());

        JsonContent<ItemOutcomeDto> result = jacksonTester.write(itemOutcomeDto);
        assertThat(result).hasJsonPath("$.available");
    }
}