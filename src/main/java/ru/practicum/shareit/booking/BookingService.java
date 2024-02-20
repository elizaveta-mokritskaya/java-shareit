package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingOutcomeDto;
import ru.practicum.shareit.booking.dto.SearchStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {
    BookingOutcomeDto saveNewBooking(LocalDateTime start, LocalDateTime end, Long itemId, Long userId);

    BookingOutcomeDto updateBooking(long bookingId, Long userId, Boolean approved);

    BookingOutcomeDto getBookingById(Long userId, long bookingId);

    List<Booking> getBookingsByUser(Long userId, SearchStatus state);

    List<BookingOutcomeDto> getBookingsByOwner(Long userId, SearchStatus state);

    List<Booking> getBookingsForUser(Long itemId);
}
