package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;

public class BookingMapper {
    public static BookingOutcomeDto toBookingDto(Booking booking) {
        return new BookingOutcomeDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                booking.getBookingStatus().name()
        );
    }
}
