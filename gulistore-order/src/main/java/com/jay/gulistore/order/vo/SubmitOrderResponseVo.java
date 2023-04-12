package com.jay.gulistore.order.vo;

import com.jay.gulistore.order.entity.OrderEntity;
import lombok.Data;

@Data
public class SubmitOrderResponseVo {
    private OrderEntity order;

    /**
     * 错误状态码： 0----成功
     * 1 库存不足
     * 2 验证失败
     */
    private Integer code;
}
