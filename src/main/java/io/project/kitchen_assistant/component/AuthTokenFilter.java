package io.project.kitchen_assistant.component;

import io.project.kitchen_assistant.exception.UserNotFoundException;
import io.project.kitchen_assistant.model.User;
import io.project.kitchen_assistant.repository.UserRepository;
import io.project.kitchen_assistant.utils.UserUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.Filter;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import static java.lang.String.format;

/**
 * {@code AuthTokenFilter} - фильтр для аутентификации пользователей
 * и управления доступом к защищенным ресурсам.
 *
 * <p>
 * Этот фильтр проверяет наличие JWT токена в заголовке авторизации,
 * а также гарантирует, что пользователи имеют доступ к ресурсам на основе
 * их аутентифицированного состояния. Фильтр также обрабатывает
 * специальные запросы к ресурсам, связанным с аутентификацией.
 * </p>
 *
 * <p>
 * Фильтр выполняет следующие задачи:
 * <ul>
 *     <li>Извлечение JWT токена из запроса.</li>
 *     <li>Проверка разрешений для доступа к эндпоинтам, таким как
 *         "/api/v1/tasks".</li>
 *     <li>Возврат HTML-ответа при запросе к эндпоинту
 *         "/api/v1/auth/close".</li>
 * </ul>
 * </p>
 *
 * <p>
 * Фильтр запускается перед обработкой запроса и может изменять
 * состояние {@code HttpServletResponse} в зависимости от логики
 * аутентификации.
 * </p>
 */
@Component
@Slf4j
public class AuthTokenFilter implements Filter {

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private UserRepository userRepository;

    /**
     * Метод, выполняющий фильтрацию входящих HTTP-запросов.
     *
     * <p>
     * Этот метод проверяет наличие JWT токена в заголовке
     * авторизации и устанавливает аутентификацию пользователя в
     * {@code SecurityContext}, если токен действителен.
     * </p>
     *
     * <p>
     * Если токен недействителен или отсутствует, фильтр
     * устанавливает статус ответа {@code SC_UNAUTHORIZED} (401).
     * Если пользователь обращается к эндпоинту
     * "/api/v1/auth/close", он получает HTML-ответ.
     * </p>
     *
     * @param request  объект {@code ServletRequest}, представляющий
     *                 входящий запрос.
     * @param response объект {@code ServletResponse}, представляющий
     *                 выходящий ответ.
     * @param chain    цепочка фильтров для передачи управления.
     * @throws IOException      если происходит ошибка ввода-вывода.
     * @throws ServletException если возникает ошибка в сервлете.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String jwtToken = httpRequest.getHeader("Authorization");
        httpResponse.setHeader("Authorization", jwtToken);

        log.info("Processing request: method={}, URI={}", httpRequest.getMethod(), httpRequest.getRequestURI());

        if (httpRequest.getRequestURI().equals("/api/v1/tasks")) {

            String email = userUtils.getCurrentUserEmail();
            log.info("Received email: '{}'", email);

            User user = userRepository.findByEmail(email).orElseThrow(
                    () -> new UserNotFoundException(format("User with email: '%s' not found", email)));

            String userToken = user.getTodoistToken();

            if (userToken == null || userToken.isEmpty()) {

                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

        } else if (httpRequest.getRequestURI().equals("/api/v1/auth/close")) {
            String htmlResponse = null;
            try {
                htmlResponse = readHtmlFile("successAuth.html");

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            httpResponse.setContentType("text/html");
            httpResponse.setCharacterEncoding("UTF-8");
            httpResponse.getWriter().write(htmlResponse);
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(request, response);
    }

    /**
     * Считывает HTML-файл из ресурсов приложения.
     * Вданном случае файл с текстом для появляющегося окна после успешной авторизации в Todoist.
     *
     * @param fileName имя HTML-файла, который необходимо считать.
     * @return содержимое HTML-файла как {@code String}.
     * @throws IOException если файл не найден или произошла ошибка ввода-вывода.
     */
    private String readHtmlFile(String fileName) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);

        if (inputStream == null) {
            throw new IOException("Html file was not found: " + fileName);
        }

        StringBuilder htmlBuilder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                htmlBuilder.append(line);
            }
        }
        return htmlBuilder.toString();
    }
}
