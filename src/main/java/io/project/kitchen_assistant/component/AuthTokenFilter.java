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
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        log.info("Processing request: method={}, URI={}", httpRequest.getMethod(), httpRequest.getRequestURI());

        if (httpRequest.getRequestURI().equals("/api/v1/tasks")) {

            String email = userService.getCurrentUser();
            log.info("Received email: '{}'", email);

            User user = userRepository.findByEmail(email).orElseThrow(
                    () -> new UserNotFoundException(format("User with email: '%s' not found", email)));

            String userToken = user.getTodoistToken();

            if (userToken == null || userToken.isEmpty()) {

                httpResponse.sendRedirect("http://localhost:8080/api/v1/auth/authorize");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Инициализация фильтра (если требуется)
    }

    @Override
    public void destroy() {
        // Освобождение ресурсов (если требуется)
    }
}

