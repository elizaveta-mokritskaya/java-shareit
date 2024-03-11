package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.Status;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentMapperTest {

    @Test
    void toCommentDto() {
        User booker = new User(1L, "booker@mail.ru", "booker");
        User owner = new User(2L, "owner@mail.ru", "owner");
        LocalDateTime created = LocalDateTime.now();
        ItemRequest request1 = new ItemRequest(1L, "request1", booker, created);
        Item item1 = new Item(1L, "item1", "description1", Status.AVAILABLE, owner, request1);
        Comment comment1 = new Comment(1L, "comment1", item1, booker, created);
        CommentDto commentDto = new CommentDto(1L, "comment1", "booker", created);

        CommentDto result = CommentMapper.toCommentDto(comment1);

        assertEquals(commentDto, result);
    }
}