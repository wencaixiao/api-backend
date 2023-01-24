package com.xiaowc.project.common;

/**
 * 返回工具类
 *
 * @author xiaowc
 */
public class ResultUtils {

    /**
     * 成功返回
     * @param data 要返回的数据
     * @param <T> 使用泛型，代表这是一个通用返回类，提高整个对象的复用性
     * @return
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "ok");
    }

    /**
     * 返回失败
     * @param errorCode 错误码，枚举类
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    /**
     * 返回失败
     * @param code 错误码，枚举类
     * @param message 错误的简短描述信息
     * @return
     */
    public static BaseResponse error(int code, String message) {
        return new BaseResponse(code, null, message);
    }

    /**
     * 返回失败
     * @param errorCode 错误码，枚举类
     * @param message 错误的简短描述信息
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode, String message) {
        return new BaseResponse(errorCode.getCode(), null, message);
    }
}
