package com.jay.gulistore.product.service.impl;

import com.jay.gulistore.product.entity.SkuImagesEntity;
import com.jay.gulistore.product.entity.SpuInfoDescEntity;
import com.jay.gulistore.product.service.*;
import com.jay.gulistore.product.vo.SkuItemSaleAttrVo;
import com.jay.gulistore.product.vo.SkuItemVo;
import com.jay.gulistore.product.vo.SpuItemAttrGroupVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jay.common.utils.PageUtils;
import com.jay.common.utils.Query;

import com.jay.gulistore.product.dao.SkuInfoDao;
import com.jay.gulistore.product.entity.SkuInfoEntity;
import org.springframework.util.StringUtils;

@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    SkuImagesService imagesService;

    @Autowired
    SpuInfoDescService spuInfoDescService;

    @Autowired
    AttrGroupService attrGroupService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    ThreadPoolExecutor executor;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.baseMapper.insert(skuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {

        QueryWrapper<SkuInfoEntity> queryWrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)){

            queryWrapper.and((wapper)->{
                wapper.eq("sku_id",key).or().like("sku_name",key);
            });
        }

        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)){

            queryWrapper.eq("catelog_id",catelogId);
        }

        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)){

            queryWrapper.eq("brand_id",brandId);
        }

        String min = (String) params.get("min");
        if (!StringUtils.isEmpty(min)){

            queryWrapper.ge("price",min);
        }

        String max = (String) params.get("max");

        if (!StringUtils.isEmpty(max) ){
            try {
                BigDecimal bigDecimal = new BigDecimal(max);
                if (bigDecimal.compareTo(new BigDecimal("0"))==1){
                    queryWrapper.le("price",max);
                }

            }catch (Exception e){

            }

        }

        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                queryWrapper

                );

        return new PageUtils(page);

    }

    @Override
    public List<SkuInfoEntity> getSkusBySpuId(Long spuId) {

        List<SkuInfoEntity> list = this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id",spuId));
        return list;
    }

    @Override
    public SkuItemVo item(Long skuId) throws ExecutionException, InterruptedException {

        SkuItemVo skuItemVo = new SkuItemVo();

        CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {
            //1、sku基本信息获取    pms_sku_info
            SkuInfoEntity info = getById(skuId);
            skuItemVo.setInfo(info);
            return info;
        }, executor);


//        Long catelogId = info.getCatelogId();
//        Long spuId = info.getSpuId();


//        //2、sku的图片信息      pms_sku_images
//        List<SkuImagesEntity> images = imagesService.getImagesBySkuId(skuId);
//        skuItemVo.setImages(images);
//
//        //3、获取spu的销售属性组合
//        List<SkuItemSaleAttrVo> saleAttrVos = skuSaleAttrValueService.getSaleAttrsBySpuId(spuId);
//        skuItemVo.setSaleAttr(saleAttrVos);
//
//        //4、获取spu的介绍 pms_spu_info_desc
//        SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(spuId);
//        skuItemVo.setDesc(spuInfoDescEntity);
//
//        //5、获取spu的规格参数信息
//        List<SpuItemAttrGroupVo> attrGroupVos = attrGroupService.getAttrGroupWithAttrsBySpuId(spuId,catelogId);
//        skuItemVo.setGroupAttrs(attrGroupVos);
//        return skuItemVo;
//
//    }

        // 无需获取返回值
        CompletableFuture<Void> imageFuture = CompletableFuture.runAsync(() -> {
            //2 sku图片信息
            List<SkuImagesEntity> images = imagesService.getImagesBySkuId(skuId);
            skuItemVo.setImages(images);
        }, executor);
        // 在1之后
        CompletableFuture<Void> saleAttrFuture =infoFuture.thenAcceptAsync(res -> {
            //3 获取spu销售属性组合 list
            List<SkuItemSaleAttrVo> saleAttrVos = skuSaleAttrValueService.getSaleAttrsBySpuId(res.getSpuId());
            skuItemVo.setSaleAttr(saleAttrVos);
        },executor);
        // 在1之后
        CompletableFuture<Void> descFuture = infoFuture.thenAcceptAsync(res -> {
            //4 获取spu介绍
            SpuInfoDescEntity spuInfo = spuInfoDescService.getById(res.getSpuId());
            skuItemVo.setDesc(spuInfo);
        },executor);
        // 在1之后
        CompletableFuture<Void> baseAttrFuture = infoFuture.thenAcceptAsync(res -> {
            //5 获取spu规格参数信息
            List<SpuItemAttrGroupVo> attrGroups = attrGroupService.getAttrGroupWithAttrsBySpuId(res.getSpuId(), res.getCatelogId());
            skuItemVo.setGroupAttrs(attrGroups);
        }, executor);

//        // 6.查询当前sku是否参与秒杀优惠
//        CompletableFuture<Void> secKillFuture = CompletableFuture.runAsync(() -> {
//            R skuSeckillInfo = seckillFeignService.getSkuSeckillInfo(skuId);
//            if (skuSeckillInfo.getCode() == 0) {
//                // 注意null的问题
//                SeckillSkuRedisTo data = skuSeckillInfo.getData(new TypeReference<SeckillSkuRedisTo>() {});
//                SeckillInfoVo seckillInfoVo = new SeckillInfoVo();
//                BeanUtils.copyProperties(data,seckillInfoVo);
//                skuItemVo.setSeckillInfoVo(seckillInfoVo);
//            }
//        }, executor);
        // 等待所有任务都完成再返回
        CompletableFuture.allOf(imageFuture,saleAttrFuture,descFuture,baseAttrFuture).get();
//        CompletableFuture.allOf(imageFuture,saleAttrFuture,descFuture,baseAttrFuture).get();
        return skuItemVo;
    }
}