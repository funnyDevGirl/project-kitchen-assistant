package io.project.kitchen_assistant.handler;

import io.project.kitchen_assistant.exception.RecipeNotFoundException;
import io.project.kitchen_assistant.exception.TaskNotFoundException;
import io.project.kitchen_assistant.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    public void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    public void testHandleRecipeNotFoundException() {
        // Arrange
        RecipeNotFoundException ex = new RecipeNotFoundException("Recipe not found");

        // Act
        ResponseEntity<String> response = exceptionHandler.handleRecipeNotFoundException(ex);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Recipe not found", response.getBody());
    }

    @Test
    public void testHandleUserNotFoundException() {
        // Arrange
        UserNotFoundException ex = new UserNotFoundException("User not found");

        // Act
        ResponseEntity<String> response = exceptionHandler.handleUserNotFoundException(ex);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
    }

    @Test
    public void testHandleTaskNotFoundException() {
        // Arrange
        TaskNotFoundException ex = new TaskNotFoundException("Task not found");

        // Act
        ResponseEntity<String> response = exceptionHandler.handleTaskNotFoundException(ex);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Task not found", response.getBody());
    }

    @Test
    public void testHandleIllegalArgumentException() {
        // Arrange
        IllegalArgumentException ex = new IllegalArgumentException("Illegal argument");

        // Act
        ResponseEntity<String> response = exceptionHandler.handleIllegalArgumentException(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Illegal argument", response.getBody());
    }

    @Test
    public void testHandleOtherExceptions() {
        // Arrange
        Exception ex = new Exception("Some internal error");

        // Act
        ResponseEntity<String> response = exceptionHandler.handleOtherExceptions(ex);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal Server Error: Some internal error", response.getBody());
    }
}
