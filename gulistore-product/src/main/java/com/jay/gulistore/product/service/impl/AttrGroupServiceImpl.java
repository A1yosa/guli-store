package com.jay.gulistore.product.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
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

}