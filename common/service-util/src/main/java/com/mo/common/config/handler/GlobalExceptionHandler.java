package com.mo.common.config.handler;

import com.mo.common.config.execption.MoException;
import com.mo.common.result.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * author mozihao
 * create 2023-10-03 17:07
 * Description
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    //全局异常处理执行的方法
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result error(Exception e){
        e.printStackTrace();
        return Result.fail().message("执行全局异常处理...");
    }

    //特定异常处理
    @ExceptionHandler(ArithmeticException.class)
    @ResponseBody
    public Result error(ArithmeticException e){
        e.printStackTrace();
        return Result.fail().message("执行特定异常处理...");
    }
    //自定义异常处理
    @ExceptionHandler(MoException.class)
    @ResponseBody
    public Result error(MoException e){
        e.printStackTrace();
        return Result.fail().code(e.getCode()).message(e.getMessage());
    }
}
