package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestIncomeDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Valid @RequestBody ItemRequestIncomeDto dto) {
        log.info("GATEWAY: Получен запрос на добавление нового запроса '{}' пользователю '{}'", dto, userId);
        return itemRequestClient.addNewRequest(userId, dto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GATEWAY: Получен запрос на показ всех запросов пользователя '{}'", userId);
        return itemRequestClient.getRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(name = "from", defaultValue = "0") int from,
                                                 @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("GATEWAY: Пользователь '{}' просит показать список запросов других пользователей", userId);
        if ((from < 0) || (size < 1)) {
            throw new ValidationException("Параметры запроса неверны");
        }
        return itemRequestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable("requestId") Long requestId) {
        log.info("GATEWAY: Пользователь '{}' просит показать запрос '{}'", userId, requestId);
        return itemRequestClient.getRequestById(userId, requestId);
    }
}
