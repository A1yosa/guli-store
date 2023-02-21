package com.jay.gulistore.coupon.service.impl;

import com.jay.common.to.MemberPrice;
import com.jay.common.to.SkuReductionTo;
import com.jay.gulistore.coupon.entity.MemberPriceEntity;
import com.jay.gulistore.coupon.entity.SkuLadderEntity;
import com.jay.gulistore.coupon.service.MemberPriceService;
import com.jay.gulistore.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jay.common.utils.PageUtils;
import com.jay.common.utils.Query;

import com.jay.gulistore.coupon.dao.SkuFullReductionDao;
import com.jay.gulistore.coupon.entity.SkuFullReductionEntity;
import com.jay.gulistore.coupon.service.SkuFullReductionService;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    SkuLadderService skuLadderService;

    @Autowired
    MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuReduction(SkuReductionTo reductionTo) {

        //1、保存满减打折，会员价 sms_sku_ladder
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        skuLadderEntity.setSkuId(reductionTo.getSkuId());
        skuLadderEntity.setAddOther(reductionTo.getCountStatus());
        skuLadderEntity.setFullCount(reductionTo.getFullCount());
        skuLadderEntity.setDiscount(reductionTo.getDiscount());
        skuLadderService.save(skuLadderEntity);

        //2、保存满减信息 sms_sku_full_reduction
        SkuFullReductionEntity reductionEntity = new SkuFullReductionEntity();
//        reductionEntity.setSkuId(skuReductionTo.getSkuId());
//        reductionEntity.setFullPrice(skuReductionTo.getFullPrice());
//        reductionEntity.setReducePrice(skuReductionTo.getReducePrice());
//        reductionEntity.setAddOther(skuReductionTo.getPriceStatus());
        BeanUtils.copyProperties(reductionTo,reductionEntity);
        this.save(reductionEntity);


        //3、保存会员价格 sms_member_price
        List<MemberPrice> memberPrice = reductionTo.getMemberPrice();
        List<MemberPriceEntity> collect = memberPrice.stream().map(item -> {
            MemberPriceEntity priceEntity = new MemberPriceEntity();
            priceEntity.setSkuId(reductionTo.getSkuId());
            priceEntity.setMemberLevelId(item.getId());
            priceEntity.setMemberPrice(item.getPrice());
            priceEntity.setMemberLevelName(item.getName());
            priceEntity.setAddOther(1);
            return priceEntity;
        }).collect(Collectors.toList());
        memberPriceService.saveBatch(collect);

    }

}