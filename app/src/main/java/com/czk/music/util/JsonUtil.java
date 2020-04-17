package com.czk.music.util;

import android.os.Message;
import android.util.Log;

import com.czk.music.bean.PlayListSimple;
import com.czk.music.bean.Song;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;
import okio.BufferedSource;

/**
 * Created by TWOSIX on 2020/4/5.
 * qq邮箱： 1023110828@qq.com
 * Describe:
 */
public class JsonUtil {
    private static String tag = "JsonUtil";
    //获取热搜词
    public static List<String> getHotKey(Response response){

        List<String> list = new ArrayList<>();
        try {
            String data = response.body().string();
            //Log.d("searchFragment",data);
            JSONArray hotKey = new JSONObject(data).getJSONObject("data").getJSONArray("hotkey");
            for(int i = 2;i<10;i++){
                String key = hotKey.getJSONObject(i).getString("k");
                list.add(key);
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return list;
    }
    //获取歌单列表
    public static List<PlayListSimple> getPlaylist(Response response){
        List<PlayListSimple> list = new ArrayList<>();
        try {
            //得到服务器返回的具体内容
            String responseData = response.body().string();
            JSONObject jsonObject = new JSONObject(responseData);
            jsonObject = jsonObject.getJSONObject("data");
            JSONArray playlist = jsonObject.getJSONArray("list");
            for (int i=0;i<playlist.length();i++){
                PlayListSimple playListSimple = new PlayListSimple();
                jsonObject = playlist.getJSONObject(i);
                playListSimple.setDissid(jsonObject.getString("dissid"));
                playListSimple.setDissname(jsonObject.getString("dissname"));
                playListSimple.setImgurl(jsonObject.getString("imgurl"));
                playListSimple.setListennum(jsonObject.getInt("listennum"));
                list.add(playListSimple);
            }
            //Log.d("MyFragment",playlist.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
    //获取歌曲播放口令生成的key
    public static String getSongToken(Response response) throws IOException {
        BufferedSource source = response.body().source();
        byte[] bytes = new byte[128];
        StringBuffer stringBuffer = new StringBuffer();
        int size = 0;
        while ((size = source.read(bytes)) > -1) {
            // byte数组转字符串
            stringBuffer.append(new String(bytes, 0, size));
        }
        String str = stringBuffer.toString();
        Log.d("song",str);
        String vkey="";
        try {
            JSONObject jsonObject = new JSONObject(str);
            vkey = jsonObject.getJSONObject("data").getJSONArray("items").getJSONObject(0).getString("vkey");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("song-vkey",vkey);
        return vkey;
    }
    //将搜索歌曲得到的数据转化为song
    public static List<Song> getSearchSong(String data){
        List<Song> songs= new ArrayList<>();
        try {
            //Log.d("searchFragment",data);
            JSONArray jsonArray = new JSONObject(data).getJSONObject("data").getJSONObject("song").getJSONArray("list");
            for(int i=0;i<jsonArray.length();i++){
                Song song = new Song();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                //Log.d(tag,jsonObject.toString());
                song.setId(jsonObject.getString("songid"));
                song.setMid(jsonObject.getString("songmid"));
                song.setName(jsonObject.getString("songname"));
                song.setAlbum(jsonObject.getString("albumname"));
                song.setAlbumMid(jsonObject.getString("albummid"));
                JSONArray singers = jsonObject.getJSONArray("singer");
                String singername = "";
                for(int j=0;j<singers.length();j++){
                    singername+=singers.getJSONObject(j).getString("name");
                }
                song.setSinger(singername);
                JSONArray grp = jsonObject.getJSONArray("grp");
                //Log.d(tag,grp.toString());
                if(grp.length()>0){
                    List<Song> songothers = new ArrayList<>();
                    for(int j=0;j<grp.length();j++){
                        Song songOther = new Song();
                        JSONObject jsonObject2 = grp.getJSONObject(j);
                        songOther.setId(jsonObject2.getString("songid"));
                        songOther.setMid(jsonObject2.getString("songmid"));
                        songOther.setName(jsonObject2.getString("songname"));
                        songOther.setAlbum(jsonObject2.getString("albumname"));
                        songOther.setAlbumMid(jsonObject2.getString("albummid"));
                        JSONArray singersother = jsonObject2.getJSONArray("singer");
                        String singernameother = "";
                        for(int k=0;k<singersother.length();k++){
                            singernameother+=singersother.getJSONObject(k).getString("name");
                        }
                        songOther.setSinger(singernameother);
                        songothers.add(songOther);
                    }
                    song.setGrp(songothers);
                }
                songs.add(song);
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }

        return songs;
    }
    //初始化歌单里的歌曲
    public static List<Song> getCdSong(Response response){
        List<Song> list = new ArrayList<>();
        //得到服务器返回的具体内容
        try {
            String responseData = response.body().string();
            JSONObject jsonObject = new JSONObject(responseData);
            JSONArray jsonArray = jsonObject.getJSONArray("cdlist");
            jsonObject = jsonArray.getJSONObject(0);
            jsonArray = jsonObject.getJSONArray("songlist");
            //下拉刷新 待做
            Log.d("json",jsonArray.toString());
            for (int i=0;i<jsonArray.length();i++) {
                Song song = new Song();
                jsonObject = jsonArray.getJSONObject(i);
                song.setId(jsonObject.getString("id"));
                song.setName(jsonObject.getString("name"));
                song.setMid(jsonObject.getString("mid"));
                JSONArray singers = jsonObject.getJSONArray("singer");
                String singerName = "";
                for(int j=0;j<singers.length();j++){
                    if(j==singers.length()-1){
                        singerName+=singers.getJSONObject(j).getString("name");
                    }
                    else {
                        singerName=singerName+singers.getJSONObject(j).getString("name")+"&";
                    }
                }
                song.setSinger(singerName);
                JSONObject album = jsonObject.getJSONObject("album");
                song.setAlbum(album.getString("name"));
                song.setAlbumMid(album.getString("mid"));
                list.add(song);
            }
        }catch(JSONException | IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
