package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class UserDto {
    private Long id;
    @Email(message = "Email должен быть корректным адресом электронной почты")
    @NotBlank(message = "Email не должен быть пустым")
    private String email;
    @NotBlank(message = "Имя пользователя не должно быть пустым")
    private String name;
}
