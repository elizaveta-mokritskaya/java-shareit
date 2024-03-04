package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JsonTest
class BookingIncomeDtoTest {
    @Autowired
    private JacksonTester<BookingIncomeDto> jacksonTester;

    @Test
    @DisplayName("Проверка корректности сериализации даты в JSON")
    void bookingIncomeDtoTest() throws Exception {
        BookingIncomeDto bookingDto = new BookingIncomeDto(1L, LocalDateTime.now(), LocalDateTime.now().plusHours(8), 1L);
        JsonContent<BookingIncomeDto> result = jacksonTester.write(bookingDto);
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
    }
}