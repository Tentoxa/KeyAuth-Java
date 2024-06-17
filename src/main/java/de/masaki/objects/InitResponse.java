package de.masaki.objects;

import com.google.gson.JsonObject;
import lombok.Getter;

public class InitResponse {

    @Getter
    private final boolean success;

    @Getter
    private final String message;

    @Getter
    private final JsonObject data;

    public InitResponse(boolean success, String message, JsonObject data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
}
