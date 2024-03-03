package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingIncomeDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingOutcomeDto;
import ru.practicum.shareit.booking.dto.SearchStatus;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.Status;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private UserService mockUserService;
    @Mock
    private ItemService mockItemService;
    @Mock
    private BookingRepository mockBookingRepository;

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
    BookingOutcomeDto bookingOutcomeDto;
    BookingOutcomeDto bookingOutcomeDto2;

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
    }

    @Test
    @DisplayName("Сохраняет бронирование успешно")
    void saveNewBookingTest_isSuccess() {
        BookingOutcomeDto bookingDto = new BookingOutcomeDto(
                1L, start, end, item1, booker, BookingStatus.WAITING.name()
        );
        when(mockUserService.getUserById(anyLong())).thenReturn(bookerDto);
        when(mockItemService.getItemById(anyLong(),anyLong())).thenReturn(item1);
        when(mockBookingRepository.save(any())).thenReturn(booking1);

        BookingOutcomeDto actual = bookingService.saveNewBooking(start, end, item1.getId(), bookerDto.getId());

        assertEquals(bookingDto, actual);
    }

    @Test
    @DisplayName("Хозяин пытается забронировать вещь - исключение")
    void saveNewBookingTest_isNotSuccess() {
        when(mockUserService.getUserById(anyLong())).thenReturn(ownerDto);
        when(mockItemService.getItemById(anyLong(),anyLong())).thenReturn(item1);

        final DataNotFoundException exception = Assertions.assertThrows(
                DataNotFoundException.class,
                () -> bookingService.saveNewBooking(start, end, item1.getId(), ownerDto.getId()));

        assertEquals("Вещь не может быть забронирована её владельцем.", exception.getMessage());
    }

    @Test
    @DisplayName("Попытка забронировать занятую вещь")
    void saveNewBookingTest_ItemNotAvailable() {
        when(mockUserService.getUserById(anyLong())).thenReturn(bookerDto);
        when(mockItemService.getItemById(anyLong(),anyLong())).thenReturn(item2);

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.saveNewBooking(start, end, item2.getId(), bookerDto.getId()));

        assertEquals("Вещь уже забронирована.", exception.getMessage());
    }


    @Test
    @DisplayName("Бронирование с некорректными датами")
    void saveNewBookingTest_StartIsAfterEnd() {
        LocalDateTime startTest = LocalDateTime.of(2024, 3, 3, 12, 0, 0);
        LocalDateTime endTest = LocalDateTime.of(2024, 3, 1, 12, 0, 0);

        when(mockUserService.getUserById(anyLong())).thenReturn(bookerDto);
        when(mockItemService.getItemById(anyLong(),anyLong())).thenReturn(item1);
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.saveNewBooking(startTest, endTest, item1.getId(), bookerDto.getId()));

        assertEquals("Время начала бронирования не может быть позже окончания.", exception.getMessage());
    }
    @Test
    @DisplayName("Бронирование с одинаковыми датами")
    void saveNewBookingTest_StartIsEqualsEnd() {
        LocalDateTime startTest = LocalDateTime.of(2024, 3, 3, 12, 0, 0);
        LocalDateTime endTest = LocalDateTime.of(2024, 3, 3, 12, 0, 0);

        when(mockUserService.getUserById(anyLong())).thenReturn(bookerDto);
        when(mockItemService.getItemById(anyLong(),anyLong())).thenReturn(item1);
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.saveNewBooking(startTest, endTest, item1.getId(), bookerDto.getId()));

        assertEquals("Время начала бронирования не может быть позже окончания.", exception.getMessage());
    }

    @Test
    @DisplayName("Успешное обновление брони")
    void updateBookingTest_isSuccess() {
        Booking oldBooking = new Booking(1L, start, end, item1, booker, BookingStatus.WAITING);
        Booking bookingUpdate = new Booking(1L, start.plusHours(2), end.plusHours(2), item1, booker, BookingStatus.WAITING);
        when(mockUserService.getUserById(anyLong())).thenReturn(bookerDto);
        when(mockBookingRepository.findById(anyLong())).thenReturn(Optional.of(oldBooking));
        when(mockItemService.getItems(anyLong())).thenReturn(List.of(oldBooking.getItem()));
        when(mockBookingRepository.save(any())).thenReturn(bookingUpdate);

        BookingOutcomeDto actual = bookingService.updateBooking(oldBooking.getId(), owner.getId(), true);

        assertEquals(BookingMapper.toBookingDto(bookingUpdate), actual);
    }

    @Test
    @DisplayName("Обновление бронирования по несуществующему пользователю")
    void updateBookingTest_UserNotFound() {
        Booking oldBooking = new Booking(1L, start, end, item1, booker, BookingStatus.WAITING);

        when(mockUserService.getUserById(anyLong())).thenReturn(null);

        final DataNotFoundException exception = Assertions.assertThrows(
                DataNotFoundException.class,
                () -> bookingService.updateBooking(oldBooking.getId(), booker.getId(), true));

        assertEquals("Пользователь не найден.", exception.getMessage());
    }

    @Test
    @DisplayName("Попытка обновить бронирование, время которого истекло")
    void updateBooking_TimeIsUp() {
        LocalDateTime startTest = LocalDateTime.now().minusDays(1);
        LocalDateTime endTest = LocalDateTime.now().minusHours(3);
        User userTest = new User(3L, "test@mail.ru", "testUser");
        Item itemTest = new Item(3L, "itemTest", "descriptionTest", Status.AVAILABLE, userTest, request1);
        Booking oldBooking = new Booking(1L, startTest, endTest, itemTest, booker, BookingStatus.WAITING);
        when(mockUserService.getUserById(anyLong())).thenReturn(bookerDto);
        when(mockBookingRepository.findById(anyLong())).thenReturn(Optional.of(oldBooking));

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.updateBooking(oldBooking.getId(), userTest.getId(), true));

        assertEquals("Время бронирования истекло!", exception.getMessage());
    }

    @Test
    @DisplayName("Попытка обновить чужое бронирование")
    void updateBooking_hasBeenCancelled() {
        User userTest = new User(3L, "test@mail.ru", "testUser");
        Item itemTest = new Item(3L, "itemTest", "descriptionTest", Status.AVAILABLE, userTest, request1);
        Booking oldBooking = new Booking(1L, start, end, itemTest, booker, BookingStatus.WAITING);
        when(mockUserService.getUserById(anyLong())).thenReturn(bookerDto);
        when(mockBookingRepository.findById(anyLong())).thenReturn(Optional.of(oldBooking));

        final DataNotFoundException exception = Assertions.assertThrows(
                DataNotFoundException.class,
                () -> bookingService.updateBooking(oldBooking.getId(), booker.getId(), true));

        assertEquals("Только владелец вещи может подтвердить бронирование!", exception.getMessage());
    }

    @Test
    @DisplayName("Успешное получение бронирования по bookingId")
    void getBookingById() {
        when(mockUserService.getUserById(anyLong())).thenReturn(ownerDto);
        when(mockBookingRepository.findById(anyLong())).thenReturn(Optional.of(booking1));
        when(mockItemService.findItemsByOwnerId(anyLong())).thenReturn(List.of(item1, item2));
        BookingOutcomeDto test = BookingMapper.toBookingDto(booking1);

        BookingOutcomeDto actual = bookingService.getBookingById(owner.getId(), booking1.getId());

        assertEquals(test, actual);
    }

    @Test
    @DisplayName("Попытка найти бронирование несуществующего пользователя")
    void getBookingById_BookerNotFound() {
        when(mockUserService.getUserById(anyLong())).thenReturn(null);

        final DataNotFoundException exception = Assertions.assertThrows(
                DataNotFoundException.class,
                () -> bookingService.getBookingById(booker.getId(), booking1.getId()));

        assertEquals("Пользователь не найден.", exception.getMessage());
    }

    @Test
    @DisplayName("Попытка найти бронирование несуществующей вещи")
    void getBookingById_ItemNtFound() {
        when(mockUserService.getUserById(anyLong())).thenReturn(bookerDto);
        when(mockBookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        final DataNotFoundException exception = Assertions.assertThrows(
                DataNotFoundException.class,
                () -> bookingService.getBookingById(booker.getId(), booking1.getId()));

        assertEquals("Вещь с таким id не найдена.", exception.getMessage());
    }

    @Test
    @DisplayName("Попытка посмотреть чужое бронирование")
    void getBookingById_otherPeoplesBooking() {
        List<Item> itemListTest = mockItemService.findItemsByOwnerId(booking1.getBooker().getId());
        when(mockUserService.getUserById(anyLong())).thenReturn(bookerDto);
        when(mockBookingRepository.findById(anyLong())).thenReturn(Optional.of(booking1));
        when(mockItemService.findItemsByOwnerId(anyLong())).thenReturn(itemListTest);

        final DataNotFoundException exception = Assertions.assertThrows(
                DataNotFoundException.class,
                () -> bookingService.getBookingById(3L, 1L));

        assertEquals("Видеть данные бронирования может только владелец вещи или бронирующий ее пользователь",
                exception.getMessage());
    }

    @Test
    @DisplayName("Получение бронирования по пользователю CURRENT")
    void getBookingsByUser_CURRENT() {
        List<Booking> bookingList = List.of(booking1, booking2);
        when(mockUserService.getUserById(anyLong())).thenReturn(bookerDto);
        when(mockBookingRepository.getBookingForBookerAndStartIsBeforeAndEndAfter(
                anyLong(), any())).thenReturn(bookingList);

        List<Booking> result = bookingService.getBookingsByUser(1L, SearchStatus.CURRENT, 0, 10);

        assertEquals(bookingList, result);
    }

    @Test
    @DisplayName("Получение бронирования по пользователю PAST")
    void getBookingsByUser_PAST() {
        List<Booking> bookingList = List.of(booking1, booking2);
        when(mockUserService.getUserById(anyLong())).thenReturn(bookerDto);
        when(mockBookingRepository.getBookingForBookerAndEndBefore(
                anyLong(), any())).thenReturn(bookingList);

        List<Booking> result = bookingService.getBookingsByUser(1L, SearchStatus.PAST, 0, 10);

        assertEquals(bookingList, result);
    }
    //todo дописать по статусу бронирования все варианты

    @Test
    @DisplayName("Получение списка бронирований по хозяину")
    void getBookingsByOwner() {
        List<Booking> bookingList = List.of(booking1, booking2);
        List<BookingOutcomeDto> dtoList = List.of(bookingOutcomeDto, bookingOutcomeDto2);
        when(mockUserService.getUserById(anyLong())).thenReturn(bookerDto);
        when(mockBookingRepository.getBookingByOwnerIdAndStartIsBeforeAndEndAfter(
                anyLong(), any())).thenReturn(bookingList);

        List<BookingOutcomeDto> result = bookingService.getBookingsByOwner(2L, SearchStatus.CURRENT, 0, 10);

        assertEquals(dtoList, result);
    }
    //todo дописать по статусу бронирования все варианты

    @Test
    @DisplayName("Поиск по itemId всех пользователей")
    void getBookingsForUser() {
        List<Booking> bookingList = List.of(booking1, booking2);
        when(mockBookingRepository.findAllByItemId(anyLong())).thenReturn(bookingList);

        List<Booking> result = bookingService.getBookingsForUser(1L);

        assertEquals(bookingList,result);
    }
}