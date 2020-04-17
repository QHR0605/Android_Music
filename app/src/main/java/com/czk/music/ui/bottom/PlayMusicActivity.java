package com.czk.music.ui.bottom;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.czk.music.R;
import com.czk.music.base.BaseActivity;
import com.czk.music.bean.Song;
import com.czk.music.broadcast.SongChangeReceiver;
import com.czk.music.interfaces.IMusicView;
import com.czk.music.service.MusicService;
import com.czk.music.util.ApplicationUtil;
import com.czk.music.util.StateUtil;

public class PlayMusicActivity extends BaseActivity {

    private MusicService.MusicBind mMusicBinder;
    private Context mContext;
    private TextView mSongName;
    private ImageView mSongImg;
    private TextView mSingerName;
    private ImageView mPlayImg;
    private ObjectAnimator objectAnimation;//歌曲图片的旋转动画
    private TextView mSongLrc;
    private View mNextSong;
    private View mLastSong;
    private LocalBroadcastManager mlocalBroadcastManager;
    private SongChangeReceiver mSongChangeReceiver;
    private TextView mTimeEnd;
    private TextView mTimeStart;
    private SeekBar mSeekBar;
    private boolean isplay;
    private ImageView mSongLikeImg;
    private Song mSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);

        Intent intent = getIntent();
        mMusicBinder = (MusicService.MusicBind) intent.getSerializableExtra("musicBinder");
        initView();
        initInfo();
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
                if(mMusicBinder.isIsplay()){
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
                mMusicBinder.nextSong();
            }
        });
        //上一首
        mLastSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMusicBinder.lastSong();
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
                mMusicBinder.seekTo( seekBar.getProgress() );
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
        if(mMusicBinder.isIsplay()){
            mPlayImg.setBackgroundResource(R.drawable.ic_song_pause);
            startAnimation(); //开启动画
            isplay = true;
            //用于更新歌词的线程
            new LyricThread().start();
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
            mSong = mMusicBinder.getSong();
            //设置图片
            Glide.with(mContext)
                    .load(Uri.parse(mMusicBinder.getImageUrl()))
                    .centerCrop()
                    .placeholder(R.drawable.loading_spinner)
                    .error(R.drawable.loading_error)
                    .into(mSongImg);
            //设置歌名
            mSongName.setText(mSong.getName());
            //设置歌手名
            mSingerName.setText(mSong.getSinger());
            //设置音乐完整时间
            mTimeEnd.setText(formatTime(mMusicBinder.getDuration()));
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
                    if ("".equals(mMusicBinder.getSongUrl())) {
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
                    mMusicBinder.pause();
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
                lyric  = mMusicBinder.updateLyric();
                mSongLrc.setText(lyric);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //更新歌词
                        mTimeStart.setText(formatTime(mMusicBinder.getCurrentTime()));
                        //更新进度条
                        int process = mMusicBinder.getCurrentTime()*100/mMusicBinder.getDuration();
                        mSeekBar.setProgress(process);
                    }
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mlocalBroadcastManager.unregisterReceiver(mSongChangeReceiver);
        isplay = false;
    }
}
