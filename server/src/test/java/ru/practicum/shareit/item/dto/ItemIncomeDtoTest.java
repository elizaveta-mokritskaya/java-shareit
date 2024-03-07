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

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemIncomeDtoTest {
    @Autowired
    private JacksonTester<ItemIncomeDto> jacksonTester;

    @Test
    void itemIncomeDtoTest() throws Exception {
        User booker = new User(1L, "booker@mail.ru", "booker");
        User owner = new User(2L, "owner@mail.ru", "owner");
        LocalDateTime created = LocalDateTime.now();
        ItemRequest request1 = new ItemRequest(1L, "request1", booker, created);
        Item item1 = new Item(1L, "item1", "description1", Status.AVAILABLE, owner, request1);
        ItemIncomeDto incomeDto = new ItemIncomeDto(item1.getName(), item1.getDescription(), true, request1.getId());

        JsonContent<ItemIncomeDto> result = jacksonTester.write(incomeDto);
        assertThat(result).hasJsonPath("$.available");
    }
}