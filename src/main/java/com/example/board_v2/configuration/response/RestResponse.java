package com.example.board_v2.configuration.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RestResponse<T> {
    private int code;
    private T result;
    private String responseTime;

    public RestResponse(int code, T result) {
        this.code = code;
        this.result = result;
        this.responseTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
    }

}
