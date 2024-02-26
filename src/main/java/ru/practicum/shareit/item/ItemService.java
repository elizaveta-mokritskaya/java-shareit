package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<Item> getItems(Long userId);

    Item addNewItem(Long userId, String name, String description, Boolean available);

    Item updateItem(Long userId, Long itemId, String itemName, String description, Boolean available);

    void deleteItem(Long userId, Long itemId);

    Item getItemById(Long userId, Long itemId);

    List<Item> getItemsByDescription(String text);

    boolean userIsOwnerOfItem(long userId, Long itemId);

    List<Item> findItemsByOwnerId(Long userId);
}
