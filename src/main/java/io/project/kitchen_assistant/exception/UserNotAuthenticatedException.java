package io.project.kitchen_assistant.exception;

import lombok.Getter;
import static java.lang.String.format;

@Getter
public class UserNotAuthenticatedException extends RuntimeException {
    private final String redirectUrl;

    public UserNotAuthenticatedException(String redirectUrl) {
        super(format("The user is not authenticated in the Todoist service. Redirection to: %s", redirectUrl));
        this.redirectUrl = redirectUrl;
    }
}
