package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingIncomeDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingOutcomeDto;
import ru.practicum.shareit.booking.dto.SearchStatus;
import ru.practicum.shareit.exception.NoEnumValueArgumentException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-item-requests.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping()
    public BookingOutcomeDto saveNewBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @Valid @RequestBody BookingIncomeDto dto) {
        log.info("Получен запрос на добавление нового бронирования '{}' пользователем '{}'", dto, userId);
        return bookingService.saveNewBooking(dto.getStart(), dto.getEnd(), dto.getItemId(), userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingOutcomeDto updateBooking(@PathVariable("bookingId") long bookingId,
                                           @RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestParam Boolean approved) {
        log.info("Получен запрос на обновление статуса бронирования с ID={}", bookingId);
        return bookingService.updateBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingOutcomeDto getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @PathVariable("bookingId") long bookingId) {
        log.info("Получен запрос на просмотр информации о бронировании с ID={}", bookingId);
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping()
    public List<BookingOutcomeDto> getBookingsByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(name = "state", defaultValue = "ALL") String stateParam) {
        log.info("Получен запрос на получение " +
                "списка бронирований пользователя с ID={} с параметром STATE={}", userId, stateParam);
        SearchStatus state;
        try {
            state = SearchStatus.valueOf(stateParam);
        } catch (IllegalArgumentException e) {
            throw new NoEnumValueArgumentException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookingService.getBookingsByUser(userId, state).stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingOutcomeDto> getBookingsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @RequestParam(name = "state", defaultValue = "ALL") String state) {
        log.info("Получен запрос на получение " +
                "списка бронирований владельцем вещи с ID={} с параметром STATE={}", userId, state);
        SearchStatus status;
        try {
            status = SearchStatus.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new NoEnumValueArgumentException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookingService.getBookingsByOwner(userId, status);
    }
}