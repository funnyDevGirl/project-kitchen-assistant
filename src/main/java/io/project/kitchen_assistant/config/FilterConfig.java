package io.project.kitchen_assistant.config;

import io.project.kitchen_assistant.component.AuthTokenFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

public class FilterConfig {

    @Bean
    public FilterRegistrationBean<AuthTokenFilter> authTokenFilter() {
        FilterRegistrationBean<AuthTokenFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new AuthTokenFilter());
        registrationBean.addUrlPatterns("/api/v1/tasks");
        registrationBean.setOrder(1);

        return registrationBean;
    }
}
