package de.masaki.objects;

import com.google.gson.JsonObject;
import lombok.Getter;

public class UserData {

    @Getter
    private final String username;
    @Getter
    private final boolean success;
    @Getter
    private final String message;
    @Getter
    private final JsonObject data;

    public UserData(String username, boolean success, String message, JsonObject data) {
        this.username = username;
        this.success = success;
        this.message = message;
        this.data = data;
    }
}
