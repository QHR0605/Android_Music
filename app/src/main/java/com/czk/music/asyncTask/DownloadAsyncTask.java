package com.czk.music.asyncTask;

import android.os.AsyncTask;

import com.czk.music.interfaces.IDownload;
import com.czk.music.util.StateUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by TWOSIX on 2020/4/16.
 * qq邮箱： 1023110828@qq.com
 * Describe:
 */
public class DownloadAsyncTask extends AsyncTask<String,Integer,Integer> {
    private IDownload mIDownload;

    private boolean isFailed = false;
    private boolean isPause =false;
    private int lastProcess = 0;

    public void setIDownload(IDownload iDownload){
        this.mIDownload = iDownload;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(String... strings) {
        InputStream inputStream = null;
        File file = null;

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Integer state) {
        switch (state){
            case StateUtil.DOWNLOAD_SUCCESS:
                mIDownload.onSuccess();
                break;
            case StateUtil.DOWNLOAD_FAILED:
                mIDownload.onFailed();
                break;
            case StateUtil.DOWNLOAD_CANCEL:
                mIDownload.onCanceled();
                break;
            case StateUtil.DOWNLOAD_PAUSE:
                mIDownload.onPaused();
                break;
        }
    }
    //获取连接长度
    private long getContentLength(String downloadUrl) throws IOException {
        OkHttpClient client = new OkHttpClient() ;
        Request request = new Request.Builder()
                .url(downloadUrl)
                .build();
        Response response = client.newCall(request).execute();
        if (response != null && response.isSuccessful()) {
            long contentLength = response.body().contentLength() ;
            response.close();
            return contentLength;
        }
        return 0;
    }

}
