package com.datavault.Dto;

import lombok.Data;
import lombok.Builder;

@Data
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    @Builder(builderMethodName = "builder")
    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
}
