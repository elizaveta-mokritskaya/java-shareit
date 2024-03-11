package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemIncomeDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getAllItemsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @RequestParam(name = "from", defaultValue = "0") int from,
                                                      @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("GATEWAY: Получен запрос - показать список вещей пользователя '{}'", userId);
        if ((from < 0) || (size < 1)) {
            throw new ValidationException("Параметры запроса неверны");
        }
        return itemClient.getAllItemsByUserId(userId, from, size);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable("itemId") long itemId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestBody ItemIncomeDto incomeDto) {
        log.info("GATEWAY: Получен запрос на обновление данных итема '{}' у пользователя '{}'", itemId, userId);
        ResponseEntity<Object> response = itemClient.updateItem(itemId, userId, incomeDto);
        log.info("GATEWAY: Вещь '{}' обновлена", incomeDto);
        return response;
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @Valid @RequestBody ItemIncomeDto incomeDto) {
        log.info("GATEWAY: Получен запрос на добавление итема '{}' пользователю '{}'", incomeDto, userId);
        ResponseEntity<Object> response = itemClient.addNewItem(userId, incomeDto);
        log.info("GATEWAY: Вещь '{}' добавлена", incomeDto);
        return response;
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @PathVariable("itemId") Long itemId) {
        log.info("GATEWAY: Получен запрос от пользователя '{}' - найти итем '{}'", userId, itemId);
        return itemClient.getItemById(userId, itemId);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable("itemId") Long itemId) {
        log.info("GATEWAY: Получен запрос от пользователя '{}' на удаление итема '{}'", userId, itemId);
        ResponseEntity<Object> response = itemClient.deleteItemById(userId, itemId);
        log.info("GATEWAY: Вещь '{}' удалёна", itemId);
        return response;
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestParam String text,
                                             @RequestParam(name = "from", defaultValue = "0") int from,
                                             @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("GATEWAY: Получен запрос на поиск итема по содержанию текста '{}' у пользователя '{}'", text, userId);
        if ((from < 0) || (size < 1)) {
            throw new ValidationException("Параметры запроса неверны");
        }
        return itemClient.getItemByDescription(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable long itemId,
                                             @Valid @RequestBody CommentDto commentDto) {
        log.info("GATEWAY: Получен запрос на добавление комментария '{}' пользоваелем '{}' к вещи '{}'",
                commentDto, userId, itemId);
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
