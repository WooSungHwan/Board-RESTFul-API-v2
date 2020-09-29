package com.example.board_v2.configuration;

import com.example.board_v2.configuration.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorResponse handlerRuntimeException(EntityNotFoundException e, HttpServletRequest req) {
        log.error("===================== Handler RuntimeException =====================");
        e.printStackTrace();
        return new ErrorResponse(HttpStatus.NOT_FOUND, e.getMessage(), null);
    }
}
