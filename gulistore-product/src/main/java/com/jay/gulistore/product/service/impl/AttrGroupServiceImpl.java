package com.jay.gulistore.product.service.impl;

import com.jay.gulistore.product.entity.AttrEntity;
import com.jay.gulistore.product.service.AttrService;
import com.jay.gulistore.product.vo.AttrGroupWithAttrsVo;
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

import com.jay.gulistore.product.dao.AttrGroupDao;
import com.jay.gulistore.product.entity.AttrGroupEntity;
import com.jay.gulistore.product.service.AttrGroupService;
import org.springframework.util.StringUtils;

@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, long catelogId) {

        String key = (String) params.get("key");
        QueryWrapper<AttrGroupEntity> wapper = new QueryWrapper<AttrGroupEntity>();
        if (!StringUtils.isEmpty(key)){
            wapper.and((obj)->{
                obj.eq("attr_group_id",key).or().like("attr_group_name",key);
            });
        }

        if (catelogId == 0){ //如果传过来的id是0，则查询所有属性
            //this.page两个参数，第一个参数是查询页码信息，其中Query.getPage方法传入一个map，会自动封装成IPage
            //第二个参数是查询条件，空的wapper就是查询全部
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),
                    wapper);
            return new PageUtils(page);
        }else{
            wapper.eq("catelog_id", catelogId);
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),
                    wapper);
            return new PageUtils(page);
        }
    }

    /*
     *根据分类id查出所有的分组以及这些分组里面的属性
     *@param:[catelogId]
     *@return:java.util.List<com.xmh.gulimall.product.vo.AttrGroupWithAttrsVo>
     *@date: 2022/2/16 21:13
     */
    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId) {

//        1、查询分组信息
        List<AttrGroupEntity> attrGroupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));

//        2、查询所有属性
        List<AttrGroupWithAttrsVo> collect = attrGroupEntities.stream().map(group -> {
            AttrGroupWithAttrsVo attrsVo = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(group,attrsVo);
            List<AttrEntity> attrs = attrService.getRelationAttr(attrsVo.getAttrGroupId());
            attrsVo.setAttrs(attrs);
            return attrsVo;
        }).collect(Collectors.toList());
        return collect;
    }

}