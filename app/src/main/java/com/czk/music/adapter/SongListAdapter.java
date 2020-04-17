package com.czk.music.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.czk.music.R;
import com.czk.music.bean.Song;
import com.czk.music.interfaces.IonItemClick;

import java.util.List;

/**
 * Created by TWOSIX on 2020/4/7.
 * qq邮箱： 1023110828@qq.com
 * Describe:歌曲列表的适配器
 */
public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.ViewHolder>{
    List<Song> list;
    private Context mContext;
    private IonItemClick mIonItemClick;
    public void setIonItemClick(IonItemClick ionItemClick){
        this.mIonItemClick = ionItemClick;
    }

    public SongListAdapter(@NonNull Context context, List<Song>list) {
        this.mContext = context;
        this.list = list;
    }

    @NonNull
    @Override
    public SongListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SongListAdapter.ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_recycler_song,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull SongListAdapter.ViewHolder holder, final int position) {
        final Song song = list.get(position);
        holder.songName.setText(song.getName());
        holder.songSinger.setText(song.getSinger()+"·"+song.getAlbum());
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MusicUtil.songItemClick(position,song,mContext,list);
                mIonItemClick.onClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends  RecyclerView.ViewHolder{
        private TextView songName;
        private TextView songSinger;
        private ImageView imageView;
        private LinearLayout linearLayout;
        public ViewHolder(View itemView){
            super(itemView);
            songName = itemView.findViewById(R.id.song_name);
            songSinger= itemView.findViewById(R.id.song_singer);
            imageView = itemView.findViewById(R.id.song_more_option);
            linearLayout = itemView.findViewById(R.id.song_layout);
        }
    }
}
