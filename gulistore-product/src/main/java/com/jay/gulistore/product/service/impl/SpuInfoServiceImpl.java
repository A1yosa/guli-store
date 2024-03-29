package com.jay.gulistore.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.jay.common.constant.ProductConstant;
import com.jay.common.to.SkuHasStockVo;
import com.jay.common.to.SkuReductionTo;
import com.jay.common.to.SpuBoundTo;
import com.jay.common.to.es.SkuEsModel;
import com.jay.common.utils.R;
import com.jay.gulistore.product.entity.*;
import com.jay.gulistore.product.fegin.CouponFeignService;
import com.jay.gulistore.product.fegin.SearchFeignService;
import com.jay.gulistore.product.fegin.WareFeignService;
import com.jay.gulistore.product.service.*;
import com.jay.gulistore.product.vo.*;
//import com.mysql.cj.util.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jay.common.utils.PageUtils;
import com.jay.common.utils.Query;

import com.jay.gulistore.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    SpuInfoDescService spuInfoDescService;

    @Autowired
    SpuImagesService imagesService;

    @Autowired
    AttrService attrService;

    @Autowired
    ProductAttrValueService attrValueService;

    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    CouponFeignService couponFeignService;

    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    WareFeignService wareFeignService;

    @Autowired
    SearchFeignService searchFeignService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo vo) {
        //1、保存spu基本信息`pms_spu_info`
        SpuInfoEntity infoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(vo, infoEntity);
        infoEntity.setCreateTime(new Date());
        infoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(infoEntity);

        //2、保存spu的描述图片`pms_spu_info_desc`
        List<String> decript = vo.getDecript();
        SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
        descEntity.setSpuId(infoEntity.getId());
        descEntity.setDecript(String.join(",", decript));
        spuInfoDescService.saveSpuInfoDesc(descEntity);

        //3、保存spu的图片集`pms_spu_images`
        List<String> images = vo.getImages();
        imagesService.saveImages(infoEntity.getId(),images);

        //4、保存spu的规格参数`pms_product_attr_value`
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map((attr) -> {
            ProductAttrValueEntity valueEntity = new ProductAttrValueEntity();
            valueEntity.setAttrId(attr.getAttrId());
            AttrEntity id = attrService.getById(attr.getAttrId());
            valueEntity.setAttrName(id.getAttrName());
            valueEntity.setAttrValue(attr.getAttrValues());
            valueEntity.setQuickShow(attr.getShowDesc());
            valueEntity.setSpuId(infoEntity.getId());
            return valueEntity;
        }).collect(Collectors.toList());
        attrValueService.saveProductAttr(collect);


        //5、保存spu的积分信息`gulimall_sms`->`sms_spu_bounds`
        Bounds bounds = vo.getBounds();
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds, spuBoundTo);
        spuBoundTo.setSpuId(infoEntity.getId());

//        TODO 商品发布
        couponFeignService.saveSpuBounds(spuBoundTo);
//        R r = couponFeignService.saveSpuBounds(spuBoundTo);
//        if (r.getCode() != 0){
//            log.error("远程保存spu积分信息失败");
//        }

        //6、保存spu对应的所有sku信息
        List<Skus> skus = vo.getSkus();
        if (skus != null && skus.size() > 0) {
            skus.forEach(item -> {
                String defaultImg = "";
                //查找出默认图片
                for (Images image : item.getImages()) {
                    if (image.getDefaultImg() == 1) {
                        defaultImg = image.getImgUrl();
                    }
                }
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(item, skuInfoEntity);
                skuInfoEntity.setSpuId(infoEntity.getId());
                skuInfoEntity.setBrandId(infoEntity.getBrandId());
                skuInfoEntity.setCatelogId(infoEntity.getCatelogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                //6.1、sku的基本信息`pms_sku_info`
                skuInfoService.saveSkuInfo(skuInfoEntity);


                Long skuId = skuInfoEntity.getSkuId();

                List<SkuImagesEntity> imagesEntities = item.getImages().stream().map(img->{
                    SkuImagesEntity skuImagesEntity =new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());

                    return skuImagesEntity;
                }).filter(entity -> {
                    //返回true是需要，返回false是过滤掉
                    return !StringUtils.isEmpty(entity.getImgUrl());
                }).collect(Collectors.toList());


                //6.2、sku的图片信息`pms_sku_images`
                skuImagesService.saveBatch(imagesEntities);


                //6.3、sku的销售属性信息`pms_sku_sale_attr_value`
                List<Attr> attr = item.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attr.stream().map(a -> {
                    SkuSaleAttrValueEntity attrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(a, attrValueEntity);
                    attrValueEntity.setSkuId(skuId);


                    return attrValueEntity;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);


                //6.4、sku的优惠、满减等信息`gulimall_sms`->`sms_sku_ladder`/`sms_sku_full_reduction`/`sms_member_price`
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(item, skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) == 1){
//                    TODO 商品发布
                    couponFeignService.saveSkuReduction(skuReductionTo);
//                    R r1 = couponFeignService.saveSkuReduction(skuReductionTo);
//                    if (r1.getCode() != 0){
//                        log.error("远程保存优惠信息失败");
//                    }
                }

            });
        }


    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity infoEntity) {
        this.baseMapper.insert(infoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {

        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();

        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)){
            wrapper.eq("catelog_id", catelogId);
        }

        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)){
            wrapper.eq("brand_id", brandId);
        }

        String status = (String) params.get("status");
        if (!StringUtils.isEmpty(status)){
            wrapper.eq("publish_status", status);
        }

        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)){
            wrapper.and((w) -> {
                w.eq("id", key).or().like("spu_name", key);
            });
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);

    }

    @Override
    public void up(Long spuId) {


        //1、查出当前spuid对应的所有sku信息
        List<SkuInfoEntity> skus = skuInfoService.getSkusBySpuId(spuId);
        List<Long> skuIdList = skus.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());


        //todo 4、查询当前sku所有可以被检索的规格属性
