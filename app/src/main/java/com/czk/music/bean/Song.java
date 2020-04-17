package com.czk.music.bean;

import org.litepal.crud.LitePalSupport;

import java.util.List;

public class Song extends LitePalSupport {
    private String songId;
    private String name;
    private String singer;
    private String mid;//用于播放音乐
    private String album;//专辑名
    private String albumMid;//专辑id，可以获取专辑及专辑图片
    private List<Song> grp;//搜索的同一种音乐的更多版本

    private int like;   //1代表我喜欢的歌
    private int history;//1代表历史听歌记录
    private MusicContext mMusicContext;
    public Song() {
    }

    public List<Song> getGrp() {
        return grp;
    }
    public void setGrp(List<Song> grp) {
        this.grp = grp;
    }
    public String getAlbumMid() {
        return albumMid;
    }
    public void setAlbumMid(String albumMid) {
        this.albumMid = albumMid;
    }
    public String getAlbum() {
        return album;
    }
    public void setAlbum(String album) {
        this.album = album;
    }
    public String getMid() {
        return mid;
    }
    public void setMid(String mid) {
        this.mid = mid;
    }
    public String getId() {
        return songId;
    }
    public void setId(String id) {
        this.songId = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSinger() {
        return singer;
    }
    public void setSinger(String singer) {
        this.singer = singer;
    }
    public MusicContext getMusicContext() {
        return mMusicContext;
    }
    public void setMusicContext(MusicContext musicContext) {
        mMusicContext = musicContext;
    }
    public int getHistory() {
        return history;
    }
    public void setHistory(int history) {
        this.history = history;
    }
    public int getLike() {
        return like;
    }
    public void setLike(int like) {
        this.like = like;
    }

    @Override
    public String toString() {
        return "Song{" +
                "id='" + songId + '\'' +
                ", name='" + name + '\'' +
                ", singer='" + singer + '\'' +
                ", mid='" + mid + '\'' +
                ", album='" + album + '\'' +
                ", albumMid='" + albumMid + '\'' +
                ", grp=" + grp +
                '}';
    }

}
