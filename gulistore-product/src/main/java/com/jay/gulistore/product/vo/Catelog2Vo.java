package com.jay.gulistore.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Catelog2Vo {
    private String catelog1Id;    //一级父分类id
    private List<Catelog3Vo> catelog3List;  //三级子分类
    private String id;
    private String name;

    //三级分类
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Catelog3Vo{
        private String catelog2Id;  //父分类，2级分类id
        private String id;
        private String name;
    }

}
