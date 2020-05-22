package com.czk.music.ui.home.my;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.czk.music.R;
import com.czk.music.adapter.SongListAdapter;
import com.czk.music.bean.Song;

import org.litepal.LitePal;

import java.util.Collections;
import java.util.List;

/**
*author: TWOSIX
*createTime:2020/4/16
*description:历史听歌记录
**/
public class MyLocalSongFragment extends Fragment {
    private View view;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private List<Song> mSongList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_local_song, container, false);
        initView();
        initLocalSong();
        return view;
    }

    private void initView() {
        mContext = view.getContext();
        mRecyclerView = view.findViewById(R.id.local_song_recycle_view);
    }
    private void initLocalSong(){
        mSongList = LitePal.where("local = 1").find(Song.class);
        if(mSongList !=null){
            //逆序
            Collections.reverse(mSongList);
            SongListAdapter adapter = new SongListAdapter(mContext, mSongList);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setAdapter(adapter);
        }
    }
}
