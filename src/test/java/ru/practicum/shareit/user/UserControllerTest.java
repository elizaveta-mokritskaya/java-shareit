package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @InjectMocks
    UserController userController;
    @Mock
    UserService mockUserService;
    private User userToCheck1 = new User(1L, "user1@mail.ru", "user1");
    private User userToCheck2 = new User(2L, "user2@mail.ru", "user2");
    @Captor
    ArgumentCaptor<User> userArgumentCaptor;


    @Test
    @DisplayName("Вызываем сервис")
    void getAllUsersTest() {
        userController.getAllUsers();
        verify(mockUserService).getAllUsers();
    }

    @Test
    @DisplayName("Возвращаем список UserDto")
    void getAllUsersTest_ReturningUserDtoList() {
        List<UserDto> userDtoList = new ArrayList<>();
        userDtoList.add(UserMapper.toUserDto(userToCheck1));
        userDtoList.add(UserMapper.toUserDto(userToCheck2));
        Mockito.when(mockUserService.getAllUsers()).thenReturn(userDtoList);

        List<UserDto> result = userController.getAllUsers();

        Assertions.assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Вызываем сервис")
    void addNewUserTest() {
        User user = new User(3L, "user3@mail.ru", "user3");
        Mockito.when(mockUserService.addUser(3L, "user3@mail.ru", "user3"))
                .thenReturn(UserMapper.toUserDto(user));

        userController.addNewUser(new UserDto(3L, "user3@mail.ru", "user3"));
        verify(mockUserService).addUser(3L, "user3@mail.ru", "user3");
    }

    @Test
    @DisplayName("Возвращаем UserDto")
    void addNewUserTest_returningUserDto() {
        User user = new User(3L, "user3@mail.ru", "user3");
        Mockito.when(mockUserService.addUser(null, "user3@mail.ru", "user3"))
                .thenReturn(UserMapper.toUserDto(user));

        UserDto result = userController.addNewUser(new UserDto(null, "user3@mail.ru", "user3"));
        UserDto userDto = new UserDto(3L, "user3@mail.ru", "user3");

        Assertions.assertEquals(userDto, result);
    }

    @Test
    @DisplayName("Обновление пользователя с Id null присвоение ему нового Id")
    void updateUserTest_whenUserIdNull_thenUserReturnWithId() {
        UserDto userDto = new UserDto(null, "user3@mail.ru", "user3");
        User user = new User(3L, "user3@mail.ru", "user3");
        Mockito.when(mockUserService.updateUser(anyLong(), any())).thenReturn(UserMapper.toUserDto(user));
        UserDto result = userController.updateUser(3L, userDto);

        Assertions.assertEquals(3L, result.getId());
    }

    @Test
    @DisplayName("Вызов сервиса при обновлении")
    void updateUserTest() {
        UserDto userDto = new UserDto(null, "user3@mail.ru", "user3");
        User user = new User(3L, "user3@mail.ru", "user3");
        Mockito.when(mockUserService.updateUser(anyLong(), any())).thenReturn(UserMapper.toUserDto(user));

        userController.updateUser(3L, userDto);

        verify(mockUserService).updateUser(3L, user);
    }

    @Test
    @DisplayName("Обновление пользователя - возвращение UserDto")
    void updateUserTest_thenReturnUserDto() {
        UserDto userDto = new UserDto(3L, "user3@mail.ru", "user3");
        User user = new User(3L, "user3@mail.ru", "user3");
        Mockito.when(mockUserService.updateUser(anyLong(), any())).thenReturn(UserMapper.toUserDto(user));

        UserDto result = userController.updateUser(3L, new UserDto(3L, "user3@mail.ru", "user3"));

        Assertions.assertEquals(userDto, result);
    }

    @Test
    @DisplayName("Вызываем сервис")
    void getUserTest() {
        Mockito.when(mockUserService.getUserById(1L)).thenReturn(UserMapper.toUserDto(userToCheck1));

        userController.getUser(1L);

        verify(mockUserService).getUserById(1L);
    }

    @Test
    @DisplayName("Возвращаем UserDto")
    void getUserTest_thenReturnUserDto() {
        UserDto userDto = new UserDto(3L, "user3@mail.ru", "user3");
        User user = new User(3L, "user3@mail.ru", "user3");
        Mockito.when(mockUserService.getUserById(3L)).thenReturn(UserMapper.toUserDto(user));

        UserDto result = userController.getUser(3L);

        Assertions.assertEquals(userDto, result);
    }

    @Test
    @DisplayName("Вызываем сервис")
    void deleteUserTest() {
        userController.deleteUser(1L);

        verify(mockUserService).deleteUserById(1L);
    }
}