package de.masaki.objects;

import lombok.Getter;

public class LicenseLoginResult {

    @Getter
    private boolean success;

    @Getter
    private String message;

    public LicenseLoginResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

}
