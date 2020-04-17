package com.czk.music.ui.bottom;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.czk.music.R;
import com.czk.music.broadcast.SongChangeReceiver;
import com.czk.music.interfaces.IMusicView;
import com.czk.music.service.MusicService;
import com.czk.music.util.ApplicationUtil;
import com.czk.music.util.StateUtil;


/**
 * Created by TWOSIX on 2020/4/4.
 * qq邮箱： 1023110828@qq.com
 * Describe:音乐底部
 */
public class MusicBottomFragment extends Fragment {
    private Context mContext;
    private View view;

    private ImageView mPlayView;//播放，暂停图片
    private ImageView songImg;//歌曲图片
    private TextView mLycView;//歌词
    private TextView mSongName;//歌名

    private ObjectAnimator objectAnimation;//歌曲图片的旋转动画

    private LocalBroadcastManager localBroadcastManager;//本地广播管理器
    private SongChangeReceiver songChangeReceiver;//接受歌曲改变的广播

    private MusicService.MusicBind musicBinder;//播放音乐服务的binder

    //private LyricThread mLyricThread;//更新歌词的线程

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mContext = getContext();
        view = inflater.inflate(R.layout.fragment_music_bottom, container, false);
        initView();
        initBroadcast();
        initEvent();
        return view;
    }

    private void initView() {
        songImg = view.findViewById(R.id.current_img);
        mPlayView = view.findViewById(R.id.current_play);
        mLycView = view.findViewById(R.id.current_song_lyc);
        mSongName = view.findViewById(R.id.current_song_name);

        //初始化旋转动画
        objectAnimation = (ObjectAnimator) AnimatorInflater.loadAnimator(ApplicationUtil.getContext(),R.animator.rotate);
        objectAnimation.setTarget(songImg);

    }
    private void initBroadcast() {
        localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.czk.music.broadcast.SongChangeReceiver");
        songChangeReceiver = new SongChangeReceiver(mIMusicView);//当歌曲改变时，改变底部播放栏状态
        //注册本地广播监听器
        localBroadcastManager.registerReceiver(songChangeReceiver, intentFilter);
    }
    private void initEvent() {
        //播放音乐,暂停音乐
        final ImageView playMusic = view.findViewById(R.id.current_play);
        playMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(musicBinder.isIsplay()){
                mIMusicView.musicStateChange(StateUtil.PAUSE_SONG);
            }else{
                mIMusicView.musicStateChange(StateUtil.PLAY_SONG);
            }
            }
        });
        //点击进入歌曲页面
        view.findViewById(R.id.current_song_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PlayMusicActivity.class);
                intent.putExtra("musicBinder", musicBinder);
                startActivity(intent);
            }
        });
    }
    //从activity获取musicBinder
    public void setBind(MusicService.MusicBind musicBind){
        this.musicBinder = musicBind;
        if (musicBind.getSongs().size() > 0) {
            mIMusicView.songChange();
        }

    }
    private IMusicView mIMusicView = new IMusicView() {
        @Override
        public void songChange() {
            //设置图片
            Glide.with(mContext)
                    .load(Uri.parse(musicBinder.getImageUrl()))
                    .centerCrop()
                    .placeholder(R.drawable.loading_spinner)
                    .error(R.drawable.loading_error)
                    .into(songImg);
            //设置歌名
            mSongName.setText(musicBinder.getSong().getName());
        }

        @Override
        public void musicStateChange(int state) {
            switch (state){
                case StateUtil.CHANGE_SONG:
                    if(musicBinder.isIsplay()){
                        musicBinder.play();
                        songImg.clearAnimation();
                        startAnimation();
                    }else {
                        musicBinder.play();
                        songImg.clearAnimation();
                        startAnimation();
                        mPlayView.setBackgroundResource(R.drawable.ic_pause);
                        new LyricThread().start();
                    }
                    break;
                case StateUtil.PLAY_SONG:
                    if ("".equals(musicBinder.getSongUrl())) {
                        Toast.makeText(mContext, "当前没有要播放的歌曲哦~~", Toast.LENGTH_SHORT).show();
                    } else {
                        mPlayView.setBackgroundResource(R.drawable.ic_pause);
                        startAnimation(); //开启动画
                        musicBinder.play();
                        //用于更新歌词的线程
                        new LyricThread().start();
                    }
                    break;
                case StateUtil.PAUSE_SONG:
                    mPlayView.setBackgroundResource(R.drawable.ic_play);
                    musicBinder.pause();
                    pauseAnimation();//暂停动画
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
            while (musicBinder.isIsplay()){
                try {
                    Thread.sleep(200);
                    lyric  = musicBinder.updateLyric();
                    mLycView.setText(lyric);
                    //Log.d("thread",Thread.currentThread().getName());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(songChangeReceiver);
        musicBinder.destroyMedia();
    }
}