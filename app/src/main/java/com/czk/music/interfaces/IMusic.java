package com.czk.music.interfaces;

import com.czk.music.bean.Song;

/**
 * Created by TWOSIX on 2020/4/13.
 * qq邮箱： 1023110828@qq.com
 * Describe:控制音乐播放的接口
 */
public interface IMusic {
    void play();
    void pause();
    void changeSong(Song song,String vkey);
    void nextSong();
    void lastSong();
    void seekTo(int position);
}
