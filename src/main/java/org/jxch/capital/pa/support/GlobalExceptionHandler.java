package org.jxch.capital.pa.support;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<String> defaultErrorHandler(@NonNull Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("发生错误：" + e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = NoHandlerFoundException.class)
    public ResponseEntity<String> noHandlerFoundHandler(@NonNull Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("没有找到处理器：" + e.getMessage());
    }

}
