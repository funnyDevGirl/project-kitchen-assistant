package io.project.kitchen_assistant.component;

import io.project.kitchen_assistant.utils.JWTUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import java.io.IOException;

/**
 * {@code TokenFilter} - фильтр для обработки JWT и аутентификации пользователей в приложении.
 * <p>
 * Этот фильтр перехватывает запросы к API, извлекает JWT токен из параметров запроса,
 * декодирует его для получения email пользователя и устанавливает аутентификацию в
 * {@code SecurityContext}.
 * </p>
 *
 * <p>
 * Фильтр будет срабатывать только для запросов, направленных на API маршрут
 * "/api/v1/auth/authorize".
 * </p>
 *
 * <p>
 * Инициализация фильтра происходит в классе SecurityConfig путем установления
 * в http.addFilterBefore(new TokenFilter(jwtUtils), UsernamePasswordAuthenticationFilter.class)
 * </p>
 */
@Slf4j
@RequiredArgsConstructor
public class TokenFilter  implements Filter {

    private final JWTUtils jwtUtils;

    /**
     * Метод, выполняющий фильтрацию запросов.
     *
     * @param request  объект {@code ServletRequest} для получения информации о запросе.
     * @param response объект {@code ServletResponse} для отправки ответа.
     * @param filterChain цепочка фильтров для передачи управления.
     * @throws IOException при вводе-выводе ошибок.
     * @throws ServletException при ошибках сервлета.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        if (httpRequest.getRequestURI() != null
                && httpRequest.getRequestURI().startsWith("/api/v1/auth/authorize")) {

            log.info("Processing request: method={}, URI={}", httpRequest.getMethod(), httpRequest.getRequestURI());

            String jwt = httpRequest.getParameter("data");

             if (jwt != null) {
                 String emailFromToken = jwtUtils.extractUsername(jwt);

                 log.info("Email '{}' was successfully received from the token.", emailFromToken);

                 final var authToken = jwtUtils.buildAuthToken(emailFromToken);
                 SecurityContextHolder.getContext().setAuthentication(authToken);
             }
        }


        filterChain.doFilter(request, response);
    }
}
