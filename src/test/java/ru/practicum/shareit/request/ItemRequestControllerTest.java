package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemOutcomeDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.Status;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestIncomeDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {
    @InjectMocks
    ItemRequestController itemRequestController;
    @Mock
    ItemRequestService itemRequestService;
    @Mock
    ItemService itemService;
    private User validUser1 = new User(1L, "user1@mail.ru", "user1");
    private User validUser2 = new User(2L, "user2@mail.ru", "user2");
    private UserDto validUserDto1 = new UserDto(1L, "user1@mail.ru", "user1");

    private LocalDateTime created = LocalDateTime.of(2024, 2, 29, 12, 0, 0);
    private ItemRequest request1 = new ItemRequest(1L, "запрос1", validUser1, created);
    private ItemRequest request2 = new ItemRequest(2L, "запрос2", validUser1, created);

    @Test
    @DisplayName("Вызывается сервис")
    void addRequest_thenUseService() {
        Mockito.when(itemRequestService.addNewRequest(1L, "запрос1")).thenReturn(request1);

        itemRequestController.addRequest(1L, new ItemRequestIncomeDto("запрос1"));

        verify(itemRequestService).addNewRequest(1L, "запрос1");
    }

    @Test
    @DisplayName("Возвращается дто_запрос")
    void addRequest_thenReturnRequestsDto() {
        Mockito.when(itemRequestService.addNewRequest(1L, "запрос1")).thenReturn(request1);

        ItemRequestDto result = itemRequestController.addRequest(1L, new ItemRequestIncomeDto("запрос1"));
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "запрос1", validUserDto1, created);

        Assertions.assertEquals(itemRequestDto, result);
    }

    @Test
    @DisplayName("Вызывается сервис")
    void getRequests_thenUseService() {
        itemRequestController.getRequests(1L);
        verify(itemRequestService).getRequests(1L);
    }

    @Test
    @DisplayName("Возвращается список дто_запросов")
    void getRequests_thenReturnItemRequestInfoDtos() {
        List<ItemRequest> itemRequests = new ArrayList<>();
        itemRequests.add(request1);
        itemRequests.add(request2);
        Mockito.when(itemRequestService.getRequests(1L)).thenReturn(itemRequests);

        List<ItemRequestInfoDto> resultRequests = itemRequestController.getRequests(1L);
        Assertions.assertEquals(2, resultRequests.size());
    }

    @Test
    @DisplayName("Вызывается сервис")
    void getAllRequests_thenUseService() {
        itemRequestController.getAllRequests(1L, 0, 10);
        verify(itemRequestService).getAllRequests(1L, 0, 10);
    }

    @Test
    @DisplayName("Возвращается список дто_запросов")
    void getAllRequests_thenReturnAllRequests() {
        List<ItemRequest> itemRequests = new ArrayList<>();
        itemRequests.add(request1);
        itemRequests.add(request2);
        int from = 0;
        int size = 10;
        Mockito.when(itemRequestService.getAllRequests(1L, from, size)).thenReturn(itemRequests);

        List<ItemRequestInfoDto> allRequests = itemRequestController.getAllRequests(1L, 0, 10);


        Assertions.assertEquals(2, allRequests.size());
    }

    @Test
    @DisplayName("Выбрасывается ошибка при запросе с параметрами from < 0 и size < 1")
    void getAllRequests_whenFromLess0AndSizeLess1_thenValidationException() {
        int from = -1;
        int size = 0;

        final ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> itemRequestController.getAllRequests(1L, from, size));
        Assertions.assertEquals("Параметры запроса неверны", exception.getMessage());
    }

    @Test
    @DisplayName("Вызывается сервис")
    void getRequestById_thenUseService() {
        Mockito.when(itemRequestService.getRequestById(any(), any())).thenReturn(request1);
        itemRequestController.getRequestById(1L, 1L);
        verify(itemRequestService).getRequestById(1L, 1L);
    }

    @Test
    @DisplayName("Возвращается дто_запрос")
    void getRequestById_thenReturnRequestDto() {
        Item item1 = new Item(1L, "item1", "--", Status.AVAILABLE, validUser2, request1);
        Item item2 = new Item(2L, "item2", "--", Status.AVAILABLE, validUser2, request1);
        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        Mockito.when(itemService.findItemsByRequestId(1L)).thenReturn(items);
        Mockito.when(itemRequestService.getRequestById(1L, 1L)).thenReturn(request1);

        ItemRequestInfoDto result = itemRequestController.getRequestById(1L, 1L);
        ItemRequestInfoDto dto = new ItemRequestInfoDto(1L, "запрос1", created,
                List.of(
                        new ItemOutcomeDto(1L, "item1", "--", true, new UserDto(2L, "user2@mail.ru", "user2"), 1L),
                        new ItemOutcomeDto(2L, "item2", "--", true, new UserDto(2L, "user2@mail.ru", "user2"), 1L)
                ));

        Assertions.assertEquals(dto, result);
    }
}