package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingOutcomeDto;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.Status;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    @Mock
    private ItemRequestRepository mockItemRequestRepository;
    @Mock
    private UserService mockUserService;

    private User booker;
    private User owner;
    private UserDto bookerDto;
    private LocalDateTime created;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemRequest request1;
    private ItemRequest request2;
    private Item item1;
    private Item item2;
    private Booking booking1;
    private Booking booking2;
    BookingOutcomeDto bookingOutcomeDto;
    BookingOutcomeDto bookingOutcomeDto2;

    @BeforeEach
    void setUp() {
        booker = new User(1L, "user1@mail.ru", "user1");
        owner = new User(2L, "user2@mail.ru", "user2");
        bookerDto = new UserDto(1L, "user1@mail.ru", "user1");
        created = LocalDateTime.now();
        start = LocalDateTime.now();
        end = LocalDateTime.now().plusDays(1);
        request1 = new ItemRequest(1L, "request1", booker, created);
        request2 = new ItemRequest(2L, "request2", booker, created);
        item1 = new Item(1L, "item1", "description1", Status.AVAILABLE, owner, request1);
        item2 = new Item(2L, "item2", "description2", Status.UNAVAILABLE, owner, request2);
        booking1 = new Booking(1L, start, end, item1, booker, BookingStatus.WAITING);
        booking2 = new Booking(2L, start, end, item2, booker, BookingStatus.WAITING);
        bookingOutcomeDto = new BookingOutcomeDto(1L, start, end, item1, booker, booking1.getBookingStatus().name());
        bookingOutcomeDto2 = new BookingOutcomeDto(2L, start, end, item2, booker, booking2.getBookingStatus().name());
    }

    @Test
    @DisplayName("Успешное добавление запроса")
    void addNewRequest_isSuccess() {
        when(mockUserService.getUserById(anyLong())).thenReturn(bookerDto);
        when(mockItemRequestRepository.save(any())).thenReturn(request1);

        ItemRequest result = itemRequestService.addNewRequest(booker.getId(), "request1");

        assertEquals(request1, result);
    }

    @Test
    @DisplayName("Неуспешное добавление запроса - пользователь не найден")
    void addNewRequest_isNotSuccess() {
        User newUser = new User(1L, "new@email", "newName");
        ItemRequest itemRequest = new ItemRequest(1L, "request1", newUser, LocalDateTime.now());
        when(mockUserService.getUserById(anyLong())).thenReturn(null);

        final DataNotFoundException exception = Assertions.assertThrows(
                DataNotFoundException.class,
                () -> itemRequestService.addNewRequest(newUser.getId(), itemRequest.getDescription()));

        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    @DisplayName("Неуспешное добавление запроса - описание не найдено")
    void addNewRequest_isNotSuccess_descriptionNotFound() {
        ItemRequest itemRequest = new ItemRequest(1L, null, booker, LocalDateTime.now());
        when(mockUserService.getUserById(anyLong())).thenReturn(bookerDto);

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemRequestService.addNewRequest(1L, itemRequest.getDescription()));

        assertEquals("Описание не может быть пустым", exception.getMessage());
    }


    @Test
    @DisplayName("Успешное получение запроса по пользователю и requestId")
    void getRequestById_isSuccess() {
        when(mockUserService.getUserById(anyLong())).thenReturn(bookerDto);
        when(mockItemRequestRepository.findById(anyLong())).thenReturn(Optional.of(request1));

        ItemRequest result = itemRequestService.getRequestById(bookerDto.getId(), request1.getId());

        assertEquals(request1, result);
    }

    @Test
    @DisplayName("Ошибка при получении запроса по пользователю и requestId")
    void getRequestById_isNotSuccess() {
        User newUser = new User(1L, "new@email", "newName");
        ItemRequest itemRequest = new ItemRequest(1L, "request1", newUser, LocalDateTime.now());
        when(mockUserService.getUserById(anyLong())).thenReturn(null);

        final DataNotFoundException exception = Assertions.assertThrows(
                DataNotFoundException.class,
                () -> itemRequestService.getRequestById(newUser.getId(), itemRequest.getId()));

        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    @DisplayName("Ошибка при получении запроса - неверный requestId")
    void getRequestById_isNotSuccess_requestNotFound() {
        ItemRequest itemRequest = new ItemRequest(1L, "request1", booker, LocalDateTime.now());
        when(mockUserService.getUserById(anyLong())).thenReturn(bookerDto);

        final DataNotFoundException exception = Assertions.assertThrows(
                DataNotFoundException.class,
                () -> itemRequestService.getRequestById(booker.getId(), itemRequest.getId()));

        assertEquals("Не найден запрос с данным id", exception.getMessage());
    }

    @Test
    @DisplayName("Успешное получение списка запросов пользователя")
    void getRequests() {
        List<ItemRequest> requestList = List.of(request1, request2);
        when(mockUserService.getUserById(anyLong())).thenReturn(bookerDto);
        when(mockItemRequestRepository.findAllByUserId(anyLong())).thenReturn(requestList);

        List<ItemRequest> result = itemRequestService.getRequests(bookerDto.getId());

        assertEquals(requestList, result);
    }

    @Test
    @DisplayName("Ошибка получения списка запросов пользователя")
    void getRequests_isNotSuccess() {
        when(mockUserService.getUserById(anyLong())).thenReturn(null);

        final DataNotFoundException exception = Assertions.assertThrows(
                DataNotFoundException.class,
                () -> itemRequestService.getRequests(bookerDto.getId()));

        assertEquals("Пользователь не найден.", exception.getMessage());
    }

    @Test
    @DisplayName("Успешное получение списка запросов")
    void getAllRequests() {
        List<ItemRequest> itemRequests = List.of(request1, request2);
        Page mockPage = Mockito.mock(Page.class);
        when(mockPage.getContent()).thenReturn(itemRequests);
        when(mockUserService.getUserById(anyLong())).thenReturn(bookerDto);
        when(mockItemRequestRepository.findAll(anyLong(), any())).thenReturn(mockPage);

        List<ItemRequest> result = itemRequestService.getAllRequests(booker.getId(), 0, 10);

        assertEquals(itemRequests, result);
    }

    @Test
    @DisplayName("Неудачное получение списка запросов")
    void getAllRequests_isSuccess() {
        when(mockUserService.getUserById(anyLong())).thenReturn(null);

        final DataNotFoundException exception = Assertions.assertThrows(
                DataNotFoundException.class,
                () -> itemRequestService.getAllRequests(booker.getId(), 0, 10));

        assertEquals("Пользователь не найден.", exception.getMessage());
    }
}