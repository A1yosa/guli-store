package com.jay.gulistore.search.test;


import com.jay.gulistore.search.recommend.Goods;
import com.jay.gulistore.search.recommend.Recommend;
import com.jay.gulistore.search.recommend.User;

import java.util.ArrayList;
import java.util.List;

/**
 * @author junfeng.lin
 * @date 2021/3/18 14:07
 */
public class Demo {
    public static void main(String[] args) {
        //输入用户总量
        List<User> users = new ArrayList<>();
        users.add(new User("zuomie")
                .set("ViVo", 50)
                .set("OPPO", 30)
                .set("APPLE", 45)
                .set("HUAWEI", 50)
                .set("RED MO", 30)
                .set("XIAO MI", 45)
                .set("GT NEo", 50));

        users.add(new User("user2")
                .set("IQOO", 40)
                .set("APPLE", 30)
                .set("RED MO", 50)
                .set("XIAO MI", 50)
                .set("GT NEo", 30)
                .set("RED MI", 30));


        users.add(new User("user3")
                .set("IQOO", 20)
                .set("ViVo", 50)
                .set("RED MO", 30)
                .set("HUAWEI", 50)
                .set("XIAO MI", 45)
                .set("LIAN XIANG", 50));

        users.add(new User("user4")
                .set("IQOO", 50)
                .set("ViVo", 30)
                .set("RED MO", 40)
                .set("NU BI Ya", 40)
                .set("LIAN XIANG", 35)
                .set("GT NEo", 35)
                .set("RED MI", 45));

        users.add(new User("user5")
                .set("IQOO", 20)
                .set("ViVo", 40)
                .set("APPLE", 45)
                .set("HUAWEI", 50)
                .set("RED MO", 20));

        users.add(new User("user6")
                .set("APPLE", 50)
                .set("MEIZU", 50)
                .set("RED MO", 30)
                .set("LIAN XIANG", 50)
                .set("RONGYAO", 45)
                .set("HUAWEI", 40)
                .set("RED MI", 35));

        users.add(new User("user7")
                .set("APPLE", 50)
                .set("ViVo", 40)
                .set("RED MO", 10)
                .set("Phoenix", 50)
                .set("NUOJIYA", 40)
                .set("The Strokes", 50));

        users.add(new User("user8")
                .set("IQOO", 40)
                .set("RED MO", 45)
                .set("NU BI Ya", 45)
                .set("NUOJIYA", 25)
                .set("The Strokes", 30));


        Recommend recommend = new Recommend();
        List<Goods> recommendationGoods = recommend.recommend("zuomie", users);
        System.out.println("-----------------------");
        System.out.println("Recommend for you:");
        for (Goods goods : recommendationGoods) {
            System.out.println("Goods : "+goods.goodsName+" ,Score : "+goods.score);
        }
    }
}


