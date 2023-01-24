package com.xiaowc.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiaowc.project.annotation.AuthCheck;
import com.xiaowc.project.common.BaseResponse;
import com.xiaowc.project.common.ErrorCode;
import com.xiaowc.project.common.ResultUtils;
import com.xiaowc.project.exception.BusinessException;
import com.xiaowc.project.mapper.UserInterfaceInfoMapper;
import com.xiaowc.project.model.vo.InterfaceInfoVO;
import com.xiaowc.project.service.InterfaceInfoService;
import com.xiaowc.apicommon.model.entity.InterfaceInfo;
import com.xiaowc.apicommon.model.entity.UserInterfaceInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分析控制器
 */
@RestController
@RequestMapping("/analysis")
@Slf4j
public class AnalysisController {

    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    @Resource
    private InterfaceInfoService interfaceInfoService;

    /**
     * 返回调用次数前几的接口信息
     *
     * 1.SQL查询调用数据：
     *  select interfaceInfoId,sum(totalNum) as totalNum from user_interface_info group by interfaceInfoId order by totalNum desc limit 3;
     * 2.关联查询接口信息
     * @return
     */
    @GetMapping("/top/interface/invoke")
    @AuthCheck(mustRole = "admin")  // 只有管理员才可以查看
    public BaseResponse<List<InterfaceInfoVO>> listTopInvokeInterfaceInfo() {
        List<UserInterfaceInfo> userInterfaceInfoList = userInterfaceInfoMapper.listTopInvokeInterfaceInfo(3); // 返回调用次数前limit的接口信息
        Map<Long, List<UserInterfaceInfo>> interfaceInfoIdObjMap = userInterfaceInfoList.stream()
                .collect(Collectors.groupingBy(UserInterfaceInfo::getInterfaceInfoId)); // 根据interfaceInfoId对接口信息进行分组
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", interfaceInfoIdObjMap.keySet()); // 查询id在这个列表中的这些，interfaceInfoIdObjMap.keySet()拿到前limit的接口信息的id
        List<InterfaceInfo> list = interfaceInfoService.list(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        List<InterfaceInfoVO> interfaceInfoVOList = list.stream().map(interfaceInfo -> {
            InterfaceInfoVO interfaceInfoVO = new InterfaceInfoVO();
            BeanUtils.copyProperties(interfaceInfo, interfaceInfoVO); // 将原本interfaceInfo对象中的属性赋给interfaceInfoVO
            int totalNum = interfaceInfoIdObjMap.get(interfaceInfo.getId()).get(0).getTotalNum(); // 查到调用次数
            interfaceInfoVO.setTotalNum(totalNum);
            return interfaceInfoVO;
        }).collect(Collectors.toList()); // 转成一个列表
        return ResultUtils.success(interfaceInfoVOList);
    }
}
