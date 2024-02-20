package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserService userService;

    @Override
    public List<Item> getItems(Long userId) {
        return repository.getAllItemByUserId(userId);
    }

    @Override
    public Item addNewItem(Long userId, Item item) {
        if (userId == null) {
            throw new ValidationException("Отсутствует идентификатор пользователя");
        }
        if (item.getName() == null || item.getName().isEmpty()) {
            throw new ValidationException("Поле 'name' не может быть пустым.");
        }
        if (item.getAvailable() == null) {
            throw new ValidationException("Отсутствует статус");
        }
        if (item.getDescription() == null) {
            throw new ValidationException("Отсутствует описание");
        }
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("Пользователь с ID " + userId + " не найден.");
        }
        item.setOwner(user);
        item.setRequest(new ItemRequest());
        return repository.addItem(item);
    }

    @Override
    public Item updateItem(Long userId, Long itemId, String itemName, String description, Boolean available) {
        Item updateItem = repository.getItemById(itemId);
        if (updateItem == null) {
            throw new DataNotFoundException("По заданному Id нет предмета");
        }
        if (!Objects.equals(updateItem.getOwner().getId(), userId)) {
            throw new DataNotFoundException("Пользователь с заданным Id не является владельцем");
        }
        if (itemName != null) {
            updateItem.setName(itemName);
        }
        if (description != null) {
            updateItem.setDescription(description);
        }
        if (available != null) {
            updateItem.setAvailable(available);
        }
        return repository.addItem(updateItem);
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        repository.deleteByUserIdAndItemId(userId, itemId);
    }

    @Override
    public Item getItemById(Long itemId) {
        return repository.getItemById(itemId);
    }

    @Override
    public List<Item> getItemsByDescription(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        List<Item> allItems = repository.getAllItems();
        List<Item> matchingItems = new ArrayList<>();

        for (Item item : allItems) {
            String itemName = item.getName();
            String itemDescription = item.getDescription();
            if (itemName != null && itemName.toLowerCase().contains(text.toLowerCase())
                    || (itemDescription != null && itemDescription.toLowerCase().contains(text.toLowerCase()))
                    && item.getAvailable()) {
                matchingItems.add(item);
            }
        }

        return matchingItems;
    }
}
