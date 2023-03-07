package com.jay.gulistore.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jay.gulistore.product.service.CategoryBrandRelationService;
import com.jay.gulistore.product.vo.Catelog2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jay.common.utils.PageUtils;
import com.jay.common.utils.Query;

import com.jay.gulistore.product.dao.CategoryDao;
import com.jay.gulistore.product.entity.CategoryEntity;
import com.jay.gulistore.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

//    @Autowired
//    CategoryDao categoryDao;

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    private StringRedisTemplate redisTemplate;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {

//        1、查出所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);


//        2、组装成父型结构
            //找到所有的一级分类
        List<CategoryEntity> level1Menus = entities.stream().filter((categoryEntity) -> {
            return categoryEntity.getParentCid() == 0;
        }).map((menu)->{
            menu.setChildren(getChildren(menu,entities));
            return menu;
        }).sorted((menu1,menu2)->{
            return (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort());
        }).collect(Collectors.toList());

        return level1Menus;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
//TODO          1、检查当前删除的菜单，是否被别处引用
//        现阶段先使用逻辑删除
        baseMapper.deleteBatchIds(asList);
    }

    //categoryServiceImpl实现方法
    //查找完整路径方法
    @Override
    public Long[] findCatelogPath(Long catelogId) {

        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);
        Collections.reverse(parentPath);
        return parentPath.toArray(new Long[parentPath.size()]);

    }

    /*
    * 级联更新
    * 缓存失效
    * */
//    @Cacheable(value = {"category"}, key = "#root.methodName")
    @CacheEvict(value = {"category"},allEntries = true)
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }

//    指定缓存分区。每一个需要缓存的数据，我们都需要来指定要放到哪个名字的缓存中。通常按照业务类型进行划分
    @Cacheable(value = {"category"},key = "#root.methodName")      //可缓存,表示当前方法需要将进行缓存，如果缓存中有，方法无效调用，如果缓存中没有，则会调用方法，最后将方法的结果放入到缓存中。
    @Override
    public List<CategoryEntity> getLevel1Category() {

        List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return categoryEntities;

    }

    /**
     * 逻辑是
     * （1）根据一级分类，找到对应的二级分类
     * （2）将得到的二级分类，封装到Catelog2Vo中
     * （3）根据二级分类，得到对应的三级分类
     * （3）将三级分类封装到Catelog3List
     * @return
     */

    //    todo 删除缓存操作
    @Cacheable(value = {"category"},key = "#root.methodName")
    @Override
    public Map<String, List<Catelog2Vo>> getCatelogJson() {
        System.out.println("查询数据库");
        /*//一次性查询出所有的分类数据，减少对于数据库的访问次数，后面的数据操作并不是到数据库中查询，而是直接从这个集合中获取，
        // 由于分类信息的数据量并不大，所以这种方式是可行的
        List<CategoryEntity> categoryEntities = this.baseMapper.selectList(null);

        //1.查出所有一级分类
        List<CategoryEntity> level1Categories = getParent_cid(categoryEntities,0L);

        Map<String, List<Catelog2Vo>> parent_cid = level1Categories.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), level1 -> {
            //2. 根据一级分类的id查找到对应的二级分类
            List<CategoryEntity> level2Categories = getParent_cid(categoryEntities,level1.getCatId());

            //3. 根据二级分类，查找到对应的三级分类
            List<Catelog2Vo> catelog2Vos =null;

            if(null != level2Categories || level2Categories.size() > 0){
                catelog2Vos = level2Categories.stream().map(level2 -> {
                    //得到对应的三级分类
                    List<CategoryEntity> level3Categories = getParent_cid(categoryEntities,level2.getCatId());
                    //封装到catelog3List
                    List<catelog3List> catelog3Lists = null;
                    if (null != level3Categories) {
                        catelog3Lists = level3Categories.stream().map(level3 -> {
                            catelog3List catelog3List = new catelog3List(level2.getCatId().toString(), level3.getCatId().toString(), level3.getName());
                            return catelog3List;
                        }).collect(Collectors.toList());
                    }
                    return new Catelog2Vo(level1.getCatId().toString(), catelog3Lists, level2.getCatId().toString(), level2.getName());
                }).collect(Collectors.toList());
            }

            return catelog2Vos;
        }));
        return parent_cid;*/
        System.out.println("查询了数据库");
        List<CategoryEntity> selectList = baseMapper.selectList(null);

        List<CategoryEntity> category = getParent_cid(selectList, 0L);

        //2、封装数据
        Map<String, List<Catelog2Vo>> parent_cid = category.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //1、查到这个一级分类下的所有二级分类
            List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());

            //2、封装上面的结果
            List<Catelog2Vo> catelog2Vos = new ArrayList<>();
            if (categoryEntities != null && categoryEntities.size() != 0) {
                catelog2Vos = categoryEntities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(
                            l2.getParentCid().toString(), null, l2.getCatId().toString(), l2.getName());
                    //1、找当前二级分类的三级分类封装成vo
                    List<CategoryEntity> level3catelog = getParent_cid(selectList, l2.getCatId());
                    List<Catelog2Vo.Catelog3Vo> collectlevel3 = new ArrayList<>();
                    if (level3catelog != null && !level3catelog.isEmpty()) {
                        //2、封装成指定格式
                        collectlevel3 = level3catelog.stream().map(l3 -> {
                            Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(l3.getParentCid().toString(),
                                    l3.getCatId().toString(), l3.getName());
                            return catelog3Vo;
                        }).collect(Collectors.toList());
                    }
                    catelog2Vo.setCatelog3List(collectlevel3);
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));

        return parent_cid;
    }
