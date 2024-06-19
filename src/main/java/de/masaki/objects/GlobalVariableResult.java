package de.masaki.objects;

import lombok.Getter;

public class GlobalVariableResult {
    @Getter
    private boolean success;

    @Getter
    private String message;

    @Getter
    private String variable;

    public GlobalVariableResult(boolean success, String message, String variable) {
        this.success = success;
        this.message = message;
        this.variable = variable;
    }


}
