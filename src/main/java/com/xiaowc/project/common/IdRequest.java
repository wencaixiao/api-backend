package com.xiaowc.project.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用对象，接收来自前端传过来的数据
 *
 * 发布接口、下线接口要传的接口的id
 *
 * @author xiaowc
 */
@Data
public class IdRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}