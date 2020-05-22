package com.czk.music;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.czk.music.base.BaseActivity;
import com.czk.music.bean.MusicContext;
import com.czk.music.bean.Song;
import com.czk.music.service.DownLoadService;
import com.czk.music.service.MusicService;
import com.czk.music.util.MusicUtil;
import com.google.android.material.navigation.NavigationView;

import org.litepal.LitePal;

import java.util.List;

public class MainActivity extends BaseActivity {
    private final String tag = "MainActivity";
    private Context mContext;
    private AppBarConfiguration mAppBarConfiguration;
    private NavController mNavController;

    private Intent mIntent;//用于服务的intent
    private Intent mIntentDownload;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicUtil.musicBind = (MusicService.MusicBind) service;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            MusicUtil.musicBind = null;
        }
    };
    private ServiceConnection connectionDownload = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicUtil.downLoadBinder = (DownLoadService.DownLoadBinder) service ;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            MusicUtil.downLoadBinder = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        initService();

    }
    private void init() {
        mContext = this;
        //设置toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setElevation(0);
        //侧边导航与导航组件
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home)
                .setDrawerLayout(drawer)
                .build();
        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, mNavController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, mNavController);
        //导航点击事件
        mNavController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller,
                                             @NonNull NavDestination destination, @Nullable Bundle arguments) {
                /*switch (destination.getId()){
                    case R.id.nav_song_list:
                        //toolbar.setVisibility(View.GONE);
                        getSupportActionBar().show();
                        break;
                    case R.id.nav_home:
                        getSupportActionBar().show();
                        break;
                    default:
                        //getSupportActionBar().show();
                        break;
                }*/

            }
        });

        //注册toolbar点击事件
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    /*case R.id.action_settings:
                        break;*/
                    case R.id.action_search:
                        getSupportActionBar().hide();
                        mNavController.navigate(R.id.action_global_nav_search);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }
    private void initService() {
        //音乐播放服务
        mIntent = new Intent(this, MusicService.class);
        startService(mIntent);
        bindService(mIntent,connection,Context.BIND_AUTO_CREATE);
        //音乐下载服务
        mIntentDownload = new Intent(this, DownLoadService.class);
        startService(mIntentDownload);
        bindService(mIntentDownload,connectionDownload,Context.BIND_AUTO_CREATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //保存退出时音乐上下文
        //saveMusicContext();
        //销毁服务

        unbindService(connection);
        stopService(mIntent);
        unbindService(connectionDownload);
        stopService(mIntentDownload);
    }
    private void saveMusicContext(){
        //保存音乐上下文，用于打开应用恢复 音乐关闭时的状态
        int i=0;//只保存100首
        LitePal.deleteAll(MusicContext.class);
        MusicContext musicContext = new MusicContext();
        List<Song> songs = MusicUtil.musicBind.getSongs();
        for (Song item:songs) {
            item.setMusicContext(musicContext);
            item.setLike(0);
            item.setHistory(0);
            item.clearSavedState();//清除对象的保存状态
            item.save();
            if(i>=99){
                break;
            }
            i++;
        }
        musicContext.setCurrentIndex(MusicUtil.musicBind.getCurrentIndex());
        musicContext.setImageUrl(MusicUtil.musicBind.getImageUrl());
        musicContext.setSongUrl(MusicUtil.musicBind.getSongUrl());
        musicContext.setSongLyric(MusicUtil.musicBind.getSongLyric());
        musicContext.setTimeLyric(MusicUtil.musicBind.getTimeLyric());
        musicContext.setDuration(MusicUtil.musicBind.getDuration());
        musicContext.setCurrentTime(MusicUtil.musicBind.getCurrentTime());
        musicContext.save();
    }
    public void changeTheme(View view){
        Button button = (Button) view;
        String text = (String) button.getText();
        changeThemeByName(text);
        //获取SharedPreferences 对象
        SharedPreferences sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        //获取SharedPreferences.Editor对象
        SharedPreferences.Editor edit=  sharedPreferences.edit();
        //添加数据
        edit.putString("theme",text);
        //提交
        edit.commit();
        //重启程序
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
       /* ApplicationUtil.finishAll();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());*/

    }
}