//    todo 删除缓存操作
//    @Override
    public Map<String, List<Catelog2Vo>> getCatelogJson2() {

        //给缓存中放json字符串，拿出json字符串，还要逆转为能用的对象类型【序列化与反序列化】
        //1、加入缓存逻辑
        //JSON好处是跨语言，跨平台兼容。
        String catelogJSON = redisTemplate.opsForValue().get("catelogJSON");
        if (StringUtils.isEmpty(catelogJSON)){
            //2、缓存中没有，查询数据库
            Map<String, List<Catelog2Vo>> catelogJsonFromDB = getCatelogJsonFromDb();
            //3、将查到的数据再放入缓存，将对象转为JSON在缓存中
            String jsonString = JSON.toJSONString(catelogJsonFromDB);
            redisTemplate.opsForValue().set("catelogJSON",jsonString);
            return catelogJsonFromDB;
        }

        //转为我们指定的对象。
        Map<String,List<Catelog2Vo>> result = JSON.parseObject(catelogJSON,new TypeReference<Map<String,List<Catelog2Vo>>>(){});
        return result;

    }


//    从数据库查询并封装分类数据
    public Map<String, List<Catelog2Vo>> getCatelogJsonFromDb() {

        List<CategoryEntity> selectList = baseMapper.selectList(null);

        //查出所有1级分类
        List<CategoryEntity> level1Category = getParent_cid(selectList,0L);

        //2、封装数据
        Map<String, List<Catelog2Vo>> parent_cid = level1Category.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //1、每一个的一级分类，查到这个一级分类的二级分类
            List<CategoryEntity> categoryEntities = getParent_cid(selectList,v.getCatId());
            //2、封装上面的结果
            List<Catelog2Vo> catelog2Vos = null;
            if (categoryEntities != null) {
                catelog2Vos = categoryEntities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    //1、找当前二级分类的三级分类封装vo
                    List<CategoryEntity> level3Catelog = getParent_cid(selectList,l2.getCatId());
                    if (level3Catelog != null){
                        List<Catelog2Vo.Catelog3Vo> collect = level3Catelog.stream().map(l3 -> {
                            //2、封装成指定格式
                            Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return catelog3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatelog3List(collect);
                    }
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));

        return parent_cid;
    }

    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList,Long parent_cid) {
//        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
        List<CategoryEntity> collect = selectList.stream().filter(item -> item.getParentCid() == parent_cid).collect(Collectors.toList());
        return collect;
    }

    //递归查找父节点id
    public List<Long> findParentPath(Long catelogId,List<Long> paths){
        //1、收集当前节点id
        paths.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        if (byId.getParentCid() != 0){
            findParentPath(byId.getParentCid(), paths);
        }

        return paths;
    }


    //    递归查找所有菜单的子菜单
    private List<CategoryEntity> getChildren(CategoryEntity root,List<CategoryEntity> all){
        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == root.getCatId();
        }).map(categoryEntity -> {
//            找到子菜单
            categoryEntity.setChildren(getChildren(categoryEntity,all));
            return categoryEntity;
        }).sorted((menu1,menu2)->{
//            菜单排序
            return (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort());
        }).collect(Collectors.toList());
        return children;
    }


}