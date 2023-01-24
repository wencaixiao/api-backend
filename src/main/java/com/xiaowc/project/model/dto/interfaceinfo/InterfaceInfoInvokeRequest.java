package com.xiaowc.project.model.dto.interfaceinfo;

import lombok.Data;

import java.io.Serializable;

/**
 * dto层：业务封装类
 *   用来接收从前端传过来的数据的对象
 *
 * 接口调用请求
 *
 * @TableName product
 */
@Data
public class InterfaceInfoInvokeRequest implements Serializable {

    /**
     * 主键
     */
    private Long id;

    /**
     * 用户传递的请求参数
     * [
     *  {"name": "username", "type": "string"}
     * ]
     */
    private String userRequestParams;

    private static final long serialVersionUID = 1L;
}