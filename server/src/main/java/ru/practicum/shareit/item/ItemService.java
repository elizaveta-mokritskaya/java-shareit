package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<Item> getItems(Long userId);

    List<Item> getItemsToPage(Long userId, int from, int size);

    Item addNewItem(Long userId, String name, String description, Boolean available, Long requestId);

    Item updateItem(Long userId, Long itemId, String itemName, String description, Boolean available);

    void deleteItem(Long userId, Long itemId);

    Item getItemById(Long userId, Long itemId);

    List<Item> getItemsByDescription(String text, int from, int size);

    boolean userIsOwnerOfItem(long userId, Long itemId);

    List<Item> findItemsByOwnerId(Long userId);

    List<Item> findItemsByRequestId(long requestId);
}
