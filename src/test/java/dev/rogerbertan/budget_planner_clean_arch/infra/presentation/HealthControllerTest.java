package dev.rogerbertan.budget_planner_clean_arch.infra.presentation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class HealthControllerTest {

    private HealthController controller;

    @BeforeEach
    void setUp() {
        controller = new HealthController();
    }

    @Test
    void health_ShouldReturnOK_WhenCalled() {
        // Act
        ResponseEntity<String> result = controller.health();

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo("OK");
    }

    @Test
    void health_ShouldReturn200Status_WhenCalled() {
        // Act
        ResponseEntity<String> result = controller.health();

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
    }
}
