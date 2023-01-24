package com.xiaowc.project.model.dto.post;

import com.xiaowc.project.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * dto层：业务封装类
 *   用来接收从前端传过来的数据的对象
 *
 * 查询请求
 *
 * @author xiaowc
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PostQueryRequest extends PageRequest implements Serializable {

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 性别（0-男, 1-女）
     */
    private Integer gender;

    /**
     * 学历
     */
    private String education;

    /**
     * 地点
     */
    private String place;

    /**
     * 职业
     */
    private String job;

    /**
     * 联系方式
     */
    private String contact;

    /**
     * 感情经历
     */
    private String loveExp;

    /**
     * 内容（个人介绍），支持模糊查询
     */
    private String content;

    /**
     * 状态（0-待审核, 1-通过, 2-拒绝）
     */
    private Integer reviewStatus;

    /**
     * 创建用户 id
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}