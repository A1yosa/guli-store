package com.jay.gulistore.cart.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class Cart {
    private List<CartItem> items;

    /*** 商品的数量*/
    private Integer countNum;
    /*** 商品的类型数量*/
    private Integer countType;

    /*** 整个购物车的总价*/
    private BigDecimal totalAmount;

    /*** 减免的价格*/
    private BigDecimal reduce = new BigDecimal("0.00");

    /*** 计算商品的总量*/
    public Integer getCountNum() {
        int count = 0;
        if(items != null && items.size() > 0){
            for (CartItem item : this.items) {
                count += item.getCount();
            }
        }
        return count;
    }

    public Integer getCountType() {
        int count = 0;
        if(this.items != null && this.items.size() > 0){
            for (CartItem item : this.items) {
                count += 1;
            }
        }
        return count;
    }

    public BigDecimal getTotalAmount() {
        BigDecimal amount = new BigDecimal("0");
        if(this.items != null && this.items.size() > 0){
            for (CartItem item : this.items) {
                if(item.getCheck()){
                    BigDecimal totalPrice = item.getTotalPrice();
                    amount = amount.add(totalPrice);
                }
            }
        }
        return amount.subtract(this.getReduce());
    }
}
