package com.itheima.reggie.exception;

import com.itheima.reggie.common.R;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sun.nio.cs.ext.MacCentralEurope;

import java.sql.SQLIntegrityConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionConfig {
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandle(SQLIntegrityConstraintViolationException exception) {
        if (exception.getMessage().contains("Duplicate entry")) {
            String[] split = exception.getMessage().split(" ");
            return R.error(split[2] + "已存在");
        }
        return R.error("未知错误");
    }
    @ExceptionHandler(BusinessException.class)
    public R<String> businessExceptionHandle(BusinessException exception){
        String message = exception.getMessage();
        return R.error(message);
    }


}
