package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemOutcomeDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestIncomeDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private final ItemService itemService;

    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestBody ItemRequestIncomeDto dto) {
        log.info("Получен запрос на добавление нового запроса '{}' пользователю '{}'", dto, userId);
        return ItemRequestMapper.toItemRequestDto(itemRequestService.addNewRequest(userId, dto.getDescription()));
    }

    @GetMapping
    public List<ItemRequestInfoDto> getRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на показ всех запросов пользователя '{}'", userId);
        return itemRequestService.getRequests(userId).stream()
                .map(r -> {
                    List<ItemOutcomeDto> dtoList = itemService.findItemsByRequestId(r.getId()).stream()
                            .map(ItemMapper::toItemDto)
                            .collect(Collectors.toList());
                    return ItemRequestMapper.toItemRequestDto2(r, dtoList);
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/all")
    public List<ItemRequestInfoDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(name = "from", defaultValue = "0") int from,
                                                   @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Пользователь '{}' просит показать список запросов других пользователей", userId);
                return itemRequestService.getAllRequests(userId, from / size, size).stream()
                .map(r -> {
                    List<ItemOutcomeDto> dtoList = itemService.findItemsByRequestId(r.getId()).stream()
                            .map(ItemMapper::toItemDto)
                            .collect(Collectors.toList());
                    return ItemRequestMapper.toItemRequestDto2(r, dtoList);
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/{requestId}")
    public ItemRequestInfoDto getRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable("requestId") Long requestId) {
        log.info("Пользователь '{}' просит показать запрос '{}'", userId, requestId);
        List<ItemOutcomeDto> dtoList = itemService.findItemsByRequestId(requestId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        return ItemRequestMapper.toItemRequestDto2(itemRequestService.getRequestById(userId, requestId), dtoList);
    }
}
