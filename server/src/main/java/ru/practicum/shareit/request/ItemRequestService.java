package ru.practicum.shareit.request;

import java.util.List;

public interface ItemRequestService {
    ItemRequest addNewRequest(Long userId, String description);

    ItemRequest getRequestById(Long userId, Long requestId);

    List<ItemRequest> getRequests(Long userId);

    List<ItemRequest> getAllRequests(Long userId, int from, int size);
}
