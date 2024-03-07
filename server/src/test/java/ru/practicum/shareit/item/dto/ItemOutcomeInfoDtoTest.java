package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.Status;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemOutcomeInfoDtoTest {
    @Autowired
    private JacksonTester<ItemOutcomeInfoDto> jacksonTester;

    @Test
    void itemOutcomeInfoDtoTest() throws Exception {
        User booker = new User(1L, "booker@mail.ru", "booker");
        User owner = new User(2L, "owner@mail.ru", "owner");
        LocalDateTime created = LocalDateTime.now();
        ItemRequest request1 = new ItemRequest(1L, "request1", booker, created);
        Item item1 = new Item(1L, "item1", "description1", Status.AVAILABLE, owner, request1);
        Comment comment1 = new Comment(1L, "comment1", item1, booker, created);
        CommentDto commentDto = CommentMapper.toCommentDto(comment1);
        ItemOutcomeInfoDto infoDto = new ItemOutcomeInfoDto(1L, item1.getName(), item1.getDescription(),
                true, UserMapper.toUserDto(owner), request1.getId(), null, null, List.of(commentDto));

        JsonContent<ItemOutcomeInfoDto> result = jacksonTester.write(infoDto);
        assertThat(result).hasJsonPath("$.available");
    }
}