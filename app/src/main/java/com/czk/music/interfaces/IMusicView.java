package com.czk.music.interfaces;

/**
 * Created by TWOSIX on 2020/4/13.
 * qq邮箱： 1023110828@qq.com
 * Describe:改变音乐界面的接口
 */
public interface IMusicView {
    void songChange();//当歌曲改变时，界面更新
    void musicStateChange(int state);//播放暂停，界面更新
}
