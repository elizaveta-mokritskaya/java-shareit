package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.Status;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemMapperTest {

    @Test
    void toItemDto() {
        User booker = new User(1L, "booker@mail.ru", "booker");
        User owner = new User(2L, "owner@mail.ru", "owner");
        LocalDateTime created = LocalDateTime.now();
        ItemRequest request1 = new ItemRequest(1L, "request1", booker, created);
        Item item1 = new Item(1L, "item1", "description1", Status.AVAILABLE, owner, request1);
        ItemOutcomeDto itemOutcomeDto = new ItemOutcomeDto(1L, item1.getName(),
                item1.getDescription(), true, UserMapper.toUserDto(owner), request1.getId());

        ItemOutcomeDto result = ItemMapper.toItemDto(item1);

        assertEquals(itemOutcomeDto, result);
    }

    @Test
    void toItemDtoWithComments() {
        User booker = new User(1L, "booker@mail.ru", "booker");
        User owner = new User(2L, "owner@mail.ru", "owner");
        LocalDateTime created = LocalDateTime.now();
        ItemRequest request1 = new ItemRequest(1L, "request1", booker, created);
        Item item1 = new Item(1L, "item1", "description1", Status.AVAILABLE, owner, request1);
        CommentDto commentDto = new CommentDto(1L, "comment1", "booker", created);
        ItemOutcomeInfoDto infoDto = new ItemOutcomeInfoDto(1L, item1.getName(), item1.getDescription(),
                true, UserMapper.toUserDto(owner), request1.getId(), null, null, List.of(commentDto));

        ItemOutcomeInfoDto result = ItemMapper.toItemDtoWithComments(item1, List.of(commentDto));

        assertEquals(infoDto, result);
    }

    @Test
    void toItemInfoDto() {
        User booker = new User(1L, "booker@mail.ru", "booker");
        User owner = new User(2L, "owner@mail.ru", "owner");
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusDays(10);
        ItemRequest request1 = new ItemRequest(1L, "request1", booker, created);
        Item item1 = new Item(1L, "item1", "description1", Status.AVAILABLE, owner, request1);
        Booking booking1 = new Booking(1L, start, end, item1, booker, BookingStatus.WAITING);
        BookingDto bookingDto1 = new BookingDto(1L, booker.getId(), start, end);
        CommentDto commentDto = new CommentDto(1L, "comment1", "booker", created);
        ItemOutcomeInfoDto infoDto = new ItemOutcomeInfoDto(1L, item1.getName(), item1.getDescription(),
                true, UserMapper.toUserDto(owner), request1.getId(), null, bookingDto1, List.of(commentDto));

        ItemOutcomeInfoDto result = ItemMapper.toItemInfoDto(item1, List.of(booking1), List.of(commentDto));

        assertEquals(infoDto, result);
    }
}