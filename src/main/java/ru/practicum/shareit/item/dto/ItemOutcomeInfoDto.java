package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemOutcomeInfoDto {
    private Long id;
    @NotBlank(message = "Название вещи не должно быть пустым")
    private String name;
    @NotBlank(message = "Описание вещи не должно быть пустым")
    private String description;
    @NotNull
    private Boolean available;
    private UserDto owner;
    private Long requestId;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private List<CommentDto> comments;
}
