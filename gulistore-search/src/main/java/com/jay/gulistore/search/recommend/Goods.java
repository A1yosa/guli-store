package com.jay.gulistore.search.recommend;

public class Goods implements Comparable<Goods> {
    public String goodsName;
    public int score;
    public Goods(String goodsName, int score) {
        this.goodsName = goodsName;
        this.score = score;
    }

    @Override
    public String toString() {
        return "Goods{" +
                "goodsName='" + goodsName + '\'' +
                ", score=" + score +
                '}';
    }

    @Override
    public int compareTo(Goods o) {
        return score > o.score ? -1 : 1;
    }

}

