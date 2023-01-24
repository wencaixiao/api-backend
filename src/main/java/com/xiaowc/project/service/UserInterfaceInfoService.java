package com.xiaowc.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaowc.apicommon.model.entity.UserInterfaceInfo;

/**
 * @author xiaowc
 * @description 针对表【user_interface_info(帖子)】的数据库操作Service
 */
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {
    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add);

    /**
     * 调用接口统计
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean invokeCount(long interfaceInfoId, long userId);
}
