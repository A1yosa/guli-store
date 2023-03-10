package com.jay.gulistore.ware.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jay.common.utils.PageUtils;
import com.jay.common.utils.Query;

import com.jay.gulistore.ware.dao.PurchaseDetailDao;
import com.jay.gulistore.ware.entity.PurchaseDetailEntity;
import com.jay.gulistore.ware.service.PurchaseDetailService;
import org.springframework.util.StringUtils;

@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<PurchaseDetailEntity> wrapper = new QueryWrapper<>();

        String key  = (String) params.get("key");
        if (!StringUtils.isEmpty(key)){
            wrapper.and(w -> {
                w.eq("sku_id", key).or().eq("purchase_id", key);
            });
        }

        String status  = (String) params.get("status");
        if (!StringUtils.isEmpty(status)){
            wrapper.eq("status", status);
        }

        String wareId  = (String) params.get("wareId");
        if (!StringUtils.isEmpty(wareId)){
            wrapper.eq("ware_id", wareId);
        }

        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);

    }

}