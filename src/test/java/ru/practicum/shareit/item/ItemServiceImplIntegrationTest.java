package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingIncomeDto;
import ru.practicum.shareit.booking.dto.BookingOutcomeDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.Status;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Sql(scripts = {"file:src/main/resources/schema.sql"})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ItemServiceImplIntegrationTest {
    private final BookingServiceImpl bookingService;
    private final UserService userService;
    private final ItemService itemService;
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
    void addNewItem() {
        userRepository.save(owner);
        Item expected = itemService.addNewItem(owner.getId(), "item1", "description1", true, null);

        Item actual = itemService.getItemById(owner.getId(), expected.getId());
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void updateItem() {
        userRepository.save(booker);
        userRepository.save(owner);
        itemRequestRepository.save(request1);
        item1.setOwner(owner);
        itemRepository.save(item1);
        Item expected = itemService.updateItem(owner.getId(), item1.getId(), "newName", "newDescription", true);

        Item actual = itemService.getItemById(owner.getId(), expected.getId());
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}