package com.xiaowc.apiinterface.aop;

import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 调用次数切面
 * AOP的作用：
 *   就是在我们调用某个接口前、后替我们先去做一些事情(比如执行一个方法等等)，动态代理
 * 使用AOP切面的优点：独立于接口，在每个接口调用后统计次数+1
 * 使用AOP切面的缺点：只存在于单个项目中，如果每个团队都要开发自己的模拟接口，那么都要写一个切面
 */
@RestControllerAdvice
public class InvokeCountAOP {

//    @Resource
//    private UserInterfaceInfoService userInterfaceInfoService;
//
//    // 伪代码
//    // 定义切面触发的事件(什么时候执行方法)controller接口的方法执行成功后，执行下述方法
//    public void doInvokeCount() {
//        // 调用方法
//        Object.proceed();
//        // 调用成功后，次数+1
//        userInterfaceInfoService.invokeCount();
//    }
}
