package com.czk.music.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.czk.music.R;
import com.czk.music.asyncTask.DownloadAsyncTask;
import com.czk.music.bean.Song;
import com.czk.music.interfaces.IDownload;

public class DownLoadService extends Service {
    private DownloadAsyncTask mDownloadAsyncTask;
    private NotificationManager manager;
    private IDownload mIDownload = new IDownload() {

        @Override
        public void onProgress(int progress) {
            Notification notification = getNotification("正在下载音乐",progress);
            manager.notify(2,notification);
        }

        @Override
        public void onSuccess() {
            mDownloadAsyncTask = null;
            stopForeground(true);
            Notification notification = getNotification("下载成功",100);
            manager.notify(2,notification);
            mDownLoadBinder.getSong().setDownload(1);
            mDownLoadBinder.getSong().save();
        }

        @Override
        public void onFailed() {
            mDownloadAsyncTask = null;
            stopForeground(true);
            Notification notification = getNotification("下载失败",-1);
            manager.notify(2,notification);
        }

        @Override
        public void onPaused() {
            mDownloadAsyncTask = null;
            //Notification notification = getNotification("暂停下载",-1);
        }

        @Override
        public void onCanceled() {
            mDownloadAsyncTask.cancelDownload();
            mDownloadAsyncTask = null;
            stopForeground(true);
        }
    };
    private DownLoadBinder mDownLoadBinder = new DownLoadBinder();
    private String downloadUrl;

    @Override
    public void onCreate() {
        super.onCreate();
        manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        NotificationChannel channel = null;
        //创建通道id
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel("DownloadService", "DownloadService", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setSound(null,null);
            manager.createNotificationChannel(channel);
        }else {
            return;
        }
    }
    private Notification getNotification(String title, int progress) {
       /* Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this,0,intent, 0);*/
        NotificationCompat.Builder builder = new NotificationCompat.Builder(DownLoadService.this,"DownloadService")
                .setContentTitle(title)
                .setSmallIcon(R.drawable.app_img)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.loading_spinner))
                .setProgress(100,progress,false)
                .setAutoCancel(true);
        if (progress > 0) {
            //当progress大于或等于0时才需显示下载进度
            builder.setContentText(progress + "%") ;
            builder.setProgress(100,progress,false) ;
        }
        return builder.build();
    }

    public class DownLoadBinder extends Binder {
        private Song mSong;//下载的歌曲
        public void startDownload(String url,String filename,String storeDir) {
            if (mDownloadAsyncTask == null) {
                downloadUrl = url;
                mDownloadAsyncTask = new DownloadAsyncTask(mIDownload);
                mDownloadAsyncTask.execute(downloadUrl,filename,storeDir);
               // mSong.setDownloadUrl(storeDir+"/zkMusic/"+filename);
                startForeground(2, getNotification("Downloading...", 0));
            }
        }
        /*public void pauseDownload() {
            if (mDownloadAsyncTask != null) {
                mDownloadAsyncTask.pauseDownload();
            }
        }
        public void cancelDownload() {
            if (mDownloadAsyncTask!= null) {
                mDownloadAsyncTask.cancelDownload();
            } else {
                if (downloadUrl != null) {
                    //取消下载时需将文件删除，并将通知关闭
                    manager.cancel(1);
                    stopForeground(true);
                }
            }
        }*/

        public Song getSong() {
            return mSong;
        }
        public void setSong(Song song) {
            mSong = song;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mDownLoadBinder;
    }
}
