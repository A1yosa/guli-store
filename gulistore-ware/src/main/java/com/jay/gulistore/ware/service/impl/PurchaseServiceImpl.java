package com.jay.gulistore.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.jay.common.constant.WareConstant;
import com.jay.common.utils.R;
import com.jay.gulistore.ware.entity.PurchaseDetailEntity;
import com.jay.gulistore.ware.feign.ProductFeignService;
import com.jay.gulistore.ware.service.PurchaseDetailService;
import com.jay.gulistore.ware.service.WareSkuService;
import com.jay.gulistore.ware.vo.MergerVo;
import com.jay.gulistore.ware.vo.PurchaseDoneVo;
import com.jay.gulistore.ware.vo.PurchaseItemDoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jay.common.utils.PageUtils;
import com.jay.common.utils.Query;

import com.jay.gulistore.ware.dao.PurchaseDao;
import com.jay.gulistore.ware.entity.PurchaseEntity;
import com.jay.gulistore.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;

@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    private PurchaseDetailService detailService;

    @Autowired
    private WareSkuService wareSkuService;

    @Autowired
    private ProductFeignService productFeignService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceive(Map<String, Object> params) {

        QueryWrapper<PurchaseEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("status", WareConstant.PurchaseStatusEnum.CREATED.getCode()).or().eq("status", WareConstant.PurchaseStatusEnum.ASSIGNED.getCode());

        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);

    }

    @Transactional
    @Override
    public void mergePurchase(MergerVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();
        // ????????????id???null ?????????????????????
        if (purchaseId == null){
            //???????????????
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }
        //??????????????????
        List<Long> items = mergeVo.getItems();

        Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> list = detailService.getBaseMapper().selectBatchIds(items).stream().filter(entity -> {
            //????????????????????????????????????????????????????????????
            return entity.getStatus() < WareConstant.PurchaseDetailStatusEnum.BUYING.getCode()
                    || entity.getStatus() == WareConstant.PurchaseDetailStatusEnum.HASERROR.getCode();
        }).map(entity -> {
            //??????????????????????????????id
            entity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
            entity.setPurchaseId(finalPurchaseId);
            return entity;
        }).collect(Collectors.toList());

        detailService.updateBatchById(list);

        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(purchaseId);
        purchaseEntity.setUpdateTime(new Date());
        purchaseEntity.setCreateTime(new Date());
        this.updateById(purchaseEntity);

    }

    @Transactional
    @Override
    public void received(List<Long> ids) {
        // ?????????????????????????????????????????????????????????
        if (ids == null || ids.size() == 0) {
            return;
        }

        List<PurchaseEntity> list = this.getBaseMapper().selectBatchIds(ids).stream().filter(entity -> {
            //????????????????????????????????????????????????
            return entity.getStatus() <= WareConstant.PurchaseStatusEnum.ASSIGNED.getCode();
        }).map(entity -> {
            //????????????????????????????????????
            entity.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode());
            return entity;
        }).collect(Collectors.toList());
        this.updateBatchById(list);

        //??????????????????????????????????????????????????????????????????
        UpdateWrapper<PurchaseDetailEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("purchase_id", ids);
        PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
        purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
        detailService.update(purchaseDetailEntity, updateWrapper);

    }

    @Transactional
    @Override
    public void done(PurchaseDoneVo vo) {
        //1???????????????????????????????????????????????????????????????
        List<PurchaseItemDoneVo> items = vo.getItems();
        List<PurchaseDetailEntity> updateList = new ArrayList<>();
        boolean flag = true;
        for (PurchaseItemDoneVo item : items){
            Long detailId = item.getItemId();
            PurchaseDetailEntity detailEntity = detailService.getById(detailId);
            detailEntity.setStatus(item.getStatus());
            //??????????????????
            if (item.getStatus() == WareConstant.PurchaseDetailStatusEnum.HASERROR.getCode()){
                flag = false;
            }else {
                //3?????????????????????????????????????????????
                // sku_id, sku_num, ware_id
                // sku_id, ware_id, stock sku_name(????????????????????????), stock_locked(?????????????????????????????????????????????????????????)
                String skuName = "";
                try {
                    R info = productFeignService.info(detailEntity.getSkuId());
                    if(info.getCode() == 0){
                        Map<String,Object> data=(Map<String,Object>)info.get("skuInfo");
                        skuName = (String) data.get("skuName");
                    }
                } catch (Exception e) {

                }
                //????????????
                wareSkuService.addStock(detailEntity.getSkuId(), detailEntity.getWareId(), skuName, detailEntity.getSkuNum());
            }
            updateList.add(detailEntity);
        }
        //??????????????????
        detailService.updateBatchById(updateList);
        //2?????????????????????????????????????????????????????????
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(vo.getId());
        purchaseEntity.setStatus(flag ? WareConstant.PurchaseStatusEnum.FINISH.getCode() : WareConstant.PurchaseStatusEnum.HASERROR.getCode());
        this.updateById(purchaseEntity);
    }

}