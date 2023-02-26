package com.jay.gulistore.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jay.common.utils.PageUtils;
import com.jay.gulistore.ware.entity.WareSkuEntity;
import com.jay.gulistore.ware.vo.SkuHasStockVo;

import java.util.List;
import java.util.Map;


/**
 * 商品库存
 *
 * @author jay
 * @email huanchen659@gmail.com
 * @date 2023-01-20 17:57:44
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, String skuName, Integer skuNum);

    List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds);

}

