package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingIncomeDto;
import ru.practicum.shareit.booking.dto.BookingOutcomeDto;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.Status;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserService userService;
    @Mock
    private ItemRequestService itemRequestService;

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
        booker = new User(1L, "user1@mail.ru", "user1");
        owner = new User(2L, "user2@mail.ru", "user2");
        bookerDto = new UserDto(1L, "user1@mail.ru", "user1");
        ownerDto = new UserDto(2L, "user2@mail.ru", "user2");
        created = LocalDateTime.now();
        start = LocalDateTime.now();
        end = LocalDateTime.now().plusDays(1);
        request1 = new ItemRequest(1L, "запрос1", booker, created);
        request2 = new ItemRequest(2L, "запрос2", booker, created);
        item1 = new Item(1L, "item1", "description1", Status.AVAILABLE, owner, request1);
        item2 = new Item(2L, "item2", "description2", Status.UNAVAILABLE, owner, request2);
        booking1 = new Booking(1L, start, end, item1, booker, BookingStatus.WAITING);
        booking2 = new Booking(2L, start, end, item2, booker, BookingStatus.WAITING);
        bookingIncomeDto = new BookingIncomeDto(1L, start, end, 1L);
        bookingOutcomeDto = new BookingOutcomeDto(1L, start, end, item1, booker, booking1.getBookingStatus().name());
        bookingOutcomeDto2 = new BookingOutcomeDto(2L, start, end, item2, booker, booking2.getBookingStatus().name());
    }

    @Test
    @DisplayName("получение списка вещей пользователя")
    void getItems() {
        List<Item> itemList = List.of(item1, item2);
        when(itemRepository.getAllByUserId(anyLong())).thenReturn(itemList);

        List<Item> result = itemService.getItems(owner.getId());

        assertEquals(itemList, result);
    }

    @Test
    @DisplayName("Страница вещей пользователя")
    void getItemsToPage() {
        List<Item> itemList = List.of(item1, item2);
        Page mockPage = mock(Page.class);
        when(mockPage.getContent()).thenReturn(itemList);
        when(itemRepository.findAllByUserIdPage(anyLong(), any())).thenReturn(mockPage);

        List<Item> result = itemService.getItemsToPage(owner.getId(), 0, 10);
        assertEquals(itemList, result);
    }

    @Test
    @DisplayName("Успешное создание карточки вещи")
    void addNewItem() {
        when(userService.getUserById(anyLong())).thenReturn(ownerDto);
        when(itemRepository.save(any())).thenReturn(item1);

        Item result = itemService.addNewItem(owner.getId(), "item1", "description1", true, request1.getId());

        assertEquals(item1, result);
    }

    @Test
    @DisplayName("При значении available = false вернуть вещь со статусом UNAVAILABLE")
    void addNewItem_whenAvailableIsFalse_thenReturnItemIsUNAVAILABLE() {
        Boolean available = false;
        Item itemUnavailable = new Item(1L, "item1", "description1", Status.UNAVAILABLE, owner, request1);
        when(userService.getUserById(1L)).thenReturn(ownerDto);
        when(itemRequestService.getRequestById(1L, 1L)).thenReturn(request1);
        when(itemRepository.save(any())).thenReturn(itemUnavailable);

        Item resultItem = itemService.addNewItem(1L, "item1", "description1", available, 1L);

        Assertions.assertEquals(itemUnavailable, resultItem);
    }

    @Test
    @DisplayName("Успешное обновление вещи")
    void updateItem_isSuccess() {
        when(itemRepository.getById(1L)).thenReturn(item1);
        when(itemRepository.save(item1)).thenReturn(item1);

        Item result = itemService.updateItem(owner.getId(), 1L, "item1", "description1", true);

        assertEquals(item1, result);
    }

    @Test
    @DisplayName("Неуспешная попытка обновить несуществующую вещь")
    void updateItem_isNotSuccess() {
        when(itemRepository.getById(10L)).thenReturn(null);

        final DataNotFoundException exception = Assertions.assertThrows(
                DataNotFoundException.class,
                () -> itemService.updateItem(owner.getId(), 10L, "item1", "description1", true));

        assertEquals("По заданному Id нет предмета", exception.getMessage());
    }

    @Test
    @DisplayName("Неуспешная попытка обновить чужую вещь")
    void updateItem_isNotSuccess_Exception() {
        when(itemRepository.getById(anyLong())).thenReturn(item1);

        final DataNotFoundException exception = Assertions.assertThrows(
                DataNotFoundException.class,
                () -> itemService.updateItem(10L, item1.getId(), "item1", "description1", true));

        assertEquals("Пользователь с заданным Id не является владельцем", exception.getMessage());
    }

    @Test
    @DisplayName("Обновление при значении available = false: вернуть вещь со статусом UNAVAILABLE")
    void updateItem_isSuccess_StatusUnavailable() {
        when(itemRepository.getById(anyLong())).thenReturn(item2);
        when(itemRepository.save(item2)).thenReturn(item2);

        Item result = itemService.updateItem(owner.getId(), 2L, "item2", "description2", false);

        assertEquals(item2, result);
    }

    @Test
    @DisplayName("Успешное удаление вещи")
    void deleteItem() {
        itemService.deleteItem(2L, 1L);

        verify(itemRepository).deleteByUserIdAndItemId(2L, 1L);
    }

    @Test
    @DisplayName("Успешное получение вещи")
    void getItemById() {
        when(userService.getUserById(anyLong())).thenReturn(ownerDto);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));

        Item result = itemService.getItemById(owner.getId(), item1.getId());

        assertEquals(item1, result);
    }

    @Test
    @DisplayName("Неудачная попытка получить вещь несуществующего пользователя")
    void getItemById_UserNotFound() {
        when(userService.getUserById(anyLong())).thenReturn(null);

        final DataNotFoundException exception = Assertions.assertThrows(
                DataNotFoundException.class,
                () -> itemService.getItemById(10L, item1.getId()));

        assertEquals("Пользователь не найден.", exception.getMessage());
    }

    @Test
    @DisplayName("Неудачная попытка получить несуществующую вещь")
    void getItemById_ItemNotFound() {
        when(userService.getUserById(anyLong())).thenReturn(ownerDto);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        final DataNotFoundException exception = Assertions.assertThrows(
                DataNotFoundException.class,
                () -> itemService.getItemById(owner.getId(), 10L));

        assertEquals("Вещь с таким id не найдена.", exception.getMessage());
    }

    @Test
    @DisplayName("Получение списка вещей по описанию")
    void getItemsByDescription() {
        item2.setDescription("description1");
        Page mockPage = mock(Page.class);
        when(itemRepository.getItemsByDescription(any(), any())).thenReturn(mockPage);
        List itemList = mockPage.toList();

        List<Item> result = itemService.getItemsByDescription("description1", 0, 10);

        assertEquals(itemList, result);
    }

    @Test
    @DisplayName("Получение пустого списка вещей по пустому описанию")
    void getItemsByDescription_isEmpty() {
        String description = "";

        List<Item> result = itemService.getItemsByDescription("", 0, 10);

        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Проверка, является ли пользователь хозяином данной вещи")
    void userIsOwnerOfItem() {
        when(userService.getUserById(anyLong())).thenReturn(ownerDto);
        when(itemRepository.getReferenceById(item1.getId())).thenReturn(item1);

        boolean result = itemService.userIsOwnerOfItem(2L, 1L);
        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("Получение списка вещей по хозяину")
    void findItemsByOwnerId() {
        List<Item> itemList = List.of(item1, item2);
        when(userService.getUserById(anyLong())).thenReturn(ownerDto);
        when(itemRepository.getByOwnerId(anyLong())).thenReturn(itemList);

        List<Item> result = itemService.findItemsByOwnerId(owner.getId());

        assertEquals(itemList, result);
    }

    @Test
    @DisplayName("Ошибка при получении списка вещей по несуществующему хозяину")
    void findItemsByOwnerId_UserNotFound() {
        when(userService.getUserById(anyLong())).thenReturn(null);

        final DataNotFoundException exception = Assertions.assertThrows(
                DataNotFoundException.class,
                () -> itemService.findItemsByOwnerId(10L));

        assertEquals("Владелец вещи не найден", exception.getMessage());
    }

    @Test
    @DisplayName("Список вещей по requestId")
    void findItemsByRequestId() {
        item2.setRequest(request1);
        List<Item> itemList = List.of(item1, item2);
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(itemList);

        List<Item> reult = itemService.findItemsByRequestId(request1.getId());
        assertEquals(itemList, reult);
    }
}