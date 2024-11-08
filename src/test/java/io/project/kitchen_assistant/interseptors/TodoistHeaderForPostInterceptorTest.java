package io.project.kitchen_assistant.interseptors;

//import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import io.project.kitchen_assistant.config.AppConfig;
//import io.project.kitchen_assistant.interceptors.TodoistHeaderForPostInterceptor;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpRequest;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.client.ClientHttpRequestExecution;
//import org.springframework.http.client.ClientHttpRequestInterceptor;
//import org.springframework.http.client.ClientHttpResponse;
//import org.springframework.mock.http.MockHttpOutputMessage;
//import org.springframework.mock.http.MockHttpServletRequest;
//import org.springframework.mock.http.MockHttpServletResponse;
//
//import java.io.IOException;
//
//public class TodoistHeaderForPostInterceptorTest {
//
//    @Mock
//    private AppConfig appConfig;
//
//    @InjectMocks
//    private TodoistHeaderForPostInterceptor interceptor;
//
//    @BeforeEach
//    public void setup() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    public void testIntercept() throws IOException {
//        // Arrange
//        HttpRequest request = new MockHttpRequest();
//        byte[] body = new byte[0];
//        ClientHttpRequestExecution execution = (request, body1) -> {
//            // Returning a mock response
//            return new MockHttpResponse(HttpStatus.OK);
//        };
//
//        when(appConfig.getTodoistRequestId()).thenReturn("test-request-id");
//        when(appConfig.getTodoistApiToken()).thenReturn("Bearer test-token");
//
//        // Act
//        ClientHttpResponse response = interceptor.intercept(request, body, execution);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("application/json", request.getHeaders().getContentType().toString());
//        assertEquals("test-request-id", request.getHeaders().get("X-Request-Id").get(0));
//        assertEquals("Bearer test-token", request.getHeaders().get("Authorization").get(0));
//    }
//}
