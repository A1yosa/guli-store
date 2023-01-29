package com.jay.gulistore.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jay.common.utils.PageUtils;
import com.jay.gulistore.ware.entity.WareOrderTaskEntity;

import java.util.Map;


/**
 * 库存工作单
 *
 * @author jay
 * @email huanchen659@gmail.com
 * @date 2023-01-20 17:57:44
 */
public interface WareOrderTaskService extends IService<WareOrderTaskEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

