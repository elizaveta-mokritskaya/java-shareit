package ru.practicum.shareit.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerMvcTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    UserService userService;

    @Test
    @DisplayName("При запросе должны вернуться все пользователи Дто")
    void getAllUsersTest_returnAllUserDto() throws Exception {
        List<UserDto> userDtoList = new ArrayList<>();
        UserDto userDto1 = new UserDto(1L, "user1@mail.ru", "user1");
        UserDto userDto2 = new UserDto(2L, "user2@mail.ru", "user2");
        userDtoList.add(userDto1);
        userDtoList.add(userDto2);
        Mockito.when(this.userService.getAllUsers()).thenReturn(userDtoList);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("Возвращает UserDto при сохранении")
    void addNewUser_returnUserDto() throws Exception {
        UserDto userDto1 = new UserDto(1L, "user1@mail.ru", "user1");
        Mockito.when(this.userService.addUser(any(),any(),any()))
                .thenReturn(userDto1);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"name\": \"update\",\n" +
                        "  \"email\": \"update@user.com\"\n" +
                        "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("user1@mail.ru"))
                .andExpect(jsonPath("$.name").value("user1"));
    }

    @Test
    @DisplayName("При запросе несуществующего пользователя возвращает ошибку")
    void updateUserTest() throws Exception {
        Mockito.when(this.userService.updateUser(anyLong(), any()))
                .thenThrow(new DataNotFoundException("Пользователь не найден."));

        mockMvc.perform(
                        patch("/users/9")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\n" +
                                        "  \"name\": \"update\",\n" +
                                        "  \"email\": \"update@user.com\"\n" +
                                        "}"))
                .andExpect(status().is(404));
    }

    @Test
    @DisplayName("Возвращает UserDto при запросе")
    void getUser_returnUserDto() throws Exception {
        UserDto userDto1 = new UserDto(1L, "user1@mail.ru", "user1");
        Mockito.when(this.userService.getUserById(1L)).thenReturn(userDto1);
        mockMvc.perform(
                        get("/users/1")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("user1@mail.ru"))
                .andExpect(jsonPath("$.name").value("user1"));
    }

    @Test
    @DisplayName("Ответ 200 при запросе на удаление")
    void deleteUserTest() throws Exception {
        mockMvc.perform(
                delete("/users/1"))
                .andExpect(status().isOk());
    }
}