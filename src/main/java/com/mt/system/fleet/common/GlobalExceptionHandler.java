package com.mt.system.fleet.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    // private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = BaseBusiException.class)
    @ResponseBody
    public ApiResponse baseBusi(HttpServletRequest request, HttpServletResponse response, BaseBusiException ex) {
        return new ApiResponse(ex.getResponseCode());
    }

}