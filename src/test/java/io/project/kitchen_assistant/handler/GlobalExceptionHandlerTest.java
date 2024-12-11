package io.project.kitchen_assistant.handler;

import io.project.kitchen_assistant.exception.RecipeNotFoundException;
import io.project.kitchen_assistant.exception.TaskNotFoundException;
import io.project.kitchen_assistant.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Objects;
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
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleRecipeNotFoundException(ex);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, Objects.requireNonNull(response.getBody()).getCode());
        assertEquals("Recipe not found", response.getBody().getMessage());
    }

    @Test
    public void testHandleUserNotFoundException() {
        // Arrange
        UserNotFoundException ex = new UserNotFoundException("User not found");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUserNotFoundException(ex);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, Objects.requireNonNull(response.getBody()).getCode());
        assertEquals("User not found", response.getBody().getMessage());
    }

    @Test
    public void testHandleTaskNotFoundException() {
        // Arrange
        TaskNotFoundException ex = new TaskNotFoundException("Task not found");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleTaskNotFoundException(ex);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, Objects.requireNonNull(response.getBody()).getCode());
        assertEquals("Task not found", response.getBody().getMessage());
    }

    @Test
    public void testHandleIllegalArgumentException() {
        // Arrange
        IllegalArgumentException ex = new IllegalArgumentException("Illegal argument");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgumentException(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, Objects.requireNonNull(response.getBody()).getCode());
        assertEquals("Illegal argument", response.getBody().getMessage());
    }

    @Test
    public void testHandleOtherExceptions() {
        // Arrange
        Exception ex = new Exception("Some internal error");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleOtherExceptions(ex);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, Objects.requireNonNull(response.getBody()).getCode());
        assertEquals("Internal Server Error: Some internal error", response.getBody().getMessage());
    }

    @Test
    public void testHandleDataIntegrityViolationException() {
        // Arrange
        DataIntegrityViolationException ex = new DataIntegrityViolationException("Data integrity violation");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleDataIntegrityViolationException(ex);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, Objects.requireNonNull(response.getBody()).getCode());
        assertEquals("The user could not be deleted. Please try again later.Data integrity violation",
                response.getBody().getMessage());
    }
}
