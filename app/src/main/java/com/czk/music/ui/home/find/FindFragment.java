package com.czk.music.ui.home.find;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.czk.music.R;
import com.czk.music.adapter.TopSongListAdapter;
import com.czk.music.bean.TopList;
import com.czk.music.util.HttpUtil;
import com.czk.music.util.JsonUtil;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class FindFragment extends Fragment {
    private View view;
    private Context mContext;
    private RecyclerView recyclerView;
    private final int UPDATE_PLAYLIST = 1;
    private List<TopList> mTopLists;
    private Handler handler = new Handler(){

        public  void  handleMessage(Message msg){
            switch (msg.what){
                case UPDATE_PLAYLIST:
                    //更新playlist
                    LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
                    recyclerView = view.findViewById(R.id.recycler_top_playlist);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(new TopSongListAdapter(mContext,mTopLists));
                    break;
                default:
                    break;
            }
        }
    };
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_find,container,false);
        mContext = view.getContext();
        recyclerView = view.findViewById(R.id.recycler_top_playlist);
        initTopList();
        return view;
    }
    //初始化排行榜
    private void initTopList() {
        String url = "https://c.y.qq.com/v8/fcg-bin/fcg_myqq_toplist.fcg?g_tk=5381&uin=0&format=json&inCharset=utf-8&outCharset=utf-8&notice=0&platform=h5&needNewCode=1&_=1512554796112";
        HttpUtil.sendQQRequestGet(url,new okhttp3.Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                mTopLists = JsonUtil.getToplist(response);
                Message message = new Message();
                message.what = UPDATE_PLAYLIST;
                handler.sendMessage(message);
            }
            @Override
            public void onFailure(Call call, IOException e) {
                HttpUtil.failed(mContext,"网络请求失败");
            }
        },null);
    }
}
