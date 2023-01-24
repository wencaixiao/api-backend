package com.xiaowc.apicommon.model.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.xiaowc.apicommon.model.entity.Post;

/**
 * VO层：返回给前端数据的封装类
 *
 * 帖子视图
 *
 * @author xiaowc
 * @TableName product
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PostVO extends Post {

    /**
     * 是否已点赞
     */
    private Boolean hasThumb;

    private static final long serialVersionUID = 1L;
}