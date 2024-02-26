package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.Status;
import ru.practicum.shareit.user.dto.UserMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ItemMapper {
    public static ItemOutcomeDto toItemDto(Item item) {
        return new ItemOutcomeDto(item.getId(), item.getName(), item.getDescription(),
                item.getAvailable() == Status.AVAILABLE, UserMapper.toUserDto(item.getOwner()),
                item.getRequest() != null ? item.getRequest().getId() : null);
    }

    public static ItemOutcomeInfoDto toItemDtoWithComments(Item item, List<CommentDto> comments) {
        return new ItemOutcomeInfoDto(item.getId(), item.getName(), item.getDescription(),
                item.getAvailable() == Status.AVAILABLE, UserMapper.toUserDto(item.getOwner()),
                item.getRequest() != null ? item.getRequest().getId() : null,
                null,
                null,
                comments.isEmpty() ? new ArrayList<>() : comments);
    }

    public static ItemOutcomeInfoDto toItemInfoDto(Item item,
                                                   List<Booking> bookings,
                                                   List<CommentDto> comments) {
        Booking bookingLast = bookings.stream()
                .filter(b -> ((b.getStart().isBefore(LocalDateTime.now())) || (b.getEnd().isBefore(LocalDateTime.now()))))
                .max(Comparator.comparing(Booking::getStart)).orElse(null);
        Booking bookingNext = bookings.stream()
                .filter(b -> ((b.getStart().isAfter(LocalDateTime.now()))
                        &&
                        (!b.getStatus().equals(ru.practicum.shareit.booking.Status.REJECTED))
                        &&
                        (!b.getStatus().equals(ru.practicum.shareit.booking.Status.CANCELED))))
                .min(Comparator.comparing(Booking::getStart)).orElse(null);
        return new ItemOutcomeInfoDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable() == Status.AVAILABLE,
                UserMapper.toUserDto(item.getOwner()),
                item.getRequest() != null ? item.getRequest().getId() : null,
                bookingLast != null ? new BookingDto(
                        bookingLast.getId(),
                        bookingLast.getBooker().getId(),
                        bookingLast.getStart(),
                        bookingLast.getEnd()) : null,
                bookingNext != null ? new BookingDto(
                        bookingNext.getId(),
                        bookingNext.getBooker().getId(),
                        bookingNext.getStart(),
                        bookingNext.getEnd()) : null,
                comments.isEmpty() ? new ArrayList<>() : comments);
    }
}
