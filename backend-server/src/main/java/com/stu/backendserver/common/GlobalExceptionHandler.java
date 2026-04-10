package com.stu.backendserver.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse<Void> handleIllegalArgument(IllegalArgumentException ex) {
        return new ApiResponse<>(false, ex.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleCommonException(Exception ex) {
        return new ApiResponse<>(false, "系统异常: " + ex.getMessage(), null);
    }
}
