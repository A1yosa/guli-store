package com.jay.gulistore.ware.vo;

import lombok.Data;

@Data
public class PurchaseItemDoneVo {
    private Long itemId;
    private Integer status;
    private String reason;
}
