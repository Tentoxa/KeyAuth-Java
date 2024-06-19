package de.masaki.objects;

import lombok.Getter;

public class BanResult {
    @Getter
    private boolean success;
    @Getter
    private String message;

    public BanResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

}
