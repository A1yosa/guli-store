package com.jay.gulistore.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jay.common.utils.PageUtils;
import com.jay.gulistore.ware.entity.PurchaseEntity;

import java.util.Map;


/**
 * 采购信息
 *
 * @author jay
 * @email huanchen659@gmail.com
 * @date 2023-01-20 17:57:44
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

