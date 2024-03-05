package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoTest {
    @Autowired
    private JacksonTester<CommentDto> jacksonTester;

    @Test
    @DisplayName("Проверка корректности сериализации даты в JSON")
    void commentDtoTest() throws Exception {
        CommentDto commentDto = new CommentDto(1L, "comment1", "autor", LocalDateTime.now());
        JsonContent<CommentDto> result = jacksonTester.write(commentDto);
        assertThat(result).hasJsonPath("$.created");
    }
}