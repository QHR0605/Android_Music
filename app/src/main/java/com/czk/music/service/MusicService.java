package com.czk.music.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;

import androidx.collection.ArrayMap;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.czk.music.bean.Song;
import com.czk.music.interfaces.IMusic;
import com.czk.music.util.HttpUtil;
import com.czk.music.util.JsonUtil;
import com.czk.music.util.StateUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Response;


/**
*author: TWOSIX
*createTime:2020/4/13
*description:
**/
public class MusicService extends Service{
    private static final String Tag = "MusicService";
    private MusicBind mMusicBind = new MusicBind();
    public MusicService() {

    }
    public class MusicBind extends Binder implements IMusic, Serializable {
        private MediaPlayer mediaPlayer = new MediaPlayer();//唯一MediaPlayer
        private boolean isplay = false;                     //当前是否正在播放音乐
        private Song song = new Song();                     //当前正在播放的歌曲
        private List<Song> songs = new ArrayList<>();       //当前播放歌曲上下文中，包含的所有歌曲
        private int currentIndex =0;                          //当前播放歌曲的索引
        private String imageUrl="";                            //当前歌曲的图片
        private String songUrl="";                             //当前播放音乐地址
        private List<String> songLyric;                               //歌词
        private List<Float> timeLyric;                           //歌词对应的时间
        private int duration;                                     //歌曲总时长
        private int currentTime;                                  //当前播放的时间
        private int guid;
        private List<Song> historySong = new ArrayList<>();
        public MusicBind() {
            //从数据库读取muscibind,恢复音乐上下文
            /*MusicContext musicContext = LitePal.findFirst(MusicContext.class,true);
            if(musicContext!=null){
                songs = musicContext.getSongs();
                currentIndex = musicContext.getCurrentIndex();
                if(songs.size()>0){
                    song = songs.get(currentIndex);
                }
                imageUrl = musicContext.getImageUrl();
                songUrl = musicContext.getSongUrl();
                songLyric = musicContext.getSongLyric();
                timeLyric = musicContext.getTimeLyric();
                duration = musicContext.getDuration();
                currentTime = musicContext.getCurrentTime();
            }
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(songUrl);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }*/

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if(songs.size()>0){
                        nextSong();
                    }
                }
            });


        }

        @Override
        public void play() {
            if(!mediaPlayer.isPlaying()&&mediaPlayer.getDuration()>0){
                mediaPlayer.start();
                isplay = true;
                //添加到历史听歌记录
                addHistorySong(song);
            }
        }
        public void destroyMedia() {
            isplay = false;
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        @Override
        public void pause() {
            if(mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                isplay = false;
            }
        }
        public void addHistorySong(Song song){
            List<Song> historySongList = LitePal.where("history = 1")
                    .find(Song.class);
            if(historySongList!=null){
                for (Song item:historySongList) {
                    if(song.getId().equals(item.getId())){
                        LitePal.deleteAll(Song.class,"history = 1 and songId = ?",song.getId());
                        break;
                    }
                }
            }
            song.setHistory(1);
            song.clearSavedState();
            song.save();
        }
        @Override
        public void changeSong(Song song,String vkey) {
            //如果是本地歌曲
            if(song.getDownload()==1){
                song.setDownloadUrl(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath()
                        +"/"+song.getName()+"-"+song.getSinger()+".m4a");
                setSongUrl(song.getDownloadUrl());
            }else{
                //setSongUrl("http://ws.stream.qqmusic.qq.com/"+"C400"+song.getMid()+".m4a?"+"fromtag=0&guid=1023110828&vkey="+vkey);
                setSongUrl("http://ws.stream.qqmusic.qq.com/"+vkey);
                //Log.d(Tag,songUrl);
            }
            //Log.d(Tag,songUrl+song.getDownload());
            //歌曲图片地址
            setImageUrl("https://y.gtimg.cn/music/photo_new/T002R300x300M000"+song.getAlbumMid()+".jpg?max_age=2592000");
            setSong(song);
            initLyric();
            try {
                mediaPlayer.reset();
                //Log.d(Tag,songUrl+"-------------");
                mediaPlayer.setDataSource(songUrl);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }

            setDuration(mediaPlayer.getDuration()/1000);
            //发送广播，通知底部音乐栏更新
            Intent intent = new Intent("com.czk.music.broadcast.SongChangeReceiver");
            intent.putExtra("state", StateUtil.CHANGE_SONG);
            intent.setComponent(new ComponentName("com.czk.music","com.czk.music.broadcast.SongChangeReceiver"));
            LocalBroadcastManager.getInstance(MusicService.this).sendBroadcast(intent);
        }

        @Override
        public void nextSong() {
            currentIndex = currentIndex+1>=songs.size()?0:currentIndex+1;
            song = songs.get(currentIndex);
            final String url = "https://u.y.qq.com/cgi-bin/musicu.fcg?format=json&data=%7B%22req_0%22%3A%7B%22module%22%3A%22vkey.GetVkeyServer%22%2C%22method%22%3A%22CgiGetVkey%22%2C%22param%22%3A%7B%22guid%22%3A%22358840384%22%2C%22songmid%22%3A%5B%22" +
                    song.getMid() +
                    "%22%5D%2C%22songtype%22%3A%5B0%5D%2C%22uin%22%3A%221443481947%22%2C%22loginflag%22%3A1%2C%22platform%22%3A%2220%22%7D%7D%2C%22comm%22%3A%7B%22uin%22%3A%2218585073516%22%2C%22format%22%3A%22json%22%2C%22ct%22%3A24%2C%22cv%22%3A0%7D%7D";
            HttpUtil.sendUQQRequestGet(url,new okhttp3.Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String vkey = JsonUtil.getSongToken(response);
                    if("".equals(vkey)){
                        nextSong();
                    }else {
                        changeSong(song,vkey);
                    }
                }
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    HttpUtil.failed(MusicService.this,"网络请求失败");
                }
            },null);
        }

        @Override
        public void lastSong() {
            currentIndex = currentIndex-1<0?songs.size()-1:currentIndex-1;
            song = songs.get(currentIndex);
            final String url = "https://u.y.qq.com/cgi-bin/musicu.fcg?format=json&data=%7B%22req_0%22%3A%7B%22module%22%3A%22vkey.GetVkeyServer%22%2C%22method%22%3A%22CgiGetVkey%22%2C%22param%22%3A%7B%22guid%22%3A%22358840384%22%2C%22songmid%22%3A%5B%22" +
                    song.getMid() +
                    "%22%5D%2C%22songtype%22%3A%5B0%5D%2C%22uin%22%3A%221443481947%22%2C%22loginflag%22%3A1%2C%22platform%22%3A%2220%22%7D%7D%2C%22comm%22%3A%7B%22uin%22%3A%2218585073516%22%2C%22format%22%3A%22json%22%2C%22ct%22%3A24%2C%22cv%22%3A0%7D%7D";
            HttpUtil.sendUQQRequestGet(url,new okhttp3.Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String vkey = JsonUtil.getSongToken(response);
                    if("".equals(vkey)){
                        lastSong();
                    }else {
                        changeSong(song,vkey);
                    }
                }
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    HttpUtil.failed(MusicService.this,"网络请求失败");
                }
            },null);
        }

        @Override
        public void seekTo(int position) {
            int seekToTime = position*duration*10;
            mediaPlayer.seekTo(seekToTime);
        }

        public void songItemClick(final int index, final Song song, final List<Song> songs){
            //本地歌曲点击
            if(song.getLocal()==1){
                setSongs(songs);
                setCurrentIndex(index);
                changeSong(song,null);
                return;
            }
            //如果当前点击歌曲不一致
            if(!song.getId().equals(getSong().getId())){
                //final String url = "https://c.y.qq.com/base/fcgi-bin/fcg_music_express_mobile3.fcg";
                final String url = "https://u.y.qq.com/cgi-bin/musicu.fcg?format=json&data=%7B%22req_0%22%3A%7B%22module%22%3A%22vkey.GetVkeyServer%22%2C%22method%22%3A%22CgiGetVkey%22%2C%22param%22%3A%7B%22guid%22%3A%22358840384%22%2C%22songmid%22%3A%5B%22" +
                        song.getMid() +
                        "%22%5D%2C%22songtype%22%3A%5B0%5D%2C%22uin%22%3A%221443481947%22%2C%22loginflag%22%3A1%2C%22platform%22%3A%2220%22%7D%7D%2C%22comm%22%3A%7B%22uin%22%3A%2218585073516%22%2C%22format%22%3A%22json%22%2C%22ct%22%3A24%2C%22cv%22%3A0%7D%7D";
               /* Map<String,String> map = new ArrayMap<>();
                map.put("filename","C400"+song.getMid()+".m4a");
                map.put("songmid",song.getMid());
                map.put("guid","1023110828");
                map.put("cid","205361747");
                map.put("platform","yqq");
                map.put("format","json205361747");*/
                HttpUtil.sendUQQRequestGet(url,new okhttp3.Callback() {
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String vkey = JsonUtil.getSongToken(response);
                        if("".equals(vkey)){
                            HttpUtil.failed(MusicService.this,"该歌曲暂时不能播放哦~~嘤嘤嘤");
                        }else {
                            setSongs(songs);
                            setCurrentIndex(index);
                            changeSong(song,vkey);
                        }
                    }
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        HttpUtil.failed(MusicService.this,"网络请求失败");
                    }
                },null);

            }


        }

        //获取歌词
        public void initLyric(){
            songLyric = new ArrayList<>();
            timeLyric = new ArrayList<>();
            String url = "https://c.y.qq.com/lyric/fcgi-bin/fcg_query_lyric_new.fcg";
            Map<String,String> map = new ArrayMap<>();
            map.put("nobase64","1");
            map.put("musicid",song.getId());
            map.put("format","json");
            HttpUtil.sendQQRequestGet(url,new okhttp3.Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response){
                    try {
                        String data = response.body().string();
                        //匹配时间的正则表达式[00:00.00
                        String reg = "^\\[\\d{2}:\\d{2}(\\.||:)\\d{2}";
                        JSONObject jsonObject = new JSONObject(data);
                        String lyric = jsonObject.getString("lyric");
                        if(lyric!=null){
                            String[] lyrics = lyric.split("\n");
                            //Log.d(Tag,lyric);
                            for (String item:lyrics) {
                                String[] strs = item.split("]");
                                //处理时间，使之变成浮动数
                                String[] time = strs[0].substring(1).split(":");
                                if(time==null||!Pattern.matches(reg,strs[0])){
                                    //Log.d(Tag,strs[0]);
                                    continue;
                                }
                                int min =Integer.parseInt(time[0])*60;//时间 分
                                float sec = Float.parseFloat(time[1]);//时间 秒
                                float timeSec = Float.valueOf(min+sec);//总时间保留2位小数

                                //处理歌词
                                if(strs.length>1){
                                    String lrc = strs[1];
                                    songLyric.add(lrc);
                                    timeLyric.add(timeSec);
                                    //Log.d("lry", lrc);
                                }else {
                                    continue;
                                }

                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                }
            },map);
        }
        //更新歌词
        public String updateLyric(){

            if(mediaPlayer!=null){
                currentTime = mediaPlayer.getCurrentPosition()/1000;
                int index = 1;
                for(int i=index;i<timeLyric.size();i++){
                    if(timeLyric.get(i)>currentTime){
                        // this.word = this.timeLyric[i]+this.lyric[i-1]+currentTime
                        index = i;
                        return songLyric.get(i-1);
                    }
                }

                if(songLyric.size()>0){
                    return songLyric.get(songLyric.size()-1);
                }
                else if(songLyric.size()==0){
                    return "歌词加载中";
                }
                else {
                    return "聆听纯音乐的声音";
                }
            }else {
                return "";
            }
        }


        public Song getSong() {
            return song;
        }
        public void setSong(Song song) {
            this.song = song;
        }
        public List<Song> getSongs() {
            return songs;
        }
        public void setSongs(List<Song> songs) {
            this.songs = songs;
        }
        public int getCurrentIndex() {
            return currentIndex;
        }
        public void setCurrentIndex(int currentIndex) {
            this.currentIndex = currentIndex;
        }
        public String getImageUrl() {
            return imageUrl;
        }
        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
        public String getSongUrl() {
            return songUrl;
        }
        public void setSongUrl(String songUrl) {
            this.songUrl = songUrl;
        }
        public boolean isIsplay() {
            return isplay;
        }
        public void setIsplay(boolean isplay) {
            this.isplay = isplay;
        }
        public List<String> getSongLyric() {
            return songLyric;
        }
        public void setSongLyric(List<String> songLyric) {
            this.songLyric = songLyric;
        }
        public List<Float> getTimeLyric() {
            return timeLyric;
        }
        public void setTimeLyric(List<Float> timeLyric) {
            this.timeLyric = timeLyric;
        }
        public int getDuration() {
            return duration;
        }
        public void setDuration(int duration) {
            this.duration = duration;
        }
        public int getCurrentTime() {
            return currentTime;
        }
        public void setCurrentTime(int currentTime) {
            this.currentTime = currentTime;
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();
        /*NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationChannel channel = null;
        //创建通道id
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel("MusicService", "MusicService", NotificationManager.IMPORTANCE_DEFAULT);
           *//* channel.enableLights(true); //是否在桌面icon右上角展示小红点
            channel.setLightColor(Color.GREEN); //小红点颜色
            channel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知*//*
            manager.createNotificationChannel(channel);
        }else {
            return;
        }
        //通知布局如果使用自定义布局文件中的话要通过RemoteViews类来实现，
        //RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notify_layout);

        //重点
        Intent intent = new Intent(this,MusicService.class);
        PendingIntent pi = PendingIntent.getActivity(this,0,intent,0);
        Notification notification = new NotificationCompat.Builder(this,"MusicService")
                .setContentTitle("这是一条很重要的通知")
                .setContentText("其实我就不叫嘤嘤嘤，我是嘤嘤怪")
                .setSmallIcon(R.drawable.app_img)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.loading_spinner))
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();
        startForeground(1,notification);
       *//* .setContent(remoteViews)
                .setCustomBigContentView(remoteViews)*//*
        //对于自定义布局文件中的控件通过RemoteViews类的对象进行事件处理
        *//*remoteViews.setOnClickPendingIntent(R.id.notificationButton1, button1PI);
        remoteViews.setOnClickPendingIntent(R.id.notificatinoButton2, button2PI);*//*
        *//*Intent intentClick = new Intent("NOTIFY_CLICK");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);
        remoteViews.setOnClickPendingIntent(R.id.notify_last_song,pendingIntent);*//*
*/
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mMusicBind;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMusicBind = null;
    }
}
