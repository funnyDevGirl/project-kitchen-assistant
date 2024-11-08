package io.project.kitchen_assistant.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.project.kitchen_assistant.config.AppConfig;
import io.project.kitchen_assistant.dto.todoist.tasks.TaskDTO;
import io.project.kitchen_assistant.dto.todoist.tasks.TodoistTaskRequest;
import io.project.kitchen_assistant.dto.todoist.tasks.TodoistTaskResponse;
import io.project.kitchen_assistant.dto.todoist.tasks.response.Due;
import io.project.kitchen_assistant.mapper.TaskMapper;
import io.project.kitchen_assistant.service.impl.TaskServiceImpl;
import io.project.kitchen_assistant.util.FileReader;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.*;

@Slf4j
//@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class TaskServiceImplTest {
    @Mock
    private AppConfig appConfig;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    @Qualifier("restTemplateForPostToTodoist")
    private RestTemplate restTemplateTodoistApiForPost;

    @Mock
    @Qualifier("restTemplateForGetAndDeleteOnTodoist")
    private RestTemplate restTemplateTodoistApiForGetAndDelete;

    @InjectMocks
    private TaskServiceImpl taskService;

    private ObjectMapper om = new ObjectMapper();

    private TodoistTaskRequest testTaskRequest1;
    private TodoistTaskRequest testTaskRequest2;
    private TaskDTO expectedDto;

//    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream(); // Перехват системного вывода
//    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

//        System.setOut(new PrintStream(outContent)); // Перенаправление системного вывода

//        om.registerModule(new JavaTimeModule());
//        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        try {
            String fixtureForRequest1 = FileReader.readFixture("test-request-get-1.json");
            String fixtureForRequest2 = FileReader.readFixture("test-request-get-2.json");

            testTaskRequest1 = om.readValue(fixtureForRequest1, TodoistTaskRequest.class);
            log.info("testTaskRequest1: '{}'", testTaskRequest1);

            testTaskRequest2 = om.readValue(fixtureForRequest2, TodoistTaskRequest.class);
            log.info("testTaskRequest2: '{}'", testTaskRequest2);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        expectedDto = new TaskDTO("8549515925",
                "Рецепт пирога",
                "Список игредиентов для пирога",
                new Due(),
                List.of("Food", "Shopping"));
    }

//    @Test
//    void createTask_ShouldReturnTaskDTO_WhenApiCallIsSuccessful() throws Exception {
//
//        // Arrange
//        String fixtureForResponse1 = FileReader.readFixture("test-response-get-1.json");
//
//        TodoistTaskResponse apiResponse = om.readValue(fixtureForResponse1, TodoistTaskResponse.class);
//        log.info("apiResponse: '{}'", apiResponse);
//
//        TaskDTO taskDto = taskMapper.toDTO(apiResponse);
//        log.info("taskDto: '{}'", taskDto);
//
////        when(restTemplateForPost.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(TodoistTaskResponse.class)))
////                .thenReturn(new ResponseEntity<>(apiResponse, HttpStatus.OK));
//
//        when(taskMapper.toDTO(apiResponse)).thenReturn(new TaskDTO("8549515925",
//                "Рецепт пирога",
//                "Список игредиентов для пирога",
////                new Due(LocalDate.parse("2024-11-02"), "2024-11-02", false, "en"),
//                new Due("2024-11-02", "2024-11-02", false, "en"),
//                List.of("Food", "Shopping")));
//
//        // Act
//        TaskDTO result = taskService.create(testTaskRequest1);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals("8549515925", result.getId());
//        verify(restTemplateForPost).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(TodoistTaskResponse.class));
//    }

    @Test
    public void testCreateSuccess() throws Exception {
        // Arrange

        String fixtureForResponse1 = FileReader.readFixture("test-response-get-1.json");
        TodoistTaskResponse taskResponse = om.readValue(fixtureForResponse1, TodoistTaskResponse.class);

        when(appConfig.getTodoistTasksApiUrl()).thenReturn("http://fakeurl.com");
        when(restTemplateTodoistApiForPost.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(TodoistTaskResponse.class)))
                .thenReturn(new ResponseEntity<>(taskResponse, HttpStatus.OK));

        when(taskMapper.toDTO(taskResponse)).thenReturn(expectedDto);
        var actual = taskMapper.toDTO(taskResponse);
        var expected = expectedDto;
        var testTaskRequest = testTaskRequest1;

        // Act
        TaskDTO result = taskService.create(testTaskRequest1);

        // Assert
        assertNotNull(result);
        assertEquals("Рецепт пирога", result.getContent());
        verify(restTemplateTodoistApiForPost, times(1)).exchange(anyString(), any(), any(), eq(TodoistTaskResponse.class));
    }


//    @Test
//    public void testCreateFailure() {
//        // Arrange
//        TodoistTaskRequest taskRequest = new TodoistTaskRequest();
//        taskRequest.setContent("Test task");
//
//        when(appConfig.getTodoistTasksApiUrl()).thenReturn("http://fakeurl.com");
//        when(restTemplateTodoistApiForPost.exchange(
//                anyString(),
//                eq(HttpMethod.POST),
//                any(HttpEntity.class),
//                eq(TodoistTaskResponse.class)))
//                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));
//
//        // Act & Assert
//        Exception exception = assertThrows(ResponseStatusException.class, () -> {
//            taskService.create(taskRequest);
//        });
//
//        assertTrue(exception.getMessage().contains("Error during API call"));
//    }


}
