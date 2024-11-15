package io.project.kitchen_assistant.config;

public class AuthorizationCodeContext {

    private static final ThreadLocal<String> AUTH_CODE = new ThreadLocal<>();

    public static void setAuthorizationCode(String code) {
        AUTH_CODE.set(code);
    }

    public static String getAuthorizationCode() {
        return AUTH_CODE.get();
    }

    public static void clear() {
        AUTH_CODE.remove();
    }
}
