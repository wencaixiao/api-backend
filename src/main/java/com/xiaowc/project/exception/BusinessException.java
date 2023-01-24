package com.xiaowc.project.exception;

import com.xiaowc.project.common.ErrorCode;

/**
 * 定义业务异常处理类
 * 其实就是给原本的运行时异常类扩充了两个字段，并且增加了构造函数，使其支持传递ErrorCode
 *   1.相对于java的异常类，支持更多的字段
 *   2.自定义构造函数，更灵活/快捷的设置字段
 *
 * @author xiaowc
 */
public class BusinessException extends RuntimeException {

    /**
     * 状态码
     */
    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode) { // errorCode传入的是ErrorCode枚举类中的某个枚举值
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    public int getCode() {
        return code;
    }
}
