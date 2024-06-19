package de.masaki.objects;

import com.google.gson.JsonObject;
import lombok.Getter;

public class FetchStatsResponse {
    @Getter
    private boolean success;
    @Getter
    private String message;
    @Getter
    private JsonObject stats;

    public FetchStatsResponse(boolean success, String message, JsonObject stats) {
        this.success = success;
        this.message = message;
        this.stats = stats;
    }
}
