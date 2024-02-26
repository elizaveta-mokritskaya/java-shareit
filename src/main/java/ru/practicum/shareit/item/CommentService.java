package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentService {

    List<Comment> getComments(Long itemId);

    List<Comment> findAllByItemId(Long itemId);

    Comment addComment(Long userId, long itemId, String text);
}
