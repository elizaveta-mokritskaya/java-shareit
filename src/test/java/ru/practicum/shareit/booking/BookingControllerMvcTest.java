package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingIncomeDto;
import ru.practicum.shareit.booking.dto.BookingOutcomeDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.Status;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerMvcTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    BookingService bookingService;
    @Autowired
    private ObjectMapper objectMapper;

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
    BookingOutcomeDto bookingOutcomeDto;
    BookingOutcomeDto bookingOutcomeDto2;

    @BeforeEach
    void setUp() {
        booker = new User(1L, "user1@mail.ru", "user1");
        owner = new User(2L, "user2@mail.ru", "user2");
        bookerDto = new UserDto(1L, "user1@mail.ru", "user1");
        ownerDto = new UserDto(2L, "user2@mail.ru", "user2");
        created = LocalDateTime.now();
        start = LocalDateTime.now().plusHours(1);
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
    @DisplayName("Сохраняем новое бронирование")
    void saveNewBooking() throws Exception {
        when(bookingService.saveNewBooking(start, end, 1L, 1L)).thenReturn(bookingOutcomeDto);
        System.out.println(objectMapper.writeValueAsString(bookingIncomeDto));
        mockMvc.perform(
                post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(bookingIncomeDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.start").value(start))
                .andExpect(jsonPath("$.end").value(end))
                .andExpect(jsonPath("$.item.id").value(1L))
                .andExpect(jsonPath("$.item.name").value(item1.getName()))
                .andExpect(jsonPath("$.item.description").value(item1.getDescription()))
                .andExpect(jsonPath("$.item.available").value(item1.getAvailable().name()))
                .andExpect(jsonPath("$.item.owner.id").value(item1.getOwner().getId()))
                .andExpect(jsonPath("$.item.owner.email").value(item1.getOwner().getEmail()))
                .andExpect(jsonPath("$.item.owner.name").value(item1.getOwner().getName()))
                .andExpect(jsonPath("$.item.request.id").value(item1.getRequest().getId()))
                .andExpect(jsonPath("$.item.request.description").value(item1.getRequest().getDescription()))
                .andExpect(jsonPath("$.item.request.requestor.id").value(item1.getRequest().getRequestor().getId()))
                .andExpect(jsonPath("$.item.request.requestor.email").value(item1.getRequest().getRequestor().getEmail()))
                .andExpect(jsonPath("$.item.request.requestor.name").value(item1.getRequest().getRequestor().getName()))
                .andExpect(jsonPath("$.item.request.createdTime").value(item1.getRequest().getCreatedTime()))
                .andExpect(jsonPath("$.item.request.createdTime").value(item1.getRequest().getCreatedTime()))
                .andExpect(jsonPath("$.booker.id").value(booker.getId()))
                .andExpect(jsonPath("$.booker.email").value(booker.getEmail()))
                .andExpect(jsonPath("$.booker.name").value(booker.getName()))
                .andExpect(jsonPath("$.status").value(status()));
    }

    @Test
    void updateBooking() {
    }

    @Test
    void getBookingById() {
    }

    @Test
    void getBookingsByUser() {
    }

    @Test
    void getBookingsByOwner() {
    }
}