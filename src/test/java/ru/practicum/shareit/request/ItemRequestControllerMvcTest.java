package ru.practicum.shareit.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.Status;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerMvcTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    ItemRequestService itemRequestService;
    @MockBean
    ItemService itemService;
    private User userToCheck1 = new User(1L, "user1@mail.ru", "user1");
    private User userToCheck2 = new User(2L, "user2@mail.ru", "user2");
    LocalDateTime created = LocalDateTime.of(2024, 3, 1, 12, 0, 0);
    private ItemRequest request1 = new ItemRequest(1L, "запрос1", userToCheck1, created);
    private ItemRequest request2 = new ItemRequest(2L, "запрос2", userToCheck1, created);
    private ItemRequest request3 = new ItemRequest(3L, "запрос3", userToCheck2, created);
    private ItemRequest request4 = new ItemRequest(4L, "запрос4", userToCheck2, created);

    @Test
    @DisplayName("Возвращает ItemRequestDto")
    void addRequestTest_returnItemRequestDto() throws Exception {
        Mockito.when(this.itemRequestService.addNewRequest(anyLong(), any())).thenReturn(request1);

        mockMvc.perform(
                        post("/requests")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1L)
                                .content("{\n" +
                                        "  \"description\": \"Хотел бы воспользоваться щёткой для обуви\"\n" +
                                        "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("запрос1"))
                .andExpect(jsonPath("$.requestor.id").value(1))
                .andExpect(jsonPath("$.requestor.email").value("user1@mail.ru"))
                .andExpect(jsonPath("$.requestor.name").value("user1"))
                .andExpect(jsonPath("$.created").value("2024-03-01T12:00:00"));
    }

    @Test
    @DisplayName("Возвращает список запросов пользователя")
    void getRequests_returnAllRequestsByUserId() throws Exception {
        List<ItemRequest> itemRequests = new ArrayList<>();
        itemRequests.add(request1);
        itemRequests.add(request2);
        Mockito.when(this.itemRequestService.getRequests(1L)).thenReturn(itemRequests);

        mockMvc.perform(
                        get("/requests")
                                .header("X-Sharer-User-Id", 1L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("При запросе должны вернуться все запросы других пользователей")
    void getAllRequests() throws Exception {
        List<ItemRequest> otherRequests = new ArrayList<>();
        otherRequests.add(request2);
        otherRequests.add(request3);
        Mockito.when(this.itemRequestService.getAllRequests(1L, 0, 10)).thenReturn(otherRequests);

        mockMvc.perform(
                        get("/requests/all")
                                .header("X-Sharer-User-Id", 1L)
                                .param("from", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("при запросе возвращает ItemRequestInfoDto")
    void getRequestByIdTest_returnItemRequestInfoDto() throws Exception {
        Item itemDto1 = new Item(1L, "item1", "description1", Status.AVAILABLE, userToCheck2, request1);
        Item itemDto2 = new Item(2L, "item2", "description2", Status.AVAILABLE, userToCheck2, request1);
        List<Item> itemList = new ArrayList<>();
        itemList.add(itemDto1);
        itemList.add(itemDto2);

        Mockito.when(this.itemService.findItemsByRequestId(1L)).thenReturn(itemList);
        Mockito.when(this.itemRequestService.getRequestById(1L, 1L)).thenReturn(request1);

        mockMvc.perform(
                        get("/requests/1")
                                .header("X-Sharer-User-Id", 1)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("запрос1"))
                .andExpect(jsonPath("$.created").value("2024-03-01T12:00:00"))
                .andExpect(jsonPath("$.items.[0].id").value(1))
                .andExpect(jsonPath("$.items.[0].name").value("item1"))
                .andExpect(jsonPath("$.items.[0].description").value("description1"))
                .andExpect(jsonPath("$.items.[0].available").value("true"))
                .andExpect(jsonPath("$.items.[0].owner.id").value(2))
                .andExpect(jsonPath("$.items.[0].owner.email").value("user2@mail.ru"))
                .andExpect(jsonPath("$.items.[0].owner.name").value("user2"))
                .andExpect(jsonPath("$.items.[0].requestId").value(1))
                .andExpect(jsonPath("$.items.[1].id").value(2L))
                .andExpect(jsonPath("$.items.[1].name").value("item2"))
                .andExpect(jsonPath("$.items.[1].description").value("description2"))
                .andExpect(jsonPath("$.items.[1].available").value("true"))
                .andExpect(jsonPath("$.items.[1].owner.id").value(2))
                .andExpect(jsonPath("$.items.[1].owner.email").value("user2@mail.ru"))
                .andExpect(jsonPath("$.items.[1].owner.name").value("user2"))
                .andExpect(jsonPath("$.items.[1].requestId").value(1));
    }
}