package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("select i from Item as i " +
            "where (upper(i.name) like upper(concat('%', :text, '%')) " +
            " or upper(i.description) like upper(concat('%', :text, '%'))) " +
            "and i.available = ru.practicum.shareit.item.model.Status.AVAILABLE ")
    List<Item> getItemsByDescription(String text);

    @Query("delete from Item as i " +
            "where i.id = :itemId and i.owner.id = :userId")
    void deleteByUserIdAndItemId(long itemId, long userId);

    @Query("select i from Item as i " +
            "where i.owner.id = :userId order by i.id")
    List<Item> getAllByUserId(Long userId);

    @Query("select i from Item as i " +
            "where i.owner.id = :userId order by i.id")
    List<Item> getByOwnerId(Long userId);
}
