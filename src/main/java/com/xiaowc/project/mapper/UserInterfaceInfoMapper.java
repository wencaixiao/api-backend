package com.xiaowc.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaowc.apicommon.model.entity.UserInterfaceInfo;

import java.util.List;

/**
 * @author xiaowc
 * @description 针对表【user_interface_info(用户调用接口关系表)】的数据库操作Mapper
 * @createDate 2023-01-17 17:33:10
 * @Entity generator.domain.UserInterfaceInfo
 */
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo> {

    /**
     * 返回调用次数前limit的接口信息
     *
     * 1.SQL查询调用数据：
     *  select interfaceInfoId,sum(totalNum) as totalNum from user_interface_info group by interfaceInfoId order by totalNum desc limit 3;
     * 2.关联查询接口信息
     * @return
     */
    List<UserInterfaceInfo> listTopInvokeInterfaceInfo(int limit);
}




