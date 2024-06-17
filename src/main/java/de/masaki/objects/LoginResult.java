package de.masaki.objects;

import lombok.Getter;

public class LoginResult {
    @Getter
    private final boolean success;
    @Getter
    private final String message;
    @Getter
    private final String username;

    public LoginResult(String username, boolean success, String message) {
        this.success = success;
        this.message = message;
        this.username = username;
    }
}
