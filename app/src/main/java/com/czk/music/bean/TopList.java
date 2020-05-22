package com.czk.music.bean;

import java.util.List;

/**
 * Created by TWOSIX on 2020/5/22.
 * qq邮箱： 1023110828@qq.com
 * Describe:
 */
public class TopList {
    /**
     * id : 4
     * listenCount : 10000000
     * picUrl : http://y.gtimg.cn/music/photo_new/T003R300x300M000000mvJrs20UzZw.jpg
     * songList : [{"singername":"毛不易","songname":"入海"},{"singername":"许嵩/刘美麟","songname":"温泉"},{"singername":"张艺兴","songname":"玉（Jade）"}]
     * topTitle : 巅峰榜·流行指数
     * type : 0
     */
    private int id;
    private int listenCount;
    private String picUrl;
    private String topTitle;
    private int type;
    private List<SongListBean> songList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getListenCount() {
        return listenCount;
    }

    public void setListenCount(int listenCount) {
        this.listenCount = listenCount;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getTopTitle() {
        return topTitle;
    }

    public void setTopTitle(String topTitle) {
        this.topTitle = topTitle;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<SongListBean> getSongList() {
        return songList;
    }

    public void setSongList(List<SongListBean> songList) {
        this.songList = songList;
    }

    public static class SongListBean {
        /**
         * singername : 毛不易
         * songname : 入海
         */
        private String singername;
        private String songname;

        public String getSingername() {
            return singername;
        }

        public void setSingername(String singername) {
            this.singername = singername;
        }

        public String getSongname() {
            return songname;
        }

        public void setSongname(String songname) {
            this.songname = songname;
        }
    }

    @Override
    public String toString() {
        return "TopList{" +
                "id=" + id +
                ", listenCount=" + listenCount +
                ", picUrl='" + picUrl + '\'' +
                ", topTitle='" + topTitle + '\'' +
                ", type=" + type +
                ", songList=" + songList +
                '}';
    }
}
