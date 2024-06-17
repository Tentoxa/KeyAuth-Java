package de.masaki.objects;

import com.google.gson.JsonObject;
import lombok.Getter;

public class LoginResponse {

    @Getter
    private final boolean success;

    @Getter
    private final String message;

    @Getter
    private final JsonObject data;

    public LoginResponse(boolean success, String message, JsonObject data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
}
