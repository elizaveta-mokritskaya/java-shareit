package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    List<Item> getItems(Long userId);

    Item addNewItem(Long userId, Item item);

    Item updateItem(Long userId, Long itemId, String itemName, String description, Boolean available);

    void deleteItem(Long userId, Long itemId);

    Item getItemById(Long itemId);

    List<Item> getItemsByDescription(String text);
}
