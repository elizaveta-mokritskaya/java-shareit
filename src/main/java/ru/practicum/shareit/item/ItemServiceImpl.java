package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.Status;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserService userService;
    private final ItemRequestService requestService;

    @Override
    public List<Item> getItems(Long userId) {
        return repository.getAllByUserId(userId);
    }

    @Override
    public List<Item> getItemsToPage(Long userId, int from, int size) {
        Sort sortById = Sort.by(Sort.Direction.ASC, "id");
        return repository.findAllByUserIdPage(userId, PageRequest.of(from, size, sortById)).getContent();
    }

    @Override
    public Item addNewItem(Long userId, String name, String description, Boolean available, Long requestId) {
        User user = UserMapper.toUser(userService.getUserById(userId));
        Status status;
        if (available) {
            status = Status.AVAILABLE;
        } else {
            status = Status.UNAVAILABLE;
        }
        return repository.save(new Item(
                null,
                name,
                description,
                status,
                user,
                requestId != null ? requestService.getRequestById(userId, requestId) : null
        ));
    }

    @Override
    public Item updateItem(Long userId, Long itemId, String itemName, String description, Boolean available) {
        Item updateItem = repository.getById(itemId);
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
        Status status;
        if (available != null) {
            if (available) {
                status = Status.AVAILABLE;
            } else {
                status = Status.UNAVAILABLE;
            }
            updateItem.setAvailable(status);
        }
        return repository.save(updateItem);
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        repository.deleteByUserIdAndItemId(userId, itemId);
    }

    @Override
    public Item getItemById(Long userId, Long itemId) {
        if (userService.getUserById(userId) == null) {
            throw new DataNotFoundException("Пользователь не найден.");
        }
        Item item = repository.findById(itemId).orElse(null);
        if (item == null) {
            throw new DataNotFoundException("Вещь с таким id не найдена.");
        }
        return item;
    }

    @Override
    public List<Item> getItemsByDescription(String text, int from, int size) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        return repository.getItemsByDescription(text, PageRequest.of(from, size, sort)).getContent();
    }

    @Override
    public boolean userIsOwnerOfItem(long userId, Long itemId) {
        if (userService.getUserById(userId).getId().equals(repository.getReferenceById(itemId).getOwner().getId())) {
            return true;
        }
        return false;
    }

    @Override
    public List<Item> findItemsByOwnerId(Long userId) {
        if (userService.getUserById(userId) == null) {
            throw new DataNotFoundException("Владелец вещи не найден");
        }
        return repository.getByOwnerId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Item> findItemsByRequestId(long requestId) {
        return repository.findAllByRequestId(requestId);
    }
}
