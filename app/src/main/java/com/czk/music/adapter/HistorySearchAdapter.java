package com.czk.music.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.czk.music.R;
import com.czk.music.bean.SearchKey;
import com.czk.music.interfaces.IonItemClick;

import org.litepal.LitePal;

import java.util.List;

/**
 * Created by TWOSIX on 2020/4/10.
 * qq邮箱： 1023110828@qq.com
 * Describe:历史搜索记录的adapter
 */
public class HistorySearchAdapter extends RecyclerView.Adapter<HistorySearchAdapter.ViewHolder> {
    List<SearchKey> keyList;
    Context mContext;
    private IonItemClick mIonItemClick;

    public HistorySearchAdapter(Context context,List<SearchKey> keyList){
        this.keyList = keyList;
        this.mContext =context;
    }

    public void setIonItemClick(IonItemClick ionItemClick){
        this.mIonItemClick = ionItemClick;
    }
    @NonNull
    @Override
    public HistorySearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HistorySearchAdapter.ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_recycler_history_search,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull HistorySearchAdapter.ViewHolder holder, final int position) {
        final int size = keyList.size();
        final String key = keyList.get(size-1-position).getKey();
        holder.textView.setText(key);
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* //发送广播，根据关键字搜索歌曲
                Intent intent = new Intent("com.czk.music.ui.search.searchFragment.SearchReceiver");
                intent.putExtra("key",key);
                intent.setComponent(new ComponentName("com.czk.music","com.czk.music.ui.search.searchFragment.SearchReceiver"));
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);*/
               mIonItemClick.onClick(position);
            }
        });
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeData(size-1-position,key);
            }
        });
    }

    @Override
    public int getItemCount() {
        return keyList.size();
    }
    //  添加数据
    public void addData(SearchKey searchKey) {
        int size =keyList.size();
        boolean isExist = false;
        for (int i=0;i<size;i++) {
            //如果keylist已经存在关键字
            if(keyList.get(i).getKey().equals(searchKey.getKey())){
                removeData(i,searchKey.getKey());
                keyList.add(searchKey);
                isExist = true;
                break;
            }
        }
        //如果keylist不存在该关键字
        if(!isExist){
            //在list中添加数据，并通知条目加入一条
            keyList.add(size,searchKey);
        }
        searchKey.save();//往数据库里添加
        /*//添加动画
        notifyItemInserted(size);*/
        notifyDataSetChanged();
    }
    //  删除数据
    public void removeData(int position,String key) {
        keyList.remove(position);
        //删除动画
        notifyItemRemoved(position);
        notifyDataSetChanged();
        //数据库里删除
        LitePal.deleteAll(SearchKey.class,"key=?",key);
    }
    //  删除所有数据
    public void removeAllData() {
        keyList.removeAll(keyList);
        //删除动画
        notifyDataSetChanged();
        //数据库里删除
        LitePal.deleteAll(SearchKey.class);
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textView;
        private ImageView imageView;
        public ViewHolder(View itemView){
            super(itemView);
            textView = itemView.findViewById(R.id.history_search_text);
            imageView = itemView.findViewById(R.id.history_search_delete);
        }
    }
}
