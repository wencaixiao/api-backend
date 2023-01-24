package com.xiaowc.apicommon.service;

import com.xiaowc.apicommon.model.entity.User;


/**
 * 用户服务
 *
 * @author xiaowc
 */
public interface InnerUserService {

    /**
     * 获取调用者的信息
     *
     * 数据库中是否已分配给用户密钥(根据accessKey拿到用户信息，返回用户信息，为空表示不存在)
     *   根据accessKey判断用户是否存在，查到secretKey
     * @param accessKey 用户的表示
     * @return 返回用户的信息
     */
    User getInvokeUser(String accessKey);
}
