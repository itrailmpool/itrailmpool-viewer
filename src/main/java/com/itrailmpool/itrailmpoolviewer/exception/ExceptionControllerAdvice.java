package com.itrailmpool.itrailmpoolviewer.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Order(0)
public class ExceptionControllerAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionControllerAdvice.class);

    @ExceptionHandler(value = MiningPoolViewerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleError(HttpServletRequest request, MiningPoolViewerException e) {
        LOGGER.error("{}: {}, method={}, url={}", HttpStatus.INTERNAL_SERVER_ERROR.name(), e.getMessage(),
                request.getMethod(), request.getRequestURI(), e);

        return new ErrorMessage()
                .setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.name())
                .setDebugMessage(e.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleError(HttpServletRequest request, Exception e) {
        LOGGER.error("{}: {}, method={}, url={}", HttpStatus.INTERNAL_SERVER_ERROR.name(), e.getMessage(),
                request.getMethod(), request.getRequestURI(), e);

        return new ErrorMessage()
                .setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.name())
                .setDebugMessage(e.getMessage());
    }



}
