package de.masaki.objects;

import lombok.Getter;

public class RegisterResult {

    @Getter
    private String username;

    @Getter
    private boolean success;

    @Getter
    private String message;

    public RegisterResult(String username, boolean success, String message) {
        this.username = username;
        this.success = success;
        this.message = message;
    }
}
