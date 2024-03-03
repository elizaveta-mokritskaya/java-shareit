package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.Status;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@DataJpaTest
class ItemRepositoryTest {
    private final TestEntityManager entityManager;
    private final ItemRepository itemRepository;

    private Pageable pageable =  PageRequest.of(0, 10);

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
                .available(Status.AVAILABLE)
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

    @Test
    @DisplayName("Получение списка вещей по описанию")
    void getItemsByDescription() {
        item2.setDescription(item1.getDescription());
        entityManager.persist(owner);
        entityManager.persist(booker);
        entityManager.persist(request1);
        entityManager.persist(request2);
        entityManager.persist(item1);
        entityManager.persist(item2);
        List<Item> itemList = List.of(item1, item2);

        List<Item> result = itemRepository.getItemsByDescription(item1.getDescription(), pageable).getContent();

        assertEquals(itemList, result);
    }

    @Test
    @DisplayName("Удаление по userId и itemId")
    void deleteByUserIdAndItemId() {
        entityManager.persist(owner);
        entityManager.persist(booker);
        entityManager.persist(request1);
        entityManager.persist(request2);
        entityManager.persist(item1);
        entityManager.persist(item2);
        List<Item> itemList = List.of(item2);

        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.deleteByUserIdAndItemId(item1.getId(), owner.getId());
        List<Item> result = itemRepository.getByOwnerId(item1.getOwner().getId());

        assertEquals(itemList, result);
    }

    @Test
    @DisplayName("Взять все по UserId")
    void getAllByUserId() {
        entityManager.persist(owner);
        entityManager.persist(booker);
        entityManager.persist(request1);
        entityManager.persist(request2);
        entityManager.persist(item1);
        entityManager.persist(item2);
        List<Item> itemList = List.of(item1, item2);

        List<Item> result = itemRepository.getAllByUserId(owner.getId());

        assertEquals(itemList, result);
    }

    @Test
    @DisplayName("список по UserId")
    void getByOwnerId() {
        entityManager.persist(owner);
        entityManager.persist(booker);
        entityManager.persist(request1);
        entityManager.persist(request2);
        entityManager.persist(item1);
        entityManager.persist(item2);
        List<Item> itemList = List.of(item1, item2);

        List<Item> result = itemRepository.getByOwnerId(owner.getId());

        assertEquals(itemList, result);
    }

    @Test
    @DisplayName("Список вещей в виде страниц")
    void findAllByUserIdPage() {
        entityManager.persist(owner);
        entityManager.persist(booker);
        entityManager.persist(request1);
        entityManager.persist(request2);
        entityManager.persist(item1);
        entityManager.persist(item2);
        List<Item> itemList = List.of(item1, item2);

        List<Item> result = itemRepository.findAllByUserIdPage(owner.getId(),pageable).getContent();

        assertEquals(itemList, result);
    }

    @Test

    @DisplayName("Список по requestId")
    void findAllByRequestId() {
        item2.setRequest(request1);
        entityManager.persist(owner);
        entityManager.persist(booker);
        entityManager.persist(request1);
        entityManager.persist(request2);
        entityManager.persist(item1);
        entityManager.persist(item2);
        List<Item> itemList = List.of(item1, item2);

        List<Item> result = itemRepository.findAllByRequestId(request1.getId());

        assertEquals(itemList, result);
    }
}