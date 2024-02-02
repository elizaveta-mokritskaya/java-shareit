package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
public class ItemRequestDto {
    Long id;
    String description;
    User requestor;
    LocalDateTime created;
}
