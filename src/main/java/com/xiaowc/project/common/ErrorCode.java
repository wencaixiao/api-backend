package com.xiaowc.project.common;

/**
 * 通用对象
 *
 * 错误码：枚举类
 * 目的：给对象补充一些信息，告诉前端这个请求在业务层面上是成功还是失败，200、404、500、502、503
 * 原来：
 *   返回 {
 *     "nama": "xiaowc"
 * }
 * 现在：
 *   成功 {
 *     "code": 0 //业务状态码
 *     "data": {
 *         "name": "xiaowc"
 *     }
 *     "message": "ok"
 *     "description": "message信息更详细的描述"
 * }
 *   失败 {
 *     "code": 50001 //业务状态码
 *     "data": null
 *     "message": "用户操作异常，xxx"
 *     "description": "message信息更详细的描述"
 * }
 *
 * @author xiaowc
 */
public enum ErrorCode {

    SUCCESS(0, "ok"),
    PARAMS_ERROR(40000, "请求参数错误"),
    NOT_LOGIN_ERROR(40100, "未登录"),
    NO_AUTH_ERROR(40101, "无权限"),
    NOT_FOUND_ERROR(40400, "请求数据不存在"),
    FORBIDDEN_ERROR(40300, "禁止访问"),
    SYSTEM_ERROR(50000, "系统内部异常"),
    OPERATION_ERROR(50001, "操作失败");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
