package com.czk.music.bean;

import org.litepal.crud.LitePalSupport;

/**
 * Created by TWOSIX on 2020/4/10.
 * qq邮箱： 1023110828@qq.com
 * Describe:搜索的关键词
 */
public class SearchKey extends LitePalSupport {
    private String key;

    public SearchKey() {
    }

    public SearchKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
