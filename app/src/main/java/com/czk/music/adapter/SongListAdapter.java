package com.czk.music.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
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
import com.czk.music.util.MusicUtil;

import java.util.List;

/**
 * Created by TWOSIX on 2020/4/7.
 * qq邮箱： 1023110828@qq.com
 * Describe:歌曲列表的适配器
 */
public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.ViewHolder>{
    List<Song> list;
    private Context mContext;
    private int currentIndex = -1;//当前点击歌曲的index
    private final TypedValue mTypedValue;//获取主题颜色
    //private int themeColor = R.color.colorGreen;
    /*private IonItemClick mIonItemClick;
    public void setIonItemClick(IonItemClick ionItemClick){
        this.mIonItemClick = ionItemClick;
    }*/

    public SongListAdapter(@NonNull Context context, List<Song>list) {
        this.mContext = context;
        this.list = list;
        mTypedValue = new TypedValue();
        mContext.getTheme().resolveAttribute(R.attr.colorPrimary, mTypedValue, true);
    }

    @NonNull
    @Override
    public SongListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SongListAdapter.ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_recycler_song,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final SongListAdapter.ViewHolder holder, final int position) {
        final Song song = list.get(position);
        holder.songName.setText(song.getName());
        holder.songSinger.setText(song.getSinger()+" · "+song.getAlbum());
        if(song.getDownload()==1||song.getLocal()==1){
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.ic_download_success);
            drawable.setBounds( 0, 0, drawable.getMinimumWidth(),drawable.getMinimumHeight());
            holder.songSinger.setCompoundDrawables(drawable,null,null,null);
        }else{
            holder.songSinger.setCompoundDrawables(null,null,null,null);
        }
        if(currentIndex == position){
            holder.songName.setTextColor(mTypedValue.data);
            holder.songSinger.setTextColor(mTypedValue.data);
        }else {
            holder.songName.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
            holder.songSinger.setTextColor(mContext.getResources().getColor(R.color.colorGray));
        }


        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicUtil.musicBind.songItemClick(position,song,list);
                currentIndex = position;
                notifyDataSetChanged();
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
