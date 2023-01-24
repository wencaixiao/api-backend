package com.xiaowc.project.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * dto层：业务封装类
 *   用来接收从前端传过来的数据的对象
 *
 * 用户登录请求体
 *
 * @author xiaowc
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * 用户账户
     */
    private String userAccount;

    /**
     * 用户密码
     */
    private String userPassword;
}
