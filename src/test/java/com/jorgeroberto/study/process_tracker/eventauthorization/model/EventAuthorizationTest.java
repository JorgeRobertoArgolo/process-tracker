package com.jorgeroberto.study.process_tracker.eventauthorization.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventAuthorizationTest {

    @Test
    @DisplayName("Deve retornar um IllegalArgumentException, pois o número do público estimado, não é maior que 0.")
    void constructor_shouldThrowIllegalArgumentExceptionWhenAttendanceIsNotPositive() {
        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new EventAuthorization(0, true)
        );

        assertEquals("O público estimado deve ser um número positivo maior que 0.", exception.getMessage());
    }
}