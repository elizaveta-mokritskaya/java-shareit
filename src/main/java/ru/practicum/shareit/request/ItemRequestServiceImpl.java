package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository repository;
    private final UserService userService;

    @Override
    public ItemRequest addNewRequest(Long userId, String description) {
        User user = UserMapper.toUser(userService.getUserById(userId));
        if (user == null) {
            throw new DataNotFoundException("Пользователь не найден");
        }
        if (description == null) {
            throw new ValidationException("Описание не может быть пустым");
        }
        return repository.save(new ItemRequest(null, description, user, LocalDateTime.now()));
    }

    @Override
    public ItemRequest getRequestById(Long userId, Long requestId) {
        if (userService.getUserById(userId) == null) {
            throw new DataNotFoundException("Пользователь не найден");
        }
        ItemRequest resultRequest = repository.findById(requestId).orElse(null);
        if (resultRequest == null) {
            throw new DataNotFoundException("Не найден запрос с данным id");
        }
        return resultRequest;
    }

    @Override
    public List<ItemRequest> getRequests(Long userId) {
        if (userService.getUserById(userId) == null) {
            throw new DataNotFoundException("Пользователь не найден.");
        }
        return repository.findAllByUserId(userId);
    }

    @Override
    public List<ItemRequest> getAllRequests(Long userId, int from, int size) {
        if (userService.getUserById(userId) == null) {
            throw new DataNotFoundException("Пользователь не найден.");
        }
        Sort sortByDate = Sort.by(Sort.Direction.DESC, "createdTime");
        return repository.findAll(userId, PageRequest.of(from, size, sortByDate)).getContent();
    }
}
