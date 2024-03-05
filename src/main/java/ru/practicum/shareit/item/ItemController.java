package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private final BookingService bookingService;
    private final CommentService commentService;

    @GetMapping
    public List<ItemOutcomeInfoDto> get(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestParam(name = "from", defaultValue = "0") int from,
                                        @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Получен запрос - показать список вещей пользователя '{}'", userId);
        if ((from < 0) || (size < 1)) {
            throw new ValidationException("Параметры запроса неверны");
        }
        return itemService.getItemsToPage(userId, from/size, size).stream().map(
                        item -> {
                            List<Booking> bookingList = bookingService.getBookingsForUser(item.getId());
                            List<Comment> commentList = commentService.getComments(item.getId());
                            List<CommentDto> commentDtos = commentList.stream().map(CommentMapper::toCommentDto)
                                    .collect(Collectors.toList());
                            return ItemMapper.toItemInfoDto(item, bookingList, commentDtos);
                        })
                .collect(Collectors.toList());
    }

    @PatchMapping("/{itemId}")
    public ItemOutcomeDto updateItem(@PathVariable("itemId") long itemId,
                                     @RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestBody ItemIncomeDto incomeDto) {
        log.info("Получен запрос на обновление данных итема '{}' у пользователя '{}'", itemId, userId);
        return ItemMapper.toItemDto(itemService.updateItem(
                userId,
                itemId,
                incomeDto.getName(),
                incomeDto.getDescription(),
                incomeDto.getAvailable()
        ));
    }

    @PostMapping
    public ItemOutcomeDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @Valid @RequestBody ItemIncomeDto incomeDto) {
        log.info("Получен запрос на добавление итема '{}' пользователю '{}'", incomeDto, userId);
        return ItemMapper.toItemDto(itemService.addNewItem(
                userId,
                incomeDto.getName(),
                incomeDto.getDescription(),
                incomeDto.getAvailable(),
                incomeDto.getRequestId()
        ));
    }

    @GetMapping("/{itemId}")
    public ItemOutcomeInfoDto getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @PathVariable("itemId") Long itemId) {
        log.info("Получен запрос от пользователя '{}' - найти итем '{}'", userId, itemId);
        Item item = itemService.getItemById(userId, itemId);
        List<CommentDto> commentsDto = commentService.getComments(item.getId()).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        ;
        if (itemService.userIsOwnerOfItem(userId, itemId)) {
            List<Booking> bookings = bookingService.getBookingsForUser(item.getId());
            return ItemMapper.toItemInfoDto(item, bookings, commentsDto);
        } else {
            return ItemMapper.toItemDtoWithComments(item, commentsDto);
        }
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable long itemId) {
        log.info("Получен запрос от пользователя '{}' на удаление итема '{}'", itemId, userId);
        itemService.deleteItem(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemOutcomeDto> searchItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestParam String text,
                                           @RequestParam(name = "from", defaultValue = "0") int from,
                                           @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Получен запрос на поиск итема по содержанию текста '{}' у пользователя '{}'", text, userId);
        if ((from < 0) || (size < 1)) {
            throw new ValidationException("Параметры запроса неверны");
        }
        return itemService.getItemsByDescription(text, from/size, size).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable long itemId,
                                 @Valid @RequestBody CommentDto commentDto) {
        return CommentMapper.toCommentDto(commentService.addComment(userId, itemId, commentDto.getText()));
    }
}
