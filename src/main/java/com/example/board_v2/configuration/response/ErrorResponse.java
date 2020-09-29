package com.example.board_v2.configuration.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private int code;
    private String message;
    private List<String> errorDetails;
    private String responseTime;

    public ErrorResponse(HttpStatus httpStatus, String message, List<String >errorDetails) {
        this.code = httpStatus.value();
        this.message = message;
        this.errorDetails = errorDetails;
        this.responseTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
    }
}
