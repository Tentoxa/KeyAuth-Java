package de.masaki.objects;

import lombok.Getter;

public class LogoutResult {

    @Getter
    private boolean success;

    @Getter
    private String message;

    public LogoutResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

}