package com.mdd.bootInit.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 删除请求体
 * @author Mdd
 * @Github <a href="https://github.com/mdd123cc"/>
 * @Gitee <a href="https://gitee.com/mdd115192"/>
 */
@Data
public class DeleteRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}
