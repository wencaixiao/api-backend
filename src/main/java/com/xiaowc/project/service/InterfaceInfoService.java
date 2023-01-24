package com.xiaowc.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaowc.apicommon.model.entity.InterfaceInfo;

/**
 * @author xiaowc
 * @description 针对表【interface_info(接口信息)】的数据库操作Service
 * @createDate 2023-01-15 10:01:01
 */
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    /**
     * 校验接口信息
     * @param interfaceInfo
     * @param add 是否为创建校验
     */
    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);
}
