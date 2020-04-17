package com.czk.music.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.czk.music.R;
import com.czk.music.interfaces.IonItemClick;

import java.util.List;

/**
 * Created by TWOSIX on 2020/4/8.
 * qq邮箱： 1023110828@qq.com
 * Describe:热词的适配器
 */
public class HotSearchAdapter extends RecyclerView.Adapter<HotSearchAdapter.ViewHolder> {
    List<String> hotWord;
    Context mContext;
    private IonItemClick mIonItemClick;

    public HotSearchAdapter(Context context,List<String> hotWord){
        this.hotWord = hotWord;
        this.mContext =context;
    }

    public void setIonItemClick(IonItemClick ionItemClick){
        this.mIonItemClick = ionItemClick;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HotSearchAdapter.ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_search_hot,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.textView.setText(hotWord.get(position));
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIonItemClick.onClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return hotWord.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textView;
        public ViewHolder(View itemView){
            super(itemView);
            textView = itemView.findViewById(R.id.search_text_view);
        }
    }
}
