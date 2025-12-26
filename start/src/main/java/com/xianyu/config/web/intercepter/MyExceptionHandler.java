package com.xianyu.config.web.intercepter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.xianyu.component.dto.MyResponseDto;
import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.DispatcherServlet;

@RestControllerAdvice
@ConditionalOnClass({Servlet.class, DispatcherServlet.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Slf4j
public class MyExceptionHandler {

    public static final String PARAM_ERROR = "PARAM_ERROR";

    @ResponseBody
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public MyResponseDto<Void> argumentMissingError(HttpServletRequest request,
            HttpServletResponse response, Object handler, Exception ex) {
        return new MyResponseDto<>(PARAM_ERROR, ex.getMessage(), null);
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public MyResponseDto<Map<String, String>> methodArgumentNotValidException(HttpServletRequest request,
            HttpServletResponse response, Object handler, MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new MyResponseDto<>(PARAM_ERROR, "参数错误", errors);
    }

    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    public MyResponseDto<Set<String>> constraintViolationException(HttpServletRequest request,
            HttpServletResponse response, Object handler, ConstraintViolationException ex) {
        Set<String> errorMsgs = new HashSet<>();
        ex.getConstraintViolations().forEach(constraintViolation -> {
            String message = constraintViolation.getMessage();
            log.error("{} {}", constraintViolation.getPropertyPath().toString(), message);
            errorMsgs.add(message);
        });
        return new MyResponseDto<>(PARAM_ERROR, "参数错误", errorMsgs);
    }

}
