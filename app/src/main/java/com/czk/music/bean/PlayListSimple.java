package com.czk.music.bean;

import org.litepal.crud.LitePalSupport;

public class PlayListSimple extends LitePalSupport {
    private String dissid;
    private String dissname;
    private String imgurl;
    private int listennum;

    public PlayListSimple() {
    }

    public PlayListSimple(String dissid, String dissname, String imgurl, int listennum) {
        this.dissid = dissid;
        this.dissname = dissname;
        this.imgurl = imgurl;
        this.listennum = listennum;
    }

    public String getDissid() {
        return dissid;
    }

    public void setDissid(String dissid) {
        this.dissid = dissid;
    }

    public String getDissname() {
        return dissname;
    }

    public void setDissname(String dissname) {
        this.dissname = dissname;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public int getListennum() {
        return listennum;
    }

    public void setListennum(int listennum) {
        this.listennum = listennum;
    }

    @Override
    public String toString() {
        return "PlayListSimple{" +
                "dissid='" + dissid + '\'' +
                ", dissname='" + dissname + '\'' +
                ", imgurl='" + imgurl + '\'' +
                ", listennum=" + listennum +
                '}';
    }

    /*dissname: "民谣对唱：文艺青年的柔声倾诉"
    imgurl: "http://p.qpic.cn/music_cover/T0qpeJj1MpLkoxkZMuVVx6efRz1bfKh3Ps8GPWiaw1AFrVLufTIaO0w/600?n=1"
    introduction: ""
    listennum: 5963103*/
}
