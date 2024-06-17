package de.masaki.objects;

import lombok.Getter;

public class SessionResponse {
    @Getter
    private final boolean success;
    @Getter
    private final String message;

    public SessionResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
