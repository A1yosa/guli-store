package com.jay.gulistore.product.vo;

import com.jay.gulistore.product.entity.SkuImagesEntity;
import com.jay.gulistore.product.entity.SkuInfoEntity;
import com.jay.gulistore.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

@Data
public class SkuItemVo {

    //1、sku基本信息获取    pms_sku_info
    SkuInfoEntity info;

    boolean hasStock = true;

    //2、sku的图片信息      pms_sku_images
    List<SkuImagesEntity> images;

    //3、获取spu的销售属性组合
    List<SkuItemSaleAttrVo> saleAttr;

    //4、获取spu的介绍
    SpuInfoDescEntity desc;

    //5、获取spu的规格参数信息
    List<SpuItemAttrGroupVo> groupAttrs;


}
