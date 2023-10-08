package com.mo.common.config.execption;

import com.mo.common.result.ResultCodeEnum;
import lombok.Data;

/**
 * author mozihao
 * create 2023-10-03 17:17
 * Description
 */
@Data
public class MoException extends RuntimeException{
    private Integer code;//状态码
    private String message;//描述

    public MoException(Integer code,String message){
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 接收枚举类型对象
     * @param resultCodeEnum
     */
    public MoException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
        this.message = resultCodeEnum.getMessage();
    }

    @Override
    public String toString() {
        return "MoException{" +
                "code=" + code +
                ", message=" + this.getMessage() +
                '}';
    }
}
