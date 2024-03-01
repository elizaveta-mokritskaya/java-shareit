package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingOutcomeDto;
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

    private User booker = new User(1L, "user1@mail.ru", "user1");
    private User owner = new User(2L, "user2@mail.ru", "user2");
    private UserDto bookerDto = new UserDto(1L, "user1@mail.ru", "user1");
    private UserDto ownerDto = new UserDto(2L, "user2@mail.ru", "user2");
    private LocalDateTime created = LocalDateTime.now();
    private LocalDateTime start = LocalDateTime.now();
    private LocalDateTime end = LocalDateTime.now().plusDays(1);
    private ItemRequest request1 = new ItemRequest(1L, "запрос1", booker, created);
    private ItemRequest request2 = new ItemRequest(2L, "запрос2", booker, created);
    private Item item1 = new Item(1L, "item1", "description1", Status.AVAILABLE, owner, request1);
    private Item item2 = new Item(2L, "item2", "description2", Status.UNAVAILABLE, owner, request2);
    private Booking booking1 = new Booking(1L, start, end, item1, booker, BookingStatus.WAITING);
    private Booking booking2 = new Booking(2L, start, end, item1, owner, BookingStatus.WAITING);

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

        Assertions.assertEquals(bookingDto, actual);
    }

    @Test
    @DisplayName("Хозяин пытается забронировать вещь - исключение")
    void saveNewBookingTest_isNotSuccess() {
        when(mockUserService.getUserById(anyLong())).thenReturn(ownerDto);
        when(mockItemService.getItemById(anyLong(),anyLong())).thenReturn(item1);

        final DataNotFoundException exception = Assertions.assertThrows(
                DataNotFoundException.class,
                () -> bookingService.saveNewBooking(start, end, item1.getId(), ownerDto.getId()));

        Assertions.assertEquals("Вещь не может быть забронирована её владельцем.", exception.getMessage());
    }

    @Test
    @DisplayName("Попытка забронировать занятую вещь")
    void saveNewBookingTest_ItemNotAvailable() {
        when(mockUserService.getUserById(anyLong())).thenReturn(bookerDto);
        when(mockItemService.getItemById(anyLong(),anyLong())).thenReturn(item2);

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.saveNewBooking(start, end, item2.getId(), bookerDto.getId()));

        Assertions.assertEquals("Вещь уже забронирована.", exception.getMessage());
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

        Assertions.assertEquals("Время начала бронирования не может быть позже окончания.", exception.getMessage());
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

        Assertions.assertEquals("Время начала бронирования не может быть позже окончания.", exception.getMessage());
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

        Assertions.assertEquals(BookingMapper.toBookingDto(bookingUpdate), actual);
    }

    @Test
    @DisplayName("Успешное обновление брони")
    void updateBooking__() {
    }

    @Test
    @DisplayName("Успешное обновление брони")
    void updateBooking___() {
    }

    @Test
    void getBookingById() {
    }

    @Test
    void getBookingsByUser() {
    }

    @Test
    void getBookingsByOwner() {
    }

    @Test
    void getBookingsForUser() {
    }
}