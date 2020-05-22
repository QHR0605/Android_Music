package com.czk.music.ui.scansong;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.czk.music.R;
import com.czk.music.bean.Song;

public class ScanSongFragment extends Fragment {

    private Button btn;
    private Context mContext;
    private View mView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_menu_scansong, container, false);
        initView();
        return mView;
    }
    private void initView(){
        mContext = mView.getContext();

        btn = mView.findViewById(R.id.scan_song_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanSong();
            }
        });
    }
    private void ScanSong(){
        // 媒体库查询语句（写一个工具类MusicUtils）
        Cursor cursor = mContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                null, MediaStore.Audio.AudioColumns.IS_MUSIC);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Song song = new Song();
                song.setDownloadUrl(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                //歌手名
                String singerName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                //歌名
                String songName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)).split("-")[0];


                song.setSinger(singerName);
                song.setName(songName);
                song.setAlbum(singerName);
                song.setLocal(1);
                song.save();
            }
            // 释放资源
            cursor.close();
        }
        Toast.makeText(mContext,"扫描歌曲成功",Toast.LENGTH_SHORT).show();
    }
}
