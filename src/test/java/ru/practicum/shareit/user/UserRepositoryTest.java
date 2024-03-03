package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@DataJpaTest
class UserRepositoryTest {
    private final TestEntityManager entityManager;
    private final UserRepository userRepository;

    @Test
    @DisplayName("Получаем пользователя по email")
    void getUserByEmail() {
        User userTest = User.builder()
                .name("userTest")
                .email("userTest@mail.ru")
                .build();
        entityManager.persist(userTest);
         User result = userRepository.getUserByEmail(userTest.getEmail());

         assertEquals(userTest, result);
    }
}