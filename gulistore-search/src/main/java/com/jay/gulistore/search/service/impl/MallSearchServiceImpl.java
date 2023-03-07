package com.jay.gulistore.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.jay.common.to.es.SkuEsModel;
import com.jay.gulistore.search.config.GulistoreElasticSearchConfig;
import com.jay.gulistore.search.constant.EsConstant;
import com.jay.gulistore.search.service.MallSearchService;
import com.jay.gulistore.search.vo.SearchParam;
import com.jay.gulistore.search.vo.SearchResult;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MallSearchServiceImpl implements MallSearchService {

    @Autowired
    private RestHighLevelClient client;

//    @Override
//    public SearchResult search(SearchParam param) {
//        //动态构建出查询需要DSL 语句
//        SearchResult result = null;
//
//        //1、准备检索请求
//        SearchRequest searchRequest = buildSearchRequest(param);
//        try {
//            //2、执行检索请求
//            SearchResponse response = client.search(searchRequest, GulistoreElasticSearchConfig.COMMON_OPTIONS);
//            //3、分析响应数据 封装成我们需要的格式
//            result =  buildSearchResult(response,param);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return result;
//    }
//
//
//
//    /**
//     * 准备检索请求
//     * #模糊匹配，过滤（按照属性，分类，品牌，价格区间，库存），排序，分页，高亮，聚合分析
//     * @return
//     */
//    private SearchRequest buildSearchRequest(SearchParam param) {
//        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();//构建DSL语句的
//        /**
//         * 查询：过滤（按照属性，分类，品牌，价格区间，库存）
//         */
//        //1、构建bool - query
//        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
//        //1.1、must-模糊匹配，
//        if (!StringUtils.isEmpty(param.getKeyword())) {
//            boolQuery.must(QueryBuilders.matchQuery("skuTitle", param.getKeyword()));
//        }
//        //1.2、bool - filter - 按照三级分类id查询
//        if (param.getCatelog3Id() != null) {
//            boolQuery.filter(QueryBuilders.termQuery("catelogId", param.getCatelog3Id()));
//        }
//        //1.2、bool - filter - 按照品牌id查询
//        if (param.getBrandId() != null && param.getBrandId().size() > 0) {
//            boolQuery.filter(QueryBuilders.termsQuery("brandId", param.getBrandId()));
//        }
//        //1.2、bool - filter - 按照所有指定的属性进行查询
//        if (param.getAttrs() != null && param.getAttrs().size() > 0) {
//            for (String attrStr : param.getAttrs()) {
//                //attrs=1_5寸:8寸&attrs=2_16G:8G
//                BoolQueryBuilder nestedboolQuery = QueryBuilders.boolQuery();
//                //attr = 1_5寸:8寸
//                String[] s = attrStr.split("_");
//                String attrId = s[0]; //检索的属性id
//                String[] attrValues = s[1].split(":"); //这个属性的检索用的值
//                nestedboolQuery.must(QueryBuilders.termQuery("attrs.attrId", attrId));
//                nestedboolQuery.must(QueryBuilders.termsQuery("attrs.attrValue", attrValues));
//                //每一个必须都得生成一个nested查询nestedQuery("attrs", nestedboolQuery, ScoreMode.None) attrs表示的是哪一个字段ScoreMode.None表示不参与评分
//                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", nestedboolQuery, ScoreMode.None);
//                boolQuery.filter(nestedQuery);
//            }
//
//        }
//        //1.2、bool - filter - 按照库存是否有进行查询
//        if(param.getHasStock() != null){
//            boolQuery.filter(QueryBuilders.termQuery("hasStock", param.getHasStock() == 1));
//        }
////        boolQuery.filter(QueryBuilders.termQuery("hasStock", param.getHasStock() == 1));
//        //1.2、bool - filter - 按照价格区间
//        if (!StringUtils.isEmpty(param.getSkuPrice())) {
//            //1_500/_500/500_
//            /**
//             * "range": {
//             *             "skuPrice": {
//             *               "gte": 0,
//             *               "lte": 6000
//             *             }
//             *           }
//             */
//            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
//            String[] s = param.getSkuPrice().split("_");
//            if (s.length == 2) {
//                //区间
//                rangeQuery.gte(s[0]).lte(s[1]);
//            } else if (s.length == 1) {
//                if (param.getSkuPrice().startsWith("_")) {
//                    rangeQuery.lte(s[0]);
//                }
//
//                if (param.getSkuPrice().endsWith("_")) {
//                    rangeQuery.gte(s[0]);
//                }
//            }
//
//            boolQuery.filter(rangeQuery);
//        }
//        //把以前的所有条件都拿来进行封装
//        sourceBuilder.query(boolQuery);
//        /**
//         * 排序，分页，高亮，
//         */
//        //2.1、排序
//        if (!StringUtils.isEmpty(param.getSort())) {
//            String sort = param.getSort();
//            //sort=hotScore_asc/desc
//            String[] s = sort.split("_");
//            SortOrder order = s[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;
//            sourceBuilder.sort(s[0], order);
//        }
//        //2.2、分页  pageSize:5
//        //  pageNum:1  from:0  size:5  [0,1,2,3,4]
//        // pageNum:2  from:5   size:5
//        //from = (pageNum-1)*size
//        sourceBuilder.from((param.getPageNum() - 1) * EsConstant.PRODUCT_PAGESIZE);
//        sourceBuilder.size(EsConstant.PRODUCT_PAGESIZE);
//
//        //2.3、高亮
//        if (!StringUtils.isEmpty(param.getKeyword())) {
//            HighlightBuilder builder = new HighlightBuilder();
//            builder.field("skuTitle");
//            builder.preTags("<b style='color:red'>");
//            builder.postTags("</b>");
//            sourceBuilder.highlighter(builder);
//        }
//        /**
//         * 聚合分析
//         */
//        //1、品牌聚合
//        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg");
//        brand_agg.field("brandId").size(50);
//        //品牌聚合的子聚合
//        brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
//        brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));
//        //TODO 1、聚合brand
//        sourceBuilder.aggregation(brand_agg);
//        //2、分类聚合 catelog_agg
//        TermsAggregationBuilder catelog_agg = AggregationBuilders.terms("catelog_agg").field("catelogId").size(20);
//        catelog_agg.subAggregation(AggregationBuilders.terms("catelog_name_agg").field("catelogName").size(1));
//        //TODO 2、聚合catelog
//        sourceBuilder.aggregation(catelog_agg);
//        //3、属性聚合 attr_agg
//        NestedAggregationBuilder attr_agg = AggregationBuilders.nested("attr_agg", "attrs");
//
//        //聚合出当前所有的attrId
//        TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
//        //聚合分析出当前attr_id对应的名字
//        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
//        //聚合分析出当前attr_id对应的所有可能的属性值attrValue
//        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));
//        attr_agg.subAggregation(attr_id_agg);
//        //TODO 3、聚合attr
//        sourceBuilder.aggregation(attr_agg);
//
//        String s = sourceBuilder.toString();
//        System.out.println("构建的DSL" + s);
//
//        SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, sourceBuilder);
//        return searchRequest;
//    }
//
//
//    private SearchResult buildSearchResult(SearchResponse response,SearchParam param) {
//        SearchResult result = new SearchResult();
//        //1、返回的所有查询到的商品
//        SearchHits hits = response.getHits();
//        List<SkuEsModel> esModels = new ArrayList<>();
//        if (null != hits.getHits() && hits.getHits().length>0){
//            for (SearchHit hit : hits.getHits()) {
//                String sourceAsString = hit.getSourceAsString();
//                SkuEsModel esModel = JSON.parseObject(sourceAsString, SkuEsModel.class);
//                if (!StringUtils.isEmpty(param.getKeyword())) {
//                    HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
//                    String string = skuTitle.getFragments()[0].string();
//                    esModel.setSkuTitle(string);
//                }
//                esModels.add(esModel);
//            }
//        }
//        result.setProducts(esModels);
//
//        //2、当前所有商品涉及到的所有属性
//        List<SearchResult.AttrVo> attrVos = new ArrayList<>();
//        ParsedNested attr_agg = response.getAggregations().get("attr_agg");
//        ParsedLongTerms attr_id_agg = attr_agg.getAggregations().get("attr_id_agg");
//        for (Terms.Bucket bucket : attr_id_agg.getBuckets()) {
//            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
//            //1、得到属性的id;
//            long attrId = bucket.getKeyAsNumber().longValue();
//            //2、得到属性的名字
//            String attrName = ((ParsedStringTerms) bucket.getAggregations().get("attr_name_agg")).getBuckets().get(0).getKeyAsString();
//            //3、得到属性的所有值
//            List<String> attrValues = ((ParsedStringTerms) bucket.getAggregations().get("attr_value_agg")).getBuckets().stream().map(item -> {
//                String keyAsString = ((Terms.Bucket) item).getKeyAsString();
//                return keyAsString;
//            }).collect(Collectors.toList());
//
//            attrVo.setAttrId(attrId);
//            attrVo.setAttrName(attrName);
//            attrVo.setAttrValue(attrValues);
//            attrVos.add(attrVo);
//        }
//        result.setAttrs(attrVos);
//
//
////        //3、当前所有商品涉及到的所有品牌信息
//        List<SearchResult.BrandVo> brandVos = new ArrayList<>();
//        ParsedLongTerms brand_agg = response.getAggregations().get("brand_agg");
//        for (Terms.Bucket bucket : brand_agg.getBuckets()) {
//            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
//            //1、得到品牌的id
//            long brandId = bucket.getKeyAsNumber().longValue();
//            //2、得到品牌的名
//            String brandName = ((ParsedStringTerms) bucket.getAggregations().get("brand_name_agg")).getBuckets().get(0).getKeyAsString();
//            //3、得到品牌的图片
//            String brandImg = ((ParsedStringTerms) bucket.getAggregations().get("brand_img_agg")).getBuckets().get(0).getKeyAsString();
//            brandVo.setBrandId(brandId);
//            brandVo.setBrandName(brandName);
//            brandVo.setBrandImg(brandImg);
//            brandVos.add(brandVo);
//        }
//
//        result.setBrands(brandVos);
//
//
////        //4、当前所有商品涉及到的所有分类信息
//        ParsedLongTerms catelog_agg = response.getAggregations().get("catelog_agg");
//        List<SearchResult.CatelogVo> catelogVos = new ArrayList<>();
//        List<? extends Terms.Bucket> buckets = catelog_agg.getBuckets();
//        for (Terms.Bucket bucket : buckets) {
//            SearchResult.CatelogVo catelogVo = new SearchResult.CatelogVo();
//            //得到分类id
//            String keyAsString = bucket.getKeyAsString();
//            catelogVo.setCatelogId(Long.parseLong(keyAsString));
//
//            //得到分类名
//            ParsedStringTerms catelog_name_agg = bucket.getAggregations().get("catelog_name_agg");
//            String catelog_name = catelog_name_agg.getBuckets().get(0).getKeyAsString();
//            catelogVo.setCatelogName(catelog_name);
//            catelogVos.add(catelogVo);
//        }
//        result.setCatelogs(catelogVos);
//
//
////        ========以上从聚合信息中获取======
////        //5、分页信息-页码
//        result.setPageNum(param.getPageNum());
////        //5、分页信息-总记录树
//        long total = hits.getTotalHits().value;
//        result.setTotal(total);
////        //5、分页信息-总页码-计算  11/2 = 5 .. 1
//        int totalPages = (int) total % EsConstant.PRODUCT_PAGESIZE == 0 ? (int) total / EsConstant.PRODUCT_PAGESIZE : ((int) total / EsConstant.PRODUCT_PAGESIZE + 1);
//        result.setTotalPages(totalPages);
//
//        //TODO 分类：不需要导航取消
//        return result;
//    }



    //去es进行检索
    @Override
    public SearchResult search(SearchParam param) {
        //动态构建出查询需要的DSL语句
        SearchResult result = null;
        //1、准备检索请求
        SearchRequest searchRequest = buildSearchRequest(param);
        try {
            //2、执行检索请求
            SearchResponse response = client.search(searchRequest, GulistoreElasticSearchConfig.COMMON_OPTIONS);
            //分析响应数据封装我们需要的格式
            result = buildSearchResult(response,param);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 准备检索请求
     * 模糊匹配、过滤（按照属性、分类、品牌、价格区间、库存），排序，分页，高亮，聚合分析
     * @return
     */
    private SearchRequest buildSearchRequest(SearchParam param) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();//构建DSL语句的
        /**
         * 过滤（按照属性、分类、品牌、价格区间、库存）
         */
        //1、构建bool-query
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        sourceBuilder.query(boolQuery);
        //1.1 must-模糊匹配、
        if (!StringUtils.isEmpty(param.getKeyword())){
            boolQuery.must(QueryBuilders.matchQuery("skuTitle",param.getKeyword()));
        }
        //1.2.1 filter-按照三级分类id查询
        if (null != param.getCatelog3Id()){
            boolQuery.filter(QueryBuilders.termQuery("catelogId",param.getCatelog3Id()));
        }
        //1.2.2 filter-按照品牌id查询
        if (null != param.getBrandId() && param.getBrandId().size()>0) {
            boolQuery.filter(QueryBuilders.termsQuery("brandId",param.getBrandId()));
        }
        //1.2.3 filter-按照是否有库存进行查询
        if (null != param.getHasStock() ) {
            boolQuery.filter(QueryBuilders.termQuery("hasStock", param.getHasStock() == 1));
        }
        //1.2.4 filter-按照区间进行查询  1_500/_500/500_
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("skuPrice");
        if (!StringUtils.isEmpty(param.getSkuPrice())) {
            String[] prices = param.getSkuPrice().split("_");
            if (prices.length == 1) {
                if (param.getSkuPrice().startsWith("_")) {
                    rangeQueryBuilder.lte(Integer.parseInt(prices[0]));
                }else {
                    rangeQueryBuilder.gte(Integer.parseInt(prices[0]));
                }
            } else if (prices.length == 2) {
                //_6000会截取成["","6000"]
                if (!prices[0].isEmpty()) {
                    rangeQueryBuilder.gte(Integer.parseInt(prices[0]));
                }
                rangeQueryBuilder.lte(Integer.parseInt(prices[1]));
            }
            boolQuery.filter(rangeQueryBuilder);
        }
        //1.2.5 filter-按照属性进行查询
        List<String> attrs = param.getAttrs();
        if (null != attrs && attrs.size() > 0) {
            //attrs=1_5寸:8寸&2_16G:8G
            attrs.forEach(attr->{
                BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
                String[] attrSplit = attr.split("_");
                queryBuilder.must(QueryBuilders.termQuery("attrs.attrId", attrSplit[0]));//检索的属性的id
                String[] attrValues = attrSplit[1].split(":");
                queryBuilder.must(QueryBuilders.termsQuery("attrs.attrValue", attrValues));//检索的属性的值
                //每一个必须都得生成一个nested查询
                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("attrs", queryBuilder, ScoreMode.None);
                boolQuery.filter(nestedQueryBuilder);
            });
        }
        //把以前所有的条件都拿来进行封装
        sourceBuilder.query(boolQuery);
        /**
         * 排序，分页，高亮，
         */
        //2.1 排序  eg:sort=saleCount_desc/asc
        if (!StringUtils.isEmpty(param.getSort())) {
            String[] sortSplit = param.getSort().split("_");
            sourceBuilder.sort(sortSplit[0], sortSplit[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC);
        }

        //2.2、分页
        sourceBuilder.from((param.getPageNum() - 1) * EsConstant.PRODUCT_PAGESIZE);
        sourceBuilder.size(EsConstant.PRODUCT_PAGESIZE);

        //2.3 高亮highlight
        if (!StringUtils.isEmpty(param.getKeyword())) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            sourceBuilder.highlighter(highlightBuilder);
        }

        /**
         * 聚合分析
         */
        //5. 聚合
        //5.1 按照品牌聚合
        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg").field("brandId").size(50);
        //品牌聚合的子聚合
        TermsAggregationBuilder brand_name_agg = AggregationBuilders.terms("brand_name_agg").field("brandName").size(1);
        TermsAggregationBuilder brand_img_agg = AggregationBuilders.terms("brand_img_agg").field("brandImg");
        brand_agg.subAggregation(brand_name_agg);
        brand_agg.subAggregation(brand_img_agg);
        sourceBuilder.aggregation(brand_agg);

        //5.2 按照catelog聚合
        TermsAggregationBuilder catelog_agg = AggregationBuilders.terms("catelog_agg").field("catelogId").size(20);
        TermsAggregationBuilder catelog_name_agg = AggregationBuilders.terms("catelog_name_agg").field("catelogName").size(1);
        catelog_agg.subAggregation(catelog_name_agg);
        sourceBuilder.aggregation(catelog_agg);

        //5.3 按照attrs聚合
        NestedAggregationBuilder nestedAggregationBuilder = new NestedAggregationBuilder("attr_agg", "attrs");
        //按照attrId聚合
        TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
        //按照attrId聚合之后再按照attrName和attrValue聚合
        TermsAggregationBuilder attr_name_agg = AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1);
        TermsAggregationBuilder attr_value_agg = AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50);
        attr_id_agg.subAggregation(attr_name_agg);
        attr_id_agg.subAggregation(attr_value_agg);

        nestedAggregationBuilder.subAggregation(attr_id_agg);
        sourceBuilder.aggregation(nestedAggregationBuilder);

        String s = sourceBuilder.toString();
        System.out.println("构建的DSL"+s);

        SearchRequest request = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, sourceBuilder);
        return request;

    }

    /**
     * 构建结果数据
     * @param response
     * @return
     */
    private SearchResult buildSearchResult(SearchResponse response,SearchParam param) {
        SearchResult result = new SearchResult();
        //1、返回的所有查询到的商品
        SearchHits hits = response.getHits();
        List<SkuEsModel> esModels = new ArrayList<>();
        if (null != hits.getHits() && hits.getHits().length>0){
            for (SearchHit hit : hits.getHits()) {
                String sourceAsString = hit.getSourceAsString();
                SkuEsModel esModel = JSON.parseObject(sourceAsString, SkuEsModel.class);
                if (!StringUtils.isEmpty(param.getKeyword())) {
                    HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
                    esModel.setSkuTitle(skuTitle.fragments()[0].string());
                }
                esModels.add(esModel);
            }
        }
        result.setProducts(esModels);
        //2、当前所有商品涉及到的所有属性
        List<SearchResult.AttrVo> attrVos = new ArrayList<>();
        ParsedNested attr_agg = response.getAggregations().get("attr_agg");
        ParsedLongTerms attr_id_agg = attr_agg.getAggregations().get("attr_id_agg");
        for (Terms.Bucket bucket : attr_id_agg.getBuckets()) {
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
            //1、得到属性的id;
            long attrId = bucket.getKeyAsNumber().longValue();
            //2、得到属性的名字
            String attrName = ((ParsedStringTerms) bucket.getAggregations().get("attr_name_agg")).getBuckets().get(0).getKeyAsString();
            //3、得到属性的所有值
            List<String> attrValues = ((ParsedStringTerms) bucket.getAggregations().get("attr_value_agg")).getBuckets().stream().map(item -> {
                String keyAsString = item.getKeyAsString();
                return keyAsString;
            }).collect(Collectors.toList());
            attrVo.setAttrId(attrId);
            attrVo.setAttrName(attrName);
            attrVo.setAttrValue(attrValues);
            attrVos.add(attrVo);
        }

        result.setAttrs(attrVos);
        //3、当前所有品牌涉及到的所有属性
        List<SearchResult.BrandVo> brandVos = new ArrayList<>();
        ParsedLongTerms brand_agg = response.getAggregations().get("brand_agg");
        for (Terms.Bucket bucket : brand_agg.getBuckets()) {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
            //1、得到品牌的id
            long brandId = bucket.getKeyAsNumber().longValue();
            //2、得到品牌的名
            String brandName = ((ParsedStringTerms) bucket.getAggregations().get("brand_name_agg")).getBuckets().get(0).getKeyAsString();
            //3、得到品牌的图片
            String brandImg = ((ParsedStringTerms) bucket.getAggregations().get("brand_img_agg")).getBuckets().get(0).getKeyAsString();
            brandVo.setBrandId(brandId);
            brandVo.setBrandName(brandName);
            brandVo.setBrandImg(brandImg);
            brandVos.add(brandVo);
        }
        result.setBrands(brandVos);
        //4、当前商品所涉及的分类信息
        ParsedLongTerms catelog_agg = response.getAggregations().get("catelog_agg");

        List<SearchResult.CatelogVo> catelogVos = new ArrayList<>();
        List<? extends Terms.Bucket> buckets = catelog_agg.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            SearchResult.CatelogVo catelogVo = new SearchResult.CatelogVo();
            //得到分类id
            String keyAsString = bucket.getKeyAsString();
            catelogVo.setCatelogId(Long.parseLong(keyAsString));

            //得到分类名
            ParsedStringTerms catelog_name_agg = bucket.getAggregations().get("catelog_name_agg");
            String catelog_name = catelog_name_agg.getBuckets().get(0).getKeyAsString();
            catelogVo.setCatelogName(catelog_name);
            catelogVos.add(catelogVo);
        }
        result.setCatelogs(catelogVos);
        //===========以上从聚合信息获取到=============
        //5、分页信息-页码
        result.setPageNum(param.getPageNum());
        //6、分页信息-总记录数
        long total = hits.getTotalHits().value;
        result.setTotal(total);
        //7、分页信息-总页码-计算
        int totalPages = total%EsConstant.PRODUCT_PAGESIZE == 0 ?(int) total/EsConstant.PRODUCT_PAGESIZE:((int)total/EsConstant.PRODUCT_PAGESIZE+1);
        result.setTotalPages(totalPages);
        return result;
    }

}
