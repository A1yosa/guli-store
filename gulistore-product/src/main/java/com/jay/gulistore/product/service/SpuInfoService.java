package com.jay.gulistore.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jay.common.utils.PageUtils;
import com.jay.gulistore.product.entity.SpuInfoDescEntity;
import com.jay.gulistore.product.entity.SpuInfoEntity;
import com.jay.gulistore.product.vo.SpuSaveVo;

import java.util.Map;


/**
 * spu信息
 *
 * @author jay
 * @email huanchen659@gmail.com
 * @date 2023-01-20 14:25:14
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuSaveVo vo);

    void saveBaseSpuInfo(SpuInfoEntity infoEntity);

    PageUtils queryPageByCondition(Map<String, Object> params);

    void up(Long spuId);

    SpuInfoEntity getSpuInfoBySkuId(Long skuId);



//    void saveSpuInfoDesc(SpuInfoDescEntity descEntity);

}

