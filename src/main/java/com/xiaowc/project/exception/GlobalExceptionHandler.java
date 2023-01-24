package com.xiaowc.project.exception;

import com.xiaowc.project.common.BaseResponse;
import com.xiaowc.project.common.ErrorCode;
import com.xiaowc.project.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 编写全局异常处理器
 *
 * 作用：
 *   1.捕获代码中所有的异常，内部消化，集中处理，让前端得到更详细的业务报错/信息
 *   2.同时屏蔽掉项目框架本身的异常(不暴露服务器内部状态)
 *   3.集中处理，比如记录日志
 * 实现：
 *   1.Spring AOP: 在调用方法前后进行额外的处理
 *
 * @author xiaowc
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 这个注解代表这个方法只捕获BusinessException这个异常
     * @param e
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("businessException: " + e.getMessage(), e);
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    /**
     * 这个方法捕获Java系统内部抛出来的自带的异常
     * @param e
     * @return
     */
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("runtimeException", e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, e.getMessage());
    }
}
