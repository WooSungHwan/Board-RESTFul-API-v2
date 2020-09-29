package com.example.board_v2.configuration;

import com.example.board_v2.configuration.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

    /**
     * 엔티티를 조회하였을 때, 찾지 못한 경우 에러처리
     * @param e
     * @param req
     * @return
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorResponse handlerEntityNotFoundException(EntityNotFoundException e, HttpServletRequest req) {
        log.error("===================== Handler EntityNotFoundException =====================");
        e.printStackTrace();
        return new ErrorResponse(HttpStatus.NOT_FOUND, e.getMessage(), null);
    }

    /**
     * RuntimeException이 발생한 경우 처리.
     * @param e
     * @param req
     * @return
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RuntimeException.class)
    public ErrorResponse handlerRuntimeException(RuntimeException e, HttpServletRequest req) {
        log.error("===================== Handler RuntimeException =====================");
        e.printStackTrace();
        return new ErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), null);
    }

    /**
     * HTTP METHOD를 잘못 요청한 경우 처리.
     * @param e
     * @param req
     * @return
     */
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(MethodNotAllowedException.class)
    public ErrorResponse handlerMethodNotAllowedException(MethodNotAllowedException e, HttpServletRequest req) {
        log.error("===================== Handler MethodNotAllowedException =====================");
        e.printStackTrace();
        return new ErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, e.getMessage(), null);
    }

    /**
     * Media Type을 지원하지 않는 경우로 요청한 경우 처리
     * @param e
     * @param req
     * @return
     */
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(UnsupportedMediaTypeStatusException.class)
    public ErrorResponse handlerUnsupportedMediaTypeStatusException(UnsupportedMediaTypeStatusException e, HttpServletRequest req) {
        log.error("===================== Handler UnsupportedMediaTypeStatusException =====================");
        e.printStackTrace();
        return new ErrorResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE, e.getMessage(), null);
    }

    /**
     * 그 외의 에러들을 전부 처리.
     * @param e
     * @param req
     * @return
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handlerException(Exception e, HttpServletRequest req) {
        log.error("===================== Handler Exception =====================");
        e.printStackTrace();
        return new ErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), null);
    }

}
