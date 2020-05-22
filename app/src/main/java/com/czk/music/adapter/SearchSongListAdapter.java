package com.czk.music.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.czk.music.R;
import com.czk.music.bean.Song;
import com.czk.music.util.MusicUtil;

import java.util.List;

/**
 * Created by TWOSIX on 2020/4/7.
 * qq邮箱： 1023110828@qq.com
 * Describe:搜索得到的歌曲 适配器
 */
public class SearchSongListAdapter extends RecyclerView.Adapter<SearchSongListAdapter.ViewHolder>{
    List<Song> list;
    private Context mContext;
    Drawable top;//上箭头
    Drawable down;//下箭头


    public SearchSongListAdapter(@NonNull Context context, List<Song>list) {
        this.mContext = context;
        this.list = list;
        top= mContext.getResources().getDrawable(R.drawable.ic_arrow_top);//上箭头
        down= mContext.getResources().getDrawable(R.drawable.ic_arrow_down);//下箭头
        }

    @NonNull
    @Override
    public SearchSongListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchSongListAdapter.ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_recycler_search_song,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final SearchSongListAdapter.ViewHolder holder, final int position) {
        final Song song = list.get(position);
        holder.songName.setText(song.getName());
        holder.songSinger.setText(song.getSinger()+"·"+song.getAlbum());
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicUtil.musicBind.songItemClick(position,song,list);
            }
        });
        //如果该歌曲还有其他版本的话
        if(song.getGrp()!=null){
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            holder.recyclerView.setLayoutManager(layoutManager);
            holder.recyclerView.setAdapter(new SongListAdapter(mContext,song.getGrp()));

            //将更多版本显示出来
            holder.moreSong.setVisibility(View.VISIBLE);
            holder.moreSong.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(holder.recyclerView.getVisibility()==View.VISIBLE){
                        holder.recyclerView.setVisibility(View.GONE);
                        holder.moreSong.setText(R.string.search_song_more);
                        holder.moreSong.setCompoundDrawablesWithIntrinsicBounds(null, null , down, null);
                    }else {
                        holder.recyclerView.setVisibility(View.VISIBLE);
                        holder.moreSong.setText(R.string.search_song_more_back);
                        holder.moreSong.setCompoundDrawablesWithIntrinsicBounds(null, null , top, null);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public void addSong(List<Song> songs){
        for (Song item: songs) {
            list.add(item);
        }
        notifyDataSetChanged();
    }
    class ViewHolder extends  RecyclerView.ViewHolder{
        private TextView songName;
        private TextView songSinger;
        private ImageView imageView;
        private LinearLayout linearLayout;
        private TextView moreSong;//更多版本
        private RecyclerView recyclerView;//更多版本里的歌曲列表
        private View view;
        public ViewHolder(View itemView){
            super(itemView);
            songName = itemView.findViewById(R.id.song_name);
            songSinger= itemView.findViewById(R.id.song_singer);
            imageView = itemView.findViewById(R.id.song_more_option);
            linearLayout = itemView.findViewById(R.id.song_layout);
            moreSong = itemView.findViewById(R.id.search_song_more);
            recyclerView = itemView.findViewById(R.id.search_song_more_recycle_view);
            view = itemView;
        }
    }
}
