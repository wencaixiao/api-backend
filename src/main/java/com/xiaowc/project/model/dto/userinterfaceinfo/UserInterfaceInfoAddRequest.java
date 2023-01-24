package com.xiaowc.project.model.dto.userinterfaceinfo;

import lombok.Data;

import java.io.Serializable;

/**
 * dto层：业务封装类
 *   用来接收从前端传过来的数据的对象
 *
 * 创建请求
 *
 * @TableName product
 */
@Data
public class UserInterfaceInfoAddRequest implements Serializable {

    /**
     * 调用用户 id
     */
    private Long userId;

    /**
     * 接口 id
     */
    private Long interfaceInfoId;

    /**
     * 总调用次数
     */
    private Integer totalNum;

    /**
     * 剩余调用次数
     */
    private Integer leftNum;

}