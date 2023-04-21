package com.jay.gulistore.search.recommend;

import java.util.ArrayList;
import java.util.List;

/**
 * @author junfeng.lin
 * @date 2021/3/18 13:39
 */
public class User {
    public String username;
    public List<Goods> goodsList = new ArrayList<>();

    public User() {}

    public User(String username) {
        this.username = username;
    }

    public User set(String goodsName, int score) {
        this.goodsList.add(new Goods(goodsName, score));
        return this;
    }

    public Goods find(String goodsName) {
        for (Goods goods : goodsList) {
            if (goods.goodsName.equals(username)) {
                return goods;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                '}';
    }
}