//        productAttrValueService.baseAttrListForSpu();
        List<ProductAttrValueEntity> baseAttrs = attrValueService.baseAttrlistForSpu(spuId);
        List<Long> attrIds = baseAttrs.stream().map(attr->{
            return attr.getAttrId();
        }).collect(Collectors.toList());

        //这是可被检索属性的id集合
        List<Long> searchAttrIds = attrService.selectSearchAttrIds(attrIds);
        //为了筛选出可被检索的商品attrs
        Set<Long> idSet = new HashSet<>(searchAttrIds);
        //拿到所有的可以被检索的商品属性关系表中数据，并提取出商品需要的attrs
        List<SkuEsModel.Attrs> attrsList = baseAttrs.stream()
                .filter(item -> {
                    return idSet.contains(item.getAttrId());
                }).map(item -> {
                    SkuEsModel.Attrs attrs1 = new SkuEsModel.Attrs();
                    BeanUtils.copyProperties(item, attrs1);
                    return attrs1;
                }).collect(Collectors.toList());

        //调用远程仓储服务，提前查出来，避免循环查库
        Map<Long, Boolean> stockMap = null;
        // todo 1、发送远程调用，库存系统查询是否有库存
        try {
            //会有8次库存查询，所以希望远程服务有一个接口可以统一帮我们查到所有sku有没有库存
            R r = wareFeignService.getSkuHasStock(skuIdList);
            TypeReference<List<SkuHasStockVo>> typeReference = new TypeReference<List<SkuHasStockVo>>() {
            };
            stockMap = r.getData(typeReference).stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHasStock));
        } catch (Exception e) {
            log.error("调用库存服务查询异常，原因为{}" , e);
        }


        //封装每个sku信息
        Map<Long, Boolean> finalStockMap = stockMap;
        List<SkuEsModel> upProducts = skus.stream().map(sku -> {
            SkuEsModel esModel = new SkuEsModel();
            BeanUtils.copyProperties(sku, esModel);

            // skuPrice skuImg
            esModel.setSkuPrice(sku.getPrice());
            esModel.setSkuImg(sku.getSkuDefaultImg());

            // hasStock hotScore
            if (finalStockMap == null) {
                esModel.setHasStock(true);
            } else {
                esModel.setHasStock(finalStockMap.get(sku.getSkuId()));
            }
            // todo 2、热度评分。0
            esModel.setHotScore(0L);

            // brandName brandImg
            //todo 3、查询品牌和分类的名字信息
            BrandEntity brand = brandService.getById(esModel.getBrandId());
            esModel.setBrandName(brand.getName());
            esModel.setBrandImg(brand.getLogo());
            //  catelogName
            CategoryEntity category = categoryService.getById(esModel.getCatelogId());
            esModel.setCatelogName(category.getName());

            //   private Long attrId;
            //   private String attrName;
            //   private String attrValue;
            //设置检索属性
            esModel.setAttrs(attrsList);
            return esModel;
        }).collect(Collectors.toList());
        // 远程调用上架商品
        //todo 5、将数据发送给es保存
        R r = searchFeignService.productStatusUp(upProducts);
        if (r.getCode()==0){
            //远程调用成功
            //todo 更改spuinfo中商品的发布状态为已上架
            //状态应该作为枚举类存在的
            baseMapper.updateSpuStatus(spuId, ProductConstant.StatusEnum.SPU_UP.getCode());
        }else {
            //远程调用失败
            //todo 7、重复调用？接口幂等性；重试机制？xxx
        }


    }

    @Override
    public SpuInfoEntity getSpuInfoBySkuId(Long skuId) {
        SkuInfoEntity byId = skuInfoService.getById(skuId);
        Long spuId = byId.getSpuId();
        SpuInfoEntity spuInfoEntity = getById(spuId);
        return spuInfoEntity;
    }


}