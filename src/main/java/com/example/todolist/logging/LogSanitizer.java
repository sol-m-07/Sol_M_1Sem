package com.example.todolist.logging;

public final class LogSanitizer {

    private LogSanitizer() {
    }

    public static String maskToken(String token) {
        if (token == null || token.isBlank()) {
            return "<empty>";
        }
        int visible = 6;
        if (token.length() <= visible * 2) {
            return "***masked***";
        }
        return token.substring(0, visible) + "..." + token.substring(token.length() - visible);
    }
}
