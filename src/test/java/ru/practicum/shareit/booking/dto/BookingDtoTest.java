package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import static org.assertj.core.api.Assertions.assertThat;


import java.time.LocalDateTime;

@JsonTest
class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingDto> jacksonTester;

    @Test
    @DisplayName("Проверка корректности сериализации даты в JSON")
    void bookingDtoTest() throws Exception {
        BookingDto bookingDto = new BookingDto(1L, 1L, LocalDateTime.now(), LocalDateTime.now().plusHours(8));
        JsonContent<BookingDto> result = jacksonTester.write(bookingDto);
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
    }
}