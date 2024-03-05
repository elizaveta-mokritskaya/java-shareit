package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.dto.BookingIncomeDto;
import ru.practicum.shareit.booking.dto.BookingOutcomeDto;
import ru.practicum.shareit.booking.dto.SearchStatus;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.Status;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Sql(scripts = {"file:src/main/resources/schema.sql"})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class BookingServiceImplIntegrationTest {
    private final BookingServiceImpl bookingService;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;

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

    @BeforeEach
    void setUp() {
        booker = User.builder()
                .name("booker")
                .email("user1@mail.ru")
                .build();
        owner = User.builder()
                .name("owner")
                .email("user2@mail.ru")
                .build();

        bookerDto = new UserDto(1L, "user1@mail.ru", "user1");
        ownerDto = new UserDto(2L, "user2@mail.ru", "user2");
        created = LocalDateTime.now();
        start = LocalDateTime.now().plusHours(1);
        end = LocalDateTime.now().plusDays(10);
        request1 = ItemRequest.builder()
                .description("request1")
                .requestor(booker)
                .createdTime(created)
                .build();
        request2 = ItemRequest.builder()
                .description("request2")
                .createdTime(created)
                .requestor(booker)
                .build();

        item1 = Item.builder()
                .name("item1")
                .description("description1")
                .available(Status.AVAILABLE)
                .owner(owner)
                .request(request1)
                .build();
        item2 = Item.builder()
                .name("item2")
                .description("description2")
                .available(Status.UNAVAILABLE)
                .owner(owner)
                .request(request2)
                .build();
        booking1 = Booking.builder()
                .start(start)
                .end(end)
                .item(item1)
                .booker(booker)
                .bookingStatus(BookingStatus.WAITING)
                .build();
        booking2 = Booking.builder()
                .start(start)
                .end(end)
                .item(item2)
                .booker(booker)
                .bookingStatus(BookingStatus.WAITING)
                .build();
    }

    @AfterEach
    void clear() {
        bookingRepository.deleteAll();
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    @DisplayName("Успешное сохранение бронирования")
    void saveNewBooking() {
        userRepository.save(booker);
        userRepository.save(owner);
        itemRequestRepository.save(request1);
        itemRepository.save(item1);
        BookingOutcomeDto expected = bookingService.saveNewBooking(start, end, item1.getId(), booker.getId());

        BookingOutcomeDto actual = bookingService.getBookingById(booker.getId(), expected.getId());

        assertThat(actual).usingRecursiveComparison().ignoringFields("start", "end").isEqualTo(expected);
    }

    @Test
    @DisplayName("Владелец не может бронировать свою вещь")
    void saveNewBooking_whenOwnerEqualsBooker() {
        userRepository.save(booker);
        item1.setOwner(booker);
        itemRequestRepository.save(request1);
        itemRepository.save(item1);

        final DataNotFoundException exception = Assertions.assertThrows(
                DataNotFoundException.class,
                () -> bookingService.saveNewBooking(start, end, item1.getId(), booker.getId()));

        assertEquals("Вещь не может быть забронирована её владельцем.", exception.getMessage());
    }

    @Test
    @DisplayName("Вещь забронирована")
    void saveNewBooking_whenItemIsBooked() {
        userRepository.save(booker);
        userRepository.save(owner);
        itemRequestRepository.save(request1);
        item1.setAvailable(Status.UNAVAILABLE);
        itemRepository.save(item1);

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.saveNewBooking(start, end, item1.getId(), booker.getId()));

        assertEquals("Вещь уже забронирована.", exception.getMessage());
    }

    @Test
    @DisplayName("Время неверное")
    void saveNewBooking_whenStartAfterEnd() {
        userRepository.save(booker);
        userRepository.save(owner);
        itemRequestRepository.save(request1);
        itemRepository.save(item1);
        start = end.plusDays(1);

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.saveNewBooking(start, end, item1.getId(), booker.getId()));

        assertEquals("Время начала бронирования не может быть позже окончания.", exception.getMessage());
    }

    @Test
    @DisplayName("Обновление")
    void updateBooking() {
        userRepository.save(booker);
        userRepository.save(owner);
        itemRequestRepository.save(request1);
        itemRepository.save(item1);
        bookingRepository.save(booking1);
        BookingOutcomeDto expected = bookingService.updateBooking(booking1.getId(), booker.getId(), false);

        BookingOutcomeDto actual = bookingService.getBookingById(booking1.getId(), booker.getId());

        assertThat(actual).usingRecursiveComparison().ignoringFields("start", "end").isEqualTo(expected);
    }

    @Test
    @DisplayName("Бронирование по пользователю")
    void getBookingsByUser() {
        userRepository.save(booker);
        userRepository.save(owner);
        itemRequestRepository.save(request1);
        itemRepository.save(item1);
        bookingRepository.save(booking1);

        List<Booking> actual = bookingService.getBookingsByUser(booker.getId(), SearchStatus.WAITING, 0, 10);

        assertThat(actual).usingRecursiveComparison().ignoringFields("start", "end", "item.request.createdTime").isEqualTo(List.of(booking1));
    }

    @Test
    @DisplayName("Бронирование по владельцу")
    void getBookingsByOwner() {
        userRepository.save(booker);
        userRepository.save(owner);
        itemRequestRepository.save(request1);
        itemRepository.save(item1);
        bookingRepository.save(booking1);

        List<BookingOutcomeDto> actual = bookingService.getBookingsByOwner(owner.getId(), SearchStatus.WAITING, 0, 10);

        BookingOutcomeDto expected = bookingService.getBookingById(booker.getId(), booking1.getId());

        assertThat(List.of(expected)).usingRecursiveComparison().ignoringFields("start", "end", "item.request.createdTime")
                .isEqualTo(actual);
    }
}