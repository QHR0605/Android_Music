package com.czk.music.bean;

import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TWOSIX on 2020/4/15.
 * qq邮箱： 1023110828@qq.com
 * Describe:音乐播放的上下文，用于打开软件，恢复上次播放的歌曲
 * 其实也就是自己写的musicbinder里成员变量
 */
public class MusicContext extends LitePalSupport {
    private List<Song> songs = new ArrayList<>();       //当前播放歌曲上下文中，包含的所有歌曲列表
    private int currentIndex =0;                          //当前播放歌曲的索引
    private String imageUrl="";                            //当前歌曲的图片
    private String songUrl="";                             //当前播放音乐地址
    private List<String> songLyric;                               //歌词
    private List<Float> timeLyric;                           //歌词对应的时间
    private int duration;                                     //歌曲总时长
    private int currentTime;                                  //当前播放的时间

    public MusicContext() {
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }

    public List<String> getSongLyric() {
        return songLyric;
    }

    public void setSongLyric(List<String> songLyric) {
        this.songLyric = songLyric;
    }

    public List<Float> getTimeLyric() {
        return timeLyric;
    }

    public void setTimeLyric(List<Float> timeLyric) {
        this.timeLyric = timeLyric;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(int currentTime) {
        this.currentTime = currentTime;
    }

    @Override
    public String toString() {
        return "MusicContext{" +
                ", currentIndex=" + currentIndex +
                ", imageUrl='" + imageUrl + '\'' +
                ", songUrl='" + songUrl + '\'' +
                ", songLyric=" + songLyric +
                ", timeLyric=" + timeLyric +
                ", duration=" + duration +
                ", currentTime=" + currentTime +
                '}';
    }
}
