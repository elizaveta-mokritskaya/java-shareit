package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingOutcomeDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.Status;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerMvcTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;
    @MockBean
    private BookingService bookingService;
    @MockBean
    private CommentService commentService;
    @Autowired
    private ObjectMapper objectMapper;

    private User booker;
    private User owner;
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
    BookingOutcomeDto bookingOutcomeDto;
    BookingOutcomeDto bookingOutcomeDto2;
    private Comment comment1;

    @BeforeEach
    void setUp() {
        booker = new User(1L, "booker@mail.ru", "booker");
        owner = new User(2L, "owner@mail.ru", "owner");
        ownerDto = new UserDto(2L, "owner@mail.ru", "owner");
        created = LocalDateTime.now();
        start = LocalDateTime.now().plusHours(1);
        end = LocalDateTime.now().plusDays(10);
        request1 = new ItemRequest(1L, "request1", booker, created);
        request2 = new ItemRequest(2L, "request2", booker, created);
        item1 = new Item(1L, "item1", "description1", Status.AVAILABLE, owner, request1);
        item2 = new Item(2L, "item2", "description2", Status.AVAILABLE, owner, request2);
        booking1 = new Booking(1L, start, end, item1, booker, BookingStatus.WAITING);
        booking2 = new Booking(2L, start, end, item2, booker, BookingStatus.WAITING);
        bookingOutcomeDto = new BookingOutcomeDto(1L, start, end, item1, booker, booking1.getBookingStatus().name());
        bookingOutcomeDto2 = new BookingOutcomeDto(2L, start, end, item2, booker, booking2.getBookingStatus().name());
        comment1 = new Comment(1L, "comment1", item1, booker, created);
    }

    @Test
    @DisplayName("Получен запрос - показать список вещей пользователя ")
    void get() throws Exception {
        List<CommentDto> commentDtoList = List.of();
        List<Item> itemList = List.of(item1);
        when(itemService.getItemsToPage(anyLong(), anyInt(), anyInt())).thenReturn(itemList);
        List<ItemOutcomeInfoDto> dtoList = List.of(new ItemOutcomeInfoDto(
                1L, "item1", "description1", true, ownerDto, request1.getId(), null, null, commentDtoList));

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/items")
                        .header("X-Sharer-User-Id", 2L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(dtoList), result);
    }

    @Test
    @DisplayName("Bad_Request при запросе показать список вещей пользователя ")
    void get_returnBadRequest() throws Exception {
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content("request"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Получен запрос на обновление данных итема")
    void updateItem() throws Exception {
        Item oldItem = new Item(1L, "old", "oldDesc", Status.AVAILABLE, owner, request1);

        when(itemService.updateItem(anyLong(), anyLong(), anyString(), anyString(), any())).thenReturn(oldItem);
        ItemOutcomeDto itemOutcomeDto = ItemMapper.toItemDto(oldItem);

        String result = mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(itemOutcomeDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemOutcomeDto), result);
    }

    @Test
    @DisplayName("Неуспешное обновление данных итема")
    void updateItem_badRequest() throws Exception {
        mockMvc.perform(patch("/items/{itemId}", 10L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Запрос на добавление итема")
    void add() throws Exception {
        when(itemService.addNewItem(anyLong(), anyString(), anyString(), any(), anyLong())).thenReturn(item1);
        ItemOutcomeDto itemOutcomeDto = ItemMapper.toItemDto(item1);
        String result = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(itemOutcomeDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemOutcomeDto), result);
    }

    @Test
    @DisplayName("Bad_Request при запросе на добавление итема")
    void add_returnBadRequest() throws Exception {
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content("request"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Запрос вещи")
    void getItemById() throws Exception {
        List<CommentDto> commentDtoList = List.of(CommentMapper.toCommentDto(comment1));
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(item1);
        when(commentService.getComments(item1.getId())).thenReturn(List.of(comment1));
        ItemOutcomeInfoDto infoDto = ItemMapper.toItemDtoWithComments(item1, commentDtoList);

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/items/{itemsId}", 1L)
                        .header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(infoDto), result);
    }

    @Test
    @DisplayName("неуспешный запрос вещи")
    void getItemById_badRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/items/{itemId}", 1L)
                        .header("Неверно! X-Sharer-User-Id", 10L))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Удаление вещи")
    void deleteItemTest() throws Exception {
        mockMvc.perform(delete("/items/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Получен запрос на поиск итема")
    void searchItem() throws Exception {
        List<Item> itemList = List.of(item1);
        List<ItemOutcomeDto> dtoList = List.of(ItemMapper.toItemDto(item1));
        when(itemService.getItemsByDescription(anyString(), anyInt(), anyInt())).thenReturn(itemList);

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "description1")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(dtoList), result);
    }

    @Test
    @DisplayName("Добавление комментария")
    void addComment() throws Exception {
        when(commentService.addComment(anyLong(), anyLong(), anyString())).thenReturn(comment1);
        CommentDto commentDto = CommentMapper.toCommentDto(comment1);

        String result = mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(commentDto), result);
    }
}