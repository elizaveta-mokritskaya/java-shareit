package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemOutcomeDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.ArrayList;
import java.util.List;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(),
                itemRequest.getDescription(),
                UserMapper.toUserDto(itemRequest.getRequestor()),
                itemRequest.getCreatedTime());
    }

    public static ItemRequestInfoDto toItemRequestDto2(ItemRequest itemRequest, List<ItemOutcomeDto> items) {
        return new ItemRequestInfoDto(itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreatedTime(),
                items.isEmpty() ? new ArrayList<>() : items);
    }
}
