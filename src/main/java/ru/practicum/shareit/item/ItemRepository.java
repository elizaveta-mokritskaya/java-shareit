package ru.practicum.shareit.item;

import java.util.List;

public interface ItemRepository {
    List<Item> getAllItemByUserId(long userId);

    Item addItem(Item item);
    
    

    void deleteByUserIdAndItemId(long userId, long itemId);

    Item getItemById(Long itemId);

    List<Item> getAllItems();
}
