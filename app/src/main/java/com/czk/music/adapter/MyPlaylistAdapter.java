package com.czk.music.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.czk.music.R;
import com.czk.music.bean.PlayListSimple;

import java.util.List;
/**
 * Created by TWOSIX on 2020/4/7.
 * qq邮箱： 1023110828@qq.com
 * Describe:我的界面 ---歌单列表的适配器
 */
public class MyPlaylistAdapter extends RecyclerView.Adapter<MyPlaylistAdapter.ViewHolder> {
    private Context mContext;
    private List<PlayListSimple> playListSimples;
    public MyPlaylistAdapter(@NonNull Context context, List<PlayListSimple> playListSimples) {
        this.mContext = context;
        this.playListSimples = playListSimples;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       return new MyPlaylistAdapter.ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_recycler_playlist,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final PlayListSimple playListSimple = playListSimples.get(position);
        holder.textView.setText(playListSimple.getDissname());
        //使用第三方库glide加载图片
        String url = playListSimple.getImgurl();
        Glide.with(mContext)
                .load(Uri.parse(url))
                .centerCrop()
                .placeholder(R.drawable.loading_spinner)
                .error(R.drawable.loading_error)
                .into(holder.imageView);

        //点击跳转到相应的歌单详情页
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("dissid", playListSimple.getDissid());
                Navigation.findNavController(v).navigate(R.id.action_HomeFragment_to_SongListFragment,bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return playListSimples.size();
    }

    class ViewHolder extends  RecyclerView.ViewHolder{
        private TextView textView;
        private ImageView imageView;
        private LinearLayout linearLayout;
        public ViewHolder(View itemView){
            super(itemView);
            textView = itemView.findViewById(R.id.playlist_text);
            imageView = itemView.findViewById(R.id.playlist_iv);
            linearLayout = itemView.findViewById(R.id.playlist_layout);
        }
    }
}
