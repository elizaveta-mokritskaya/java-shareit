package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.Status;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@DataJpaTest
class ItemRequestRepositoryTest {
    private final TestEntityManager entityManager;
    private final ItemRequestRepository itemRequestRepository;

    private Pageable pageable = PageRequest.of(0, 10);

    private User booker;
    private User owner;
    private LocalDateTime created;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemRequest request1;
    private ItemRequest request2;
    private Item item1;
    private Item item2;

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
    }

    @Test
    @DisplayName("Получение списка запросов")
    void findAllByUserId() {
        entityManager.persist(owner);
        entityManager.persist(booker);
        entityManager.persist(request1);
        entityManager.persist(request2);
        entityManager.persist(item1);
        entityManager.persist(item2);
        List<ItemRequest> requestList = List.of(request1, request2);

        List<ItemRequest> result = itemRequestRepository.findAllByUserId(booker.getId());

        assertEquals(requestList, result);
    }

    @Test
    @DisplayName("Получение страницы запросов пользователя")
    void findAll() {
        entityManager.persist(owner);
        entityManager.persist(booker);
        entityManager.persist(request1);
        entityManager.persist(request2);
        entityManager.persist(item1);
        entityManager.persist(item2);
        itemRequestRepository.save(request1);
        itemRequestRepository.save(request2);
        List<ItemRequest> requestList = List.of(request1, request2);

        List<ItemRequest> result = itemRequestRepository.findAll(owner.getId(), pageable).getContent();

        assertEquals(requestList, result);
    }
}