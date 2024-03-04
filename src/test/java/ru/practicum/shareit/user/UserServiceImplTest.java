package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @InjectMocks
    UserServiceImpl userService;
    @Mock
    UserRepository mockUserRepository;
    @Captor
    ArgumentCaptor<User> userCaptor;
    private UserDto userToCheck1 = new UserDto(1L, "user1@mail.ru", "user1");
    private UserDto userToCheck2 = new UserDto(2L, "user2@mail.ru", "user2");
    private User validUser1 = new User(1L, "aa@mail.ru", "Aa");
    private User validUser2 = new User(2L, "bb@mail.ru", "Bb");

    @Test
    @DisplayName("Показывает список всех пользователей Дто")
    void getAllUsersTest() {
        List<User> users = List.of(validUser1, validUser2);
        when(mockUserRepository.findAll()).thenReturn(users);

        List<UserDto> result = userService.getAllUsers();

        Assertions.assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Корректное сохранение пользователя")
    void addUser_whenUserValid_thenSaveUser() {
        User user = new User(3l, "new@mail.ru", "new");
        UserDto userDto = UserMapper.toUserDto(user);
        when(mockUserRepository.save(any())).thenReturn(user);

        UserDto result = userService.addUser(3L, "new@mail.ru", "new");

        verify(mockUserRepository).save(any());
        Assertions.assertEquals(result, userDto);
    }

    @Test
    @DisplayName("Некорректное сохранение пользователя")
    void addUser_whenUserNotValid_thenNotSaveUser() {
        when(mockUserRepository.save(any())).thenThrow(new DataNotFoundException("Пользователь не найден"));

        final DataNotFoundException exception = Assertions.assertThrows(
                DataNotFoundException.class,
                () -> userService.addUser(10L, "new@mail.ru", "new"));
        verify(mockUserRepository).save(any());
        Assertions.assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    @DisplayName("Обновление пользователя")
    void updateUser_whenUserIsValid() {
        User oldUser = User.builder()
                .id(3L)
                .email("oldUser1@mail.ru")
                .name("oldUser1")
                .build();
        User userForUpdate = User.builder()
                .id(3L)
                .email("newUser1@mail.ru")
                .name("newUser1")
                .build();
        when(mockUserRepository.findById(anyLong())).thenReturn(Optional.of(oldUser));
        when((mockUserRepository.getUserByEmail(anyString()))).thenReturn(null);
        when(mockUserRepository.save(userForUpdate)).thenReturn(userForUpdate);
        userService.updateUser(3L, userForUpdate);

        verify(mockUserRepository).save(userCaptor.capture());
        Assertions.assertEquals(userForUpdate, userCaptor.getValue());
    }

    @Test
    @DisplayName("Обновление пользователя если входящий параметр null необходимо выбрасывать исключение ")
    void updateUser_whenUserNull_thenDataNotFoundException() {
        final DataNotFoundException exception = Assertions.assertThrows(
                DataNotFoundException.class,
                () -> userService.updateUser(1L, null));

        Assertions.assertEquals("Пользователь не найден.", exception.getMessage());
    }

    @Test
    @DisplayName("Обновление пользователя если у входящего параметра пользователя Id null" +
            " пользователь будет сохранен с Id")
    void updateUser_whenUserIdNull_thenUserSaveWithId() {
        User oldUser = User.builder()
                .id(3L)
                .email("oldUser1@mail.ru")
                .name("oldUser1")
                .build();
        User userForUpdate = User.builder()
                .email("newUser1@mail.ru")
                .name("newUser1")
                .build();
        when(mockUserRepository.findById(anyLong())).thenReturn(Optional.of(oldUser));
        when((mockUserRepository.getUserByEmail(anyString()))).thenReturn(null);
        when(mockUserRepository.save(userForUpdate)).thenReturn(userForUpdate);
        userService.updateUser(3L, userForUpdate);
        verify(mockUserRepository).save(userCaptor.capture());
        Assertions.assertEquals(3L, userCaptor.getValue().getId());
    }

    @Test
    @DisplayName("Обновление пользователя если у входящего параметра пользователя используемый email" +
            " будет выбрасываться исключение")
    void updateUser_whenUserEmailNotFree_thenRuntimeException() {
        User user = new User(2L, "aa@mail.ru", "new");
        when(mockUserRepository.findById(2L)).thenReturn(Optional.ofNullable(validUser2));
        when(mockUserRepository.getUserByEmail("aa@mail.ru")).thenReturn(validUser1);

        final RuntimeException exception = Assertions.assertThrows(
                RuntimeException.class,
                () -> userService.updateUser(2L, user));

        Assertions.assertEquals("Пользователь с таким email уже существует.", exception.getMessage());
    }

    @Test
    @DisplayName("Показать пользователя по Id, когда пользователь найден, возвратить этого пользователя")
    void getUserById_whenUserFound_thenReturnedUser() {
        when(mockUserRepository.findById(anyLong())).thenReturn(Optional.of(validUser1));

        UserDto result = userService.getUserById(1L);

        verify(mockUserRepository).findById(1L);
        Assertions.assertEquals(UserMapper.toUserDto(validUser1), result);
    }

    @Test
    @DisplayName("Показать пользователя по Id, когда пользователь не найден, возвратить ошибку")
    void getUserById_whenUserNotFound_thenDataNotFoundException() {
        when(mockUserRepository.findById(anyLong())).thenReturn(Optional.empty());

        final DataNotFoundException exception = Assertions.assertThrows(
                DataNotFoundException.class,
                () -> userService.getUserById(0L));

        Assertions.assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    @DisplayName("Удаление пользователя")
    void deleteUserById() {
        userService.deleteUserById(1L);
        verify(mockUserRepository, times(1)).deleteById(1L);
    }
}