package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingOutcomeDto;
import ru.practicum.shareit.booking.dto.SearchStatus;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public BookingOutcomeDto saveNewBooking(LocalDateTime start, LocalDateTime end, Long itemId, Long userId) {
        User booker = UserMapper.toUser(userService.getUserById(userId));
        BookingStatus bookingStatus = BookingStatus.WAITING;
        Item item = itemService.getItemById(userId, itemId);
        if (item.getOwner().getId().equals(booker.getId())) {
            throw new DataNotFoundException("Вещь не может быть забронирована её владельцем.");
        }
        if (item.getAvailable() != ru.practicum.shareit.item.model.Status.AVAILABLE) {
            throw new ValidationException("Вещь уже забронирована.");
        }
        if (start.isAfter(end) || start.equals(end)) {
            throw new ValidationException("Время начала бронирования не может быть позже окончания.");
        }
        return BookingMapper.toBookingDto(repository.save(new Booking(null, start, end, item, booker, bookingStatus)));
    }

    @Override
    public BookingOutcomeDto updateBooking(long bookingId, Long userId, Boolean approved) {
        UserDto booker = userService.getUserById(userId);
        if (booker == null) {
            throw new DataNotFoundException("Пользователь не найден.");
        }
        Booking booking = repository.findById(bookingId).orElseThrow(() -> new DataNotFoundException("Бронирование не найдено!"));
        Long itemIdFromBooking = booking.getItem().getId();
        boolean itemValid = itemService.getItems(userId).stream().anyMatch(item -> item.getId().equals(itemIdFromBooking));
        if (booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Время бронирования истекло!");
        }
        if (booking.getBooker().getId().equals(userId)) {
            if (!approved) {
                booking.setBookingStatus(BookingStatus.CANCELED);
                log.info("Бронирование отменено.");
            } else {
                throw new DataNotFoundException("Только владелец вещи может подтвердить бронирование!");
            }
        } else if ((itemValid) && (!booking.getBookingStatus().equals(BookingStatus.CANCELED))) {
            if (!(booking.getBookingStatus().equals(BookingStatus.WAITING))) {
                throw new ValidationException("Не новое бронирование, решение уже принято");
            }
            if (approved) {
                booking.setBookingStatus(BookingStatus.APPROVED);
                log.info("Пользователь с ID={} подтвердил бронирование с ID={}", userId, bookingId);
            } else {
                booking.setBookingStatus(BookingStatus.REJECTED);
                log.info("Пользователь с ID={} отклонил бронирование с ID={}", userId, bookingId);
            }
        } else {
            if (booking.getBookingStatus().equals(BookingStatus.CANCELED)) {
                throw new ValidationException("Бронирование было отменено!");
            } else {
                throw new ValidationException("Подтвердить бронирование может только владелец вещи!");
            }
        }
        return BookingMapper.toBookingDto(repository.save(booking));
    }

    @Override
    public BookingOutcomeDto getBookingById(Long userId, long bookingId) {
        if (userService.getUserById(userId) == null) {
            throw new DataNotFoundException("Пользователь не найден.");
        }
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new DataNotFoundException("Вещь с таким id не найдена."));
        List<Item> items = itemService.findItemsByOwnerId(userId);
        boolean isItemOwner = items.stream().anyMatch(
                item -> Objects.equals(item.getId(), booking.getItem().getId())
        );
        if (booking.getBooker().getId().equals(userId) || (isItemOwner)) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new DataNotFoundException("Видеть данные бронирования может только владелец вещи" +
                    " или бронирующий ее пользователь");
        }
    }

    @Override
    public List<Booking> getBookingsByUser(Long userId, SearchStatus state, int from, int size) {
        if (userService.getUserById(userId) == null) {
            throw new DataNotFoundException("Пользователь не найден.");
        }
        List<Booking> bookings;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        switch (state) {
            case CURRENT:
                bookings = repository.getBookingForBookerAndStartIsBeforeAndEndAfter(userId, LocalDateTime.now());
                break;
            case PAST:
                bookings = repository.getBookingForBookerAndEndBefore(userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = repository.getBookingForBookerIdAndStartAfter(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = repository.getBookingForBookerAndStatus(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = repository.getBookingForBookerAndStatus(userId, BookingStatus.REJECTED);
                break;
            default:
                bookings = repository.findAllByBookerId(userId,
                                PageRequest.of(from, size, sort))
                        .getContent();
        }
        return bookings;
    }

    @Override
    public List<BookingOutcomeDto> getBookingsByOwner(Long userId, SearchStatus state, int from, int size) {
        if (userService.getUserById(userId) == null) {
            throw new DataNotFoundException("Пользователь не найден.");
        }
        List<Booking> bookings;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        switch (state) {
            case CURRENT:
                bookings = repository.getBookingByOwnerIdAndStartIsBeforeAndEndAfter(userId, LocalDateTime.now());
                break;
            case PAST:
                bookings = repository.getBookingByOwnerIdAndEndBefore(userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = repository.getBookingByOwnerIdAndStartAfter(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = repository.getBookingByOwner_IdAndStatus(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = repository.getBookingByOwner_IdAndStatus(userId, BookingStatus.REJECTED);
                break;
            default:
                bookings = repository.findAllByOwnerId(userId,
                                PageRequest.of(from, size, sort))
                        .getContent();
        }
        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public List<Booking> getBookingsForUser(Long itemId) {
        return repository.findAllByItemId(itemId);
    }
}
