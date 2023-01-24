package com.xiaowc.project.service.impl.inner;

import com.xiaowc.project.service.UserInterfaceInfoService;
import com.xiaowc.apicommon.service.InnerUserInterfaceInfoService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * 实现api-common项目中的InnerUserInterfaceInfoService接口
 *
 * 要使用dubbo必须在实现类上加上这个注解，表示提供给其他项目调用的接口
 * 要想使用这个接口，必须在要使用的地方引入这个类，使用@DubboReference注解来引入这个类即可
 */
@DubboService  // 要使用dubbo必须在实现类上加上这个注解，表示提供给其他项目调用的接口
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    /**
     * 调用接口统计
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        return userInterfaceInfoService.invokeCount(interfaceInfoId, userId);
    }
}
