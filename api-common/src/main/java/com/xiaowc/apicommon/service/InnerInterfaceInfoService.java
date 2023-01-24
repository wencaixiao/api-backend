package com.xiaowc.apicommon.service;

import com.xiaowc.apicommon.model.entity.InterfaceInfo;

/**
 * @author xiaowc
 * @description 针对表【interface_info(接口信息)】的数据库操作Service
 * @createDate 2023-01-15 10:01:01
 */
public interface InnerInterfaceInfoService {

    /**
     * 获取接口信息
     *
     * 从数据库中查询模拟接口是否存在(传入请求路径、请求方法、请求参数，返回接口信息)
     * @param url 请求路径
     * @param method 请求方法
     * @return 返回接口信息
     */
    InterfaceInfo getInterfaceInfo(String url, String method);
}
