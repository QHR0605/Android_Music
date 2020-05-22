package com.czk.music.asyncTask;

import android.os.AsyncTask;

import com.czk.music.interfaces.IDownload;
import com.czk.music.util.StateUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

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
    private boolean isPause = false;
    private boolean isCancelled = false;
    private int lastProcess = 0;

    public DownloadAsyncTask(IDownload IDownload) {
        mIDownload = IDownload;
    }

    @Override
    protected Integer doInBackground(String... strings) {
        InputStream inputStream = null;
        RandomAccessFile saveFile = null;
        File file = null;
        String songUrl = strings[0];
        String fileName = strings[1];
        String storeDir = strings[2];
        try {
            long downloadLength = 0;
            file = new File(storeDir+"/"+fileName);
            if(file.exists()){
                downloadLength = file.length();
            }
            long contentLength = getContentLength(songUrl);
            if(contentLength==0){
                return StateUtil.DOWNLOAD_FAILED;
            }else if(contentLength == downloadLength){
                return StateUtil.DOWNLOAD_SUCCESS;
            }
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                //断点下载，指定从哪个字节开始下载
                .addHeader( "RANGE", "bytes=" + downloadLength + "-")
                .url(songUrl)
                .build();
            Response response = client.newCall(request).execute();
            if(response!=null){
                inputStream = response.body().byteStream();
                saveFile = new RandomAccessFile(file,"rw");
                saveFile.seek(downloadLength);
                byte[] all = new byte[1024];
                int len = -1;
                int total = 0;
                while ((len = inputStream.read(all))!=-1) {
                    if(isCancelled){
                        return StateUtil.DOWNLOAD_CANCEL;
                    }else if(isPause){
                        return StateUtil.DOWNLOAD_PAUSE;
                    }else {
                        total+=len;
                        saveFile.write(all,0,len);
                        int process = (int) ((total+downloadLength)*100/contentLength);
                        publishProgress(process);
                    }

                }
            }
            response.body().close();
            inputStream.close();
            return StateUtil.DOWNLOAD_SUCCESS;
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally {
            if(inputStream!=null) {
                inputStream = null;
            }
            if(isCancelled()&&file!=null){
                file.delete();
            }
            if(saveFile!=null){
                saveFile = null;
            }
        }
        return StateUtil.DOWNLOAD_FAILED;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
       int process = values[0];
       if(process>lastProcess){
           mIDownload.onProgress(process);
           lastProcess = process;
       }
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
    public void pauseDownload(){
        isPause = true;
    }
    public void cancelDownload(){
        isCancelled = true;
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
