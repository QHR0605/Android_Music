package com.czk.music.ui.bottom;

import android.Manifest;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.czk.music.R;
import com.czk.music.base.BaseActivity;
import com.czk.music.bean.Song;
import com.czk.music.broadcast.SongChangeReceiver;
import com.czk.music.interfaces.IMusicView;
import com.czk.music.util.ApplicationUtil;
import com.czk.music.util.MusicUtil;
import com.czk.music.util.StateUtil;

public class PlayMusicActivity extends BaseActivity {
    private Context mContext;

    private TextView mSongName;
    private TextView mSingerName;
    private TextView mSongLrc;
    private TextView mTimeEnd;
    private TextView mTimeStart;

    private ImageView mSongImg;
    private ImageView mPlayImg;
    private ImageView mNextSong;
    private ImageView mLastSong;
    private ImageView mSongDownload;
    private ImageView mSongLikeImg;

    private ObjectAnimator objectAnimation;//歌曲图片的旋转动画
    private LocalBroadcastManager mlocalBroadcastManager;
    private SongChangeReceiver mSongChangeReceiver;
    private SeekBar mSeekBar;
    private boolean isplay;

    private Song mSong;

    private final int  REQUEST_CODE_STORE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);

        initView();
        initInfo();
        initPermission();
        initEvent();
        initBroadcast();
    }



    private void initView(){
        mContext = this;
        mSongImg = findViewById(R.id.music_img);
        mSongName =  findViewById(R.id.music_song_name);
        mSongLrc = findViewById(R.id.music_lrc);
        mSingerName = findViewById(R.id.music_singer_name);
        mTimeStart = findViewById(R.id.music_time_start);
        mTimeEnd = findViewById(R.id.music_time_end);
        mPlayImg = findViewById(R.id.music_play_song);
        mNextSong = findViewById(R.id.music_next_song);
        mLastSong = findViewById(R.id.music_last_song);
        mSeekBar = findViewById(R.id.music_seek_bar);
        mSongLikeImg = findViewById(R.id.music_like_song);
        mSongDownload = findViewById(R.id.music_download_song);
        //初始化旋转动画
        objectAnimation = (ObjectAnimator) AnimatorInflater.loadAnimator(ApplicationUtil.getContext(),R.animator.rotate);
        objectAnimation.setTarget(mSongImg);
    }
    private void initEvent(){
        //点击返回
        findViewById(R.id.music_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //播放暂停
        mPlayImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.czk.music.broadcast.SongChangeReceiver");
                if(MusicUtil.musicBind.isIsplay()){
                    intent.putExtra("state", StateUtil.PAUSE_SONG);
                }else{
                    intent.putExtra("state",StateUtil.PLAY_SONG);
                }
                intent.setComponent(new ComponentName("com.czk.music","com.czk.music.broadcast.SongChangeReceiver"));
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            }
        });
        //下一首
        mNextSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicUtil.musicBind.nextSong();
            }
        });
        //上一首
        mLastSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicUtil.musicBind.lastSong();
            }
        });
        //我喜欢
        mSongLikeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSong.getLike() == 1){
                    mSongLikeImg.setBackgroundResource(R.drawable.ic_song_like);
                    removeSongLike(mSong);
                }else{
                    mSongLikeImg.setBackgroundResource(R.drawable.ic_song_likered);
                    addSongLike(mSong);
                }
            }
        });
        //下载音乐
        mSongDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicUtil.downLoadBinder.setSong(mSong);
                MusicUtil.downLoadBinder.startDownload(MusicUtil.musicBind.getSongUrl(),
                        mSong.getName()+"-"+mSong.getSinger()+".m4a",
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath());

            }
        });
        //进度条拖动事件
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //进度条改变
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //正在拖动
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //停止拖动
                MusicUtil.musicBind.seekTo( seekBar.getProgress() );
            }
        });
    }
    private void initBroadcast() {
        mlocalBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.czk.music.broadcast.SongChangeReceiver");
        mSongChangeReceiver = new SongChangeReceiver(mIMusicView);//当歌曲改变时，改变底部播放栏状态
        //注册本地广播监听器
        mlocalBroadcastManager.registerReceiver(mSongChangeReceiver, intentFilter);
    }
    //初始化页面信息
    private void initInfo() {
        mIMusicView.songChange();//初始化页面信息
        if(MusicUtil.musicBind.isIsplay()){
            mPlayImg.setBackgroundResource(R.drawable.ic_song_pause);
            startAnimation(); //开启动画
            isplay = true;
            //用于更新歌词的线程
            new LyricThread().start();
        }
    }
    private void initPermission() {
        //请求权限
        if (ContextCompat.checkSelfPermission (PlayMusicActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PlayMusicActivity.this,
                    new String[]{ Manifest. permission. WRITE_EXTERNAL_STORAGE},REQUEST_CODE_STORE);
        }

    }
    //格式化时间，将其转化为00:00形式
    private String formatTime(int time) {
        //歌曲结束时间
        String formatTime = "";
        int sec = (int) Math.floor(time%60);
        int min = (int) Math.floor(time/60);
        if(min<10){
            formatTime += "0"+min+":";
        }else {
            formatTime +=min+":";
        }
        if(sec<10){
            formatTime += "0"+sec;
        }else {
            formatTime +=sec;
        }
        return formatTime;
    }
    //添加我喜欢的歌
    private void addSongLike(Song song){
        song.setLike(1);
        song.save();
        Toast.makeText(mContext,"歌曲已添加至我喜欢",Toast.LENGTH_SHORT).show();
    }
    //移出我喜欢的歌
    private void removeSongLike(Song song){
        song.setLike(0);
        song.save();
        Toast.makeText(mContext,"歌曲已从我喜欢移出，嘤嘤嘤",Toast.LENGTH_SHORT).show();
    }
    private IMusicView mIMusicView = new IMusicView() {
        @Override
        public void songChange() {
            mSong = MusicUtil.musicBind.getSong();
            //设置图片
            Glide.with(mContext)
                    .load(Uri.parse(MusicUtil.musicBind.getImageUrl()))
                    .centerCrop()
                    .placeholder(R.drawable.loading_spinner)
                    .error(R.drawable.loading_error)
                    .into(mSongImg);
            //设置歌名
            mSongName.setText(mSong.getName());
            //设置歌手名
            mSingerName.setText(mSong.getSinger());
            //设置音乐完整时间
            mTimeEnd.setText(formatTime(MusicUtil.musicBind.getDuration()));
            //设置我喜欢图标
            if(mSong.getLike() == 1){
                mSongLikeImg.setBackgroundResource(R.drawable.ic_song_likered);
            }else {
                mSongLikeImg.setBackgroundResource(R.drawable.ic_song_like);
            }
        }

        @Override
        public void musicStateChange(int state) {
            switch (state){
                case StateUtil.CHANGE_SONG:
                    mSongImg.clearAnimation();
                    startAnimation();
                    if(!isplay){
                        mPlayImg.setBackgroundResource(R.drawable.ic_song_pause);
                        isplay = true;
                        new LyricThread().start();
                    }
                    break;
                case StateUtil.PLAY_SONG:
                    if ("".equals(MusicUtil.musicBind.getSongUrl())) {
                        Toast.makeText(mContext, "当前没有要播放的歌曲哦~~", Toast.LENGTH_SHORT).show();
                    } else {
                        mPlayImg.setBackgroundResource(R.drawable.ic_song_pause);
                        startAnimation(); //开启动画
                        isplay = true;
                        //用于更新歌词的线程
                        new LyricThread().start();
                    }
                    break;
                case StateUtil.PAUSE_SONG:
                    mPlayImg.setBackgroundResource(R.drawable.ic_song_play);
                    MusicUtil.musicBind.pause();
                    pauseAnimation();//暂停动画
                    isplay = false;
                    break;
            }
        }
    };
    private void startAnimation(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (objectAnimation.isPaused()){
                objectAnimation.resume();
            }else {
                objectAnimation.start();
            }
        }
    }
    private void pauseAnimation(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            objectAnimation.pause();
        }
    }
    class LyricThread extends Thread{
        String lyric;
        @Override
        public void run() {
            while (isplay){
                //Log.d("thread","acivity"+Thread.currentThread().getName());
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lyric  = MusicUtil.musicBind.updateLyric();
                mSongLrc.setText(lyric);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //更新歌词
                        mTimeStart.setText(formatTime(MusicUtil.musicBind.getCurrentTime()));
                        //更新进度条
                        int process = MusicUtil.musicBind.getCurrentTime()*100/ MusicUtil.musicBind.getDuration();
                        mSeekBar.setProgress(process);
                    }
                });
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                             int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_STORE:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "嘤嘤嘤,拒绝权限将无法使用程序哦", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                }
            default:
                break;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mlocalBroadcastManager.unregisterReceiver(mSongChangeReceiver);
        isplay = false;
    }
}
