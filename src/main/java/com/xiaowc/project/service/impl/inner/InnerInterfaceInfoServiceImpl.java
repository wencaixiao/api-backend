package com.xiaowc.project.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiaowc.project.common.ErrorCode;
import com.xiaowc.project.exception.BusinessException;
import com.xiaowc.project.mapper.InterfaceInfoMapper;
import com.xiaowc.apicommon.model.entity.InterfaceInfo;
import com.xiaowc.apicommon.service.InnerInterfaceInfoService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * 实现api-common项目中的InnerInterfaceInfoService接口
 *
 * 要使用dubbo必须在实现类上加上这个注解，表示提供给其他项目调用的接口
 * 要想使用这个接口，必须在要使用的地方引入这个类，使用@DubboReference注解来引入这个类即可
 */
@DubboService  // 要使用dubbo必须在实现类上加上这个注解，表示提供给其他项目调用的接口
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {

    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;

    /**
     * 获取接口信息
     *
     * 从数据库中查询模拟接口是否存在(传入请求路径、请求方法、请求参数，返回接口信息)
     * @param url 请求路径
     * @param method 请求方法
     * @return 返回接口信息
     */
    @Override
    public InterfaceInfo getInterfaceInfo(String url, String method) {
        if (StringUtils.isAnyBlank(url, method)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("url", url);
        queryWrapper.eq("method", method);
        return interfaceInfoMapper.selectOne(queryWrapper);
    }
}
