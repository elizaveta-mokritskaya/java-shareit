package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingIncomeDto;
import ru.practicum.shareit.booking.dto.BookingOutcomeDto;
import ru.practicum.shareit.booking.dto.SearchStatus;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.Status;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {
    @InjectMocks
    private CommentServiceImpl commentService;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingService bookingService;
    @Mock
    private UserService userService;
    @Mock
    private ItemService itemService;
    @Captor
    private ArgumentCaptor<Comment> commentCaptor;

    private User booker;
    private User owner;
    private UserDto bookerDto;
    private UserDto ownerDto;
    private LocalDateTime created;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemRequest request1;
    private ItemRequest request2;
    private Item item1;
    private Item item2;
    private Booking booking1;
    private Booking booking2;
    private BookingIncomeDto bookingIncomeDto;
    private BookingOutcomeDto bookingOutcomeDto;
    private BookingOutcomeDto bookingOutcomeDto2;
    private Comment comment1;
    private Comment comment2;

    @BeforeEach
    void setUp() {
        booker = new User(1L, "user1@mail.ru", "user1");
        owner = new User(2L, "user2@mail.ru", "user2");
        bookerDto = new UserDto(1L, "user1@mail.ru", "user1");
        ownerDto = new UserDto(2L, "user2@mail.ru", "user2");
        created = LocalDateTime.now();
        start = LocalDateTime.now();
        end = LocalDateTime.now().plusDays(1);
        request1 = new ItemRequest(1L, "запрос1", booker, created);
        request2 = new ItemRequest(2L, "запрос2", booker, created);
        item1 = new Item(1L, "item1", "description1", Status.AVAILABLE, owner, request1);
        item2 = new Item(2L, "item2", "description2", Status.UNAVAILABLE, owner, request2);
        booking1 = new Booking(1L, start, end, item1, booker, BookingStatus.WAITING);
        booking2 = new Booking(2L, start, end, item2, booker, BookingStatus.WAITING);
        bookingIncomeDto = new BookingIncomeDto(1L, start, end, 1L);
        bookingOutcomeDto = new BookingOutcomeDto(1L, start, end, item1, booker, booking1.getBookingStatus().name());
        bookingOutcomeDto2 = new BookingOutcomeDto(2L, start, end, item2, booker, booking2.getBookingStatus().name());
        comment1 = new Comment(1L, "comment1", item1, booker, created);
        comment2 = new Comment(2L, "comment2", item1, booker, created);
    }


    @Test
    @DisplayName("Получение списка комментариев вещи")
    void getComments() {
        List<Comment> commentList = List.of(comment1, comment2);
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(commentList);

        List<Comment> result = commentService.getComments(item1.getId());

        assertEquals(commentList, result);
    }

    @Test
    @DisplayName("Получение списка комментариев")
    void findAllByItemId() {
        List<Comment> commentList = List.of(comment1, comment2);
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(commentList);

        List<Comment> result = commentService.findAllByItemId(item1.getId());

        assertEquals(commentList, result);
    }

    @Test
    @DisplayName("Успешное добавление комментария")
    void addComment_isSuccess() {
        String text = "comment1";
        List<Booking> bookingList = List.of(booking1);
        when(userService.getUserById(owner.getId())).thenReturn(ownerDto);
        when(itemService.getItemById(owner.getId(), item1.getId())).thenReturn(item1);
        when(bookingService.getBookingsByUser(owner.getId(), SearchStatus.PAST, 0, 0)).thenReturn(bookingList);

        commentService.addComment(owner.getId(), item1.getId(), text);

        verify(commentRepository).save(commentCaptor.capture());
        assertAll(
                () -> assertEquals(item1, commentCaptor.getValue().getItem()),
                () -> assertEquals(owner, commentCaptor.getValue().getAuthor()),
                () -> assertEquals(text, commentCaptor.getValue().getText()),
                () -> assertNull(commentCaptor.getValue().getId())
        );
    }

    @Test
    @DisplayName("Вернуть ошибку при значении пользователя null")
    void addComment_whenParamIsNotValid_thenReturnDataNotFoundException() {
        String text = "comment1";
        Long userId = 10L;
        Long itemId = 1L;
        Mockito.when(userService.getUserById(userId)).thenReturn(null);

        DataNotFoundException exception = Assertions.assertThrows(DataNotFoundException.class,
                () -> commentService.addComment(userId, itemId, text));

        Assertions.assertEquals("Владелец вещи не найден", exception.getMessage());
    }

    @Test
    @DisplayName("Вернуть ошибку при значении параметра text null")
    void addComment_whenParamTextIsNull_thenReturnDataNotFoundException() {
        Long userId = 2L;
        Long itemId = 1L;

        Mockito.when(userService.getUserById(userId)).thenReturn(ownerDto);

        DataNotFoundException exception = Assertions.assertThrows(DataNotFoundException.class,
                () -> commentService.addComment(userId, itemId, null));

        Assertions.assertEquals("Текст комментария не может быть пустым", exception.getMessage());
    }

    @Test
    @DisplayName("Вернуть ошибку при значении параметра item null")
    void addComment_whenParamItemIsNull_thenReturnDataNotFoundException() {
        String text = "comment1";
        Long userId = 2L;
        Long itemId = 10L;

        Mockito.when(userService.getUserById(userId)).thenReturn(ownerDto);
        Mockito.when(itemService.getItemById(userId, itemId)).thenReturn(null);

        DataNotFoundException exception = Assertions.assertThrows(DataNotFoundException.class,
                () -> commentService.addComment(userId, itemId, text));

        Assertions.assertEquals("Вещь с таким id не найдена", exception.getMessage());
    }
}