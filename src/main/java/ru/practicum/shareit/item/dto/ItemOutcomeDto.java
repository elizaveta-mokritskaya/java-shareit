package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class ItemOutcomeDto {
    private Long id;
    @NotBlank(message = "Название вещи не должно быть пустым")
    private String name;
    @NotBlank(message = "Описание вещи не должно быть пустым")
    private String description;
    @NotNull
    private Boolean available;
    private UserDto owner;
    private Long requestId;
}

