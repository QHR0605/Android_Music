package com.czk.music.interfaces;

/**
 * Created by TWOSIX on 2020/4/16.
 * qq邮箱： 1023110828@qq.com
 * Describe:下载的接口
 */
public interface IDownload {
    void onProgress(int progress);
    void onSuccess();
    void onFailed() ;
    void onPaused() ;
    void onCanceled();
}
