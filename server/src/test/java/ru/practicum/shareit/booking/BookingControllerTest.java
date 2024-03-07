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
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.Status;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {
    @InjectMocks
    BookingController bookingController;
    @Mock
    BookingService bookingService;

    private User booker;
    private User owner;
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
        created = LocalDateTime.now();
        start = LocalDateTime.now();
        end = LocalDateTime.now().plusDays(1);
        request1 = new ItemRequest(1L, "запрос1", booker, created);
        request2 = new ItemRequest(2L, "запрос2", booker, created);
        item1 = new Item(1L, "item1", "description1", Status.AVAILABLE, owner, request1);
        item2 = new Item(2L, "item2", "description2", Status.UNAVAILABLE, owner, request2);
        booking1 = new Booking(1L, start, end, item1, booker, BookingStatus.WAITING);
        booking2 = new Booking(2L, start, end, item2, booker, BookingStatus.WAITING);
        bookingOutcomeDto = new BookingOutcomeDto(1L, start, end, item1, booker, booking1.getBookingStatus().name());
        bookingOutcomeDto2 = new BookingOutcomeDto(1L, start, end, item2, booker, booking2.getBookingStatus().name());
    }

    @Test
    @DisplayName("Сохранение бронирования")
    void saveNewBookingTest() {
        BookingOutcomeDto testDto = new BookingOutcomeDto(
                1L, start, end, item1, booker, booking1.getBookingStatus().name());
        when(bookingService.saveNewBooking(any(), any(), anyLong(), anyLong())).thenReturn(testDto);

        bookingController.saveNewBooking(1L, new BookingIncomeDto(1L, start, end, 1L));

        verify(bookingService).saveNewBooking(start, end, 1L, 1L);
    }

    @Test
    @DisplayName("Возвращает запрос")
    void saveNewBooking_thenReturnBookingOutcomeDto() {
        BookingOutcomeDto testDto = new BookingOutcomeDto(
                1L, start, end, item1, booker, booking1.getBookingStatus().name());
        when(bookingService.saveNewBooking(any(), any(), anyLong(), anyLong())).thenReturn(testDto);

        BookingOutcomeDto result = bookingController.saveNewBooking(1L, new BookingIncomeDto(1L, start, end, 1L));

        Assertions.assertEquals(testDto, result);
    }

    @Test
    @DisplayName("Успешное обновление бронирования")
    void updateBookingTest() {
        BookingOutcomeDto oldDto = new BookingOutcomeDto(1L, start, end, item1, booker, "WAITING");
        BookingOutcomeDto updateDto = new BookingOutcomeDto(1L, start, end, item1, booker, "APPROVED");
        when(bookingService.updateBooking(1L, 1L, true)).thenReturn(updateDto);

        BookingOutcomeDto result = bookingController.updateBooking(1L, 1L, true);

        Assertions.assertEquals(updateDto, result);
    }

    @Test
    @DisplayName("Получение бронирования по bookingId")
    void getBookingById() {
        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(bookingOutcomeDto);

        BookingOutcomeDto result = bookingController.getBookingById(1L, 1L);

        assertEquals(bookingOutcomeDto, result);
    }

    @Test
    @DisplayName("Получение списка бронирований пользователя")
    void getBookingsByUser() {
        List<Booking> bookingList = List.of(booking1, booking2);
        List<BookingOutcomeDto> dtoList = bookingList.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
        int from = 0;
        int size = 10;
        SearchStatus status;
        when(bookingService.getBookingsByUser(anyLong(), any(), anyInt(), anyInt())).thenReturn(bookingList);

        List<BookingOutcomeDto> result = bookingController.getBookingsByUser(1L, "ALL", from, size);

        assertEquals(dtoList, result);
    }

    @Test
    @DisplayName("Получение списка бронирований у хозяина вещей")
    void getBookingsByOwner() {
        List<Booking> bookingList = List.of(booking1, booking2);
        List<BookingOutcomeDto> dtoList = bookingList.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
        when(bookingService.getBookingsByOwner(anyLong(), any(), anyInt(), anyInt())).thenReturn(dtoList);

        List<BookingOutcomeDto> result = bookingController.getBookingsByOwner(2L, "ALL", 0, 10);

        assertEquals(dtoList, result);
    }
}