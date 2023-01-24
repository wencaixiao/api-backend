package com.xiaowc.project.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiaowc.project.common.ErrorCode;
import com.xiaowc.project.exception.BusinessException;
import com.xiaowc.project.mapper.UserMapper;
import com.xiaowc.apicommon.model.entity.User;
import com.xiaowc.apicommon.service.InnerUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * 实现api-common项目中的InnerUserService接口
 *
 * 要使用dubbo必须在实现类上加上这个注解，表示提供给其他项目调用的接口
 * 要想使用这个接口，必须在要使用的地方引入这个类，使用@DubboReference注解来引入这个类即可
 */
@DubboService  //
public class InnerUserServiceImpl implements InnerUserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 获取调用者的信息
     *
     * 数据库中是否已分配给用户密钥(根据accessKey拿到用户信息，返回用户信息，为空表示不存在)
     *   根据accessKey判断用户是否存在，查到secretKey
     * @param accessKey 用户的表示
     * @return 返回用户的信息
     */
    @Override
    public User getInvokeUser(String accessKey) {
        if (StringUtils.isAnyBlank(accessKey)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("accessKey", accessKey);
        return userMapper.selectOne(queryWrapper);
    }
}
