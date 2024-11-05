package cn.atlantt1c.model.common;

import lombok.Data;

/**
 * 分页
 */
@Data
public class BaseDto {

    /**
     * 当前页数
     */
    private Integer page;

    /**
     * 每页条数
     */
    private Integer rows;

}
