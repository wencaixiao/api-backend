package com.xiaowc.project.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用对象，接收来自前端传过来的数据
 *
 * 删除请求
 *
 * @author xiaowc
 */
@Data
public class DeleteRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}