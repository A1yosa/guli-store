package com.jay.gulistore.product.recommend;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelateDTO {
    //用户id
//    private Integer userId;
    //业务id
//    private Integer foodId;
    //指数
//    private Integer index;

    /**
     * id
     */
    @TableId
    private Integer id;
    /**
     * sku_id
     */
    private Integer skuId;
    /**
     * 星级
     */
    private Integer star;

}
