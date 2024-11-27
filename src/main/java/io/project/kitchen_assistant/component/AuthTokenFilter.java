package io.project.kitchen_assistant.component;

import io.project.kitchen_assistant.exception.UserNotFoundException;
import io.project.kitchen_assistant.model.User;
import io.project.kitchen_assistant.repository.UserRepository;
import io.project.kitchen_assistant.service.UserService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.io.PrintWriter;

import static java.lang.String.format;

@Slf4j
@Component
public class AuthTokenFilter implements Filter {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Установка CORS заголовков
//        httpResponse.setHeader("Access-Control-Allow-Origin", "http://localhost:8080"); // Указать фронтенд
//        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
//        httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
//
//        // Определение обработки OPTIONS запросов
//        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
//            httpResponse.setStatus(HttpServletResponse.SC_OK); // Возвращаем 200 OK для OPTIONS
//            return;
//        }

        log.info("Processing request: method={}, URI={}", httpRequest.getMethod(), httpRequest.getRequestURI());

        if (httpRequest.getRequestURI().equals("/api/v1/tasks")) {

            String email = userService.getCurrentUser();
            log.info("Received email: '{}'", email);

            User user = userRepository.findByEmail(email).orElseThrow(
                    () -> new UserNotFoundException(format("User with email: '%s' not found", email)));

            String userToken = user.getTodoistToken();

            if (userToken == null || userToken.isEmpty()) {

                httpResponse.sendRedirect("/api/v1/auth/authorize");
//                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 2 вариант проработки

                // Отправляем JSON ответ с флагом, что требуется авторизация // 2 вариант проработки с дополнениями для фронта
//                httpResponse.setContentType("application/json");
//                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                PrintWriter out = httpResponse.getWriter();
//                out.print("{\"requiresAuthorization\": true}");
//                out.flush();
                return;
            }
        }

        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Error CORS: {}", e.getMessage());
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {
        // Инициализация фильтра (если требуется)
    }

    @Override
    public void destroy() {
        // Освобождение ресурсов (если требуется)
    }
}
