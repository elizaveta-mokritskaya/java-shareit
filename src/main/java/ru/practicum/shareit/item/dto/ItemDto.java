package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.user.User;

@Data
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private Long requestId;

    public ItemDto(Long id, String name, String description, Boolean available, User owner, Long requestId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
        this.requestId = requestId;
    }
}
