package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.Status;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    @InjectMocks
    private ItemController itemController;
    @Mock
    private ItemService itemService;
    @Mock
    private BookingService bookingService;
    @Mock
    private CommentService commentService;

    private User booker;
    private User owner;
    private LocalDateTime created;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemRequest request1;
    private Item item1;
    private ItemIncomeDto itemIncomeDto;
    private Booking booking1;
    private Comment comment1;
    private Comment comment2;

    @BeforeEach
    void setUp() {
        booker = new User(1L, "user1@mail.ru", "user1");
        owner = new User(2L, "user2@mail.ru", "user2");
        created = LocalDateTime.now();
        start = LocalDateTime.now();
        end = LocalDateTime.now().plusDays(1);
        request1 = new ItemRequest(1L, "запрос1", booker, created);
        item1 = new Item(1L, "item1", "description1", Status.AVAILABLE, owner, request1);
        itemIncomeDto = new ItemIncomeDto("item1", "description1", true, 1L);
        booking1 = new Booking(1L, start, end, item1, booker, BookingStatus.WAITING);
        comment1 = new Comment(1L, "comment1", item1, booker, created);
        comment2 = new Comment(2L, "comment2", item1, booker, created);
    }

    @Test
    @DisplayName("Неверные параметры запроса GET")
    void get_isNotSuccess() {
        int from = -1;
        int size = 0;

        ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> itemController.get(1L, from, size));

        assertEquals("Параметры запроса неверны", exception.getMessage());
    }

    @Test
    @DisplayName("Успешное обновление данных вещи")
    void updateItem_thenUseServiceAndShouldReturnItemOutcomeDto() {
        when(itemService.updateItem(anyLong(), anyLong(), anyString(), anyString(), any())).thenReturn(item1).thenReturn(item1);
        ItemOutcomeDto itemOutcomeDto = ItemMapper.toItemDto(item1);

        ItemOutcomeDto result = itemController.updateItem(1L, 2L, itemIncomeDto);

        assertEquals(itemOutcomeDto, result);
    }

    @Test
    @DisplayName("Добавление вещи успешное")
    void add() {
        when(itemService.addNewItem(anyLong(), anyString(), anyString(), any(), anyLong())).thenReturn(item1);
        ItemOutcomeDto itemOutcomeDto = ItemMapper.toItemDto(item1);

        ItemOutcomeDto result = itemController.add(2L, itemIncomeDto);

        assertEquals(itemOutcomeDto, result);
    }

    @Test
    @DisplayName("Список вещей")
    void getItemById() {
        List<Comment> commentList = List.of(comment1, comment2);
        List<CommentDto> commentDtoList = List.of(CommentMapper.toCommentDto(comment1), CommentMapper.toCommentDto(comment2));
        List<Booking> bookingList = List.of(booking1);
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(item1);
        when(commentService.getComments(item1.getId())).thenReturn(commentList);
        when(itemService.userIsOwnerOfItem(anyLong(), anyLong())).thenReturn(true);
        when(bookingService.getBookingsForUser(anyLong())).thenReturn(bookingList);
        ItemOutcomeInfoDto infoDto = ItemMapper.toItemInfoDto(item1, bookingList, commentDtoList);

        ItemOutcomeInfoDto result = itemController.getItemById(booker.getId(), item1.getId());

        assertEquals(infoDto, result);
    }

    @Test
    @DisplayName("Удаление")
    void deleteItem() {
        itemController.deleteItem(2L, 1L);

        verify(itemService).deleteItem(2L, 1L);
    }

    @Test
    @DisplayName("Получен запрос на поиск итема по содержанию текста")
    void searchItem() {
        int from = -1;
        int size = 0;
        String text = "description1";

        ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> itemController.searchItem(1L, text, from, size));

        Assertions.assertEquals("Параметры запроса неверны", exception.getMessage());
    }

    @Test
    @DisplayName("Запрос на добавление комментария")
    void addComment() {
        CommentDto commentDto = CommentMapper.toCommentDto(comment1);
        when(commentService.addComment(anyLong(), anyLong(), anyString())).thenReturn(comment1);

        CommentDto result = itemController.addComment(2L, 1L, commentDto);

        assertEquals(commentDto, result);
    }
}