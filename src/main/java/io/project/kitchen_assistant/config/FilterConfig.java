package io.project.kitchen_assistant.config;

import io.project.kitchen_assistant.component.AuthTokenFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import java.util.Collections;

public class FilterConfig {

    @Bean
    public FilterRegistrationBean<AuthTokenFilter> authTokenFilter() {
        FilterRegistrationBean<AuthTokenFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new AuthTokenFilter());
        registrationBean.addUrlPatterns("/api/v1/tasks");
        registrationBean.setOrder(1);

        return registrationBean;
    }

//    // Регистрация CORS фильтра
//    @Bean
//    public FilterRegistrationBean<CorsFilter> corsFilter() {
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        CorsConfiguration config = new CorsConfiguration();
//
//        config.setAllowCredentials(true); // Разрешение на использование учетных данных
//        config.setAllowedOrigins(Collections.singletonList("http://localhost:8080")); // Укажите ваш домен здесь
//        config.setAllowedMethods(Collections.singletonList("*")); // Все методы
//        config.setAllowedHeaders(Collections.singletonList("*")); // Все заголовки
//
//        source.registerCorsConfiguration("/**", config); // Применить ко всем URL
//        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
//        bean.setOrder(Ordered.HIGHEST_PRECEDENCE); // Задать высокий приоритет для работы с CORS
//
//        return bean;
//    }
}
