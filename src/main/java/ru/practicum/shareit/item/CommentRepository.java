package ru.practicum.shareit.item;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("select c from Comment as c where c.item.id = :itemId order by c.created desc ")
    List<Comment> findAllByItemId(long itemId);

    List<Comment> findAllByItemIdIn(List<Long> items, Sort sort);
}
