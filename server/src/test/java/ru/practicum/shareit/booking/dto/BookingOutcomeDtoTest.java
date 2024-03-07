package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.Status;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingOutcomeDtoTest {
    @Autowired
    private JacksonTester<BookingOutcomeDto> jacksonTester;

    @Test
    void bookingOutcomeDtoTest() throws Exception {
        User booker = new User(1L, "user1@mail.ru", "user1");
        User owner = new User(2L, "user2@mail.ru", "user2");
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        ItemRequest request1 = new ItemRequest(1L, "запрос1", booker, created);
        Item item1 = new Item(1L, "item1", "description1", Status.AVAILABLE, owner, request1);
        Booking booking1 = new Booking(1L, start, end, item1, booker, BookingStatus.WAITING);

        BookingOutcomeDto bookingOutcomeDto = new BookingOutcomeDto(1L, start, end,
                item1, booker, booking1.getBookingStatus().name());
        JsonContent<BookingOutcomeDto> result = jacksonTester.write(bookingOutcomeDto);
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
    }
}