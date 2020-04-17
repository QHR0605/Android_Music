package com.czk.music.ui.home.my;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.czk.music.R;
import com.czk.music.adapter.MyOptionAdapter;
import com.czk.music.adapter.MyPlaylistAdapter;
import com.czk.music.bean.ImageText;
import com.czk.music.bean.PlayListSimple;
import com.czk.music.component.MyViewFlipper;
import com.czk.music.util.HttpUtil;
import com.czk.music.util.JsonUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class MyFragment extends Fragment {
    private View view;
    private Context context;
    //轮播图
    private int[] resIds = new int[]{R.drawable.a, R.drawable.b,R.drawable.c,R.drawable.d,R.drawable.e,R.drawable.f};

    //异步消息处理
    private final int UPDATE_PLAYLIST = 1;
    private Handler handler = new Handler(){

        public  void  handleMessage(Message msg){
            switch (msg.what){
                case UPDATE_PLAYLIST:
                    //更新playlist
                    LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                    RecyclerView recyclerView = view.findViewById(R.id.recycler_my_playlist);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(new MyPlaylistAdapter(context,(List<PlayListSimple>)msg.obj));
                    break;
                default:
                    break;
            }
        }
    };
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my, container, false);
        context = view.getContext();
        initView();
        return view;
    }

    //初始化各种View
    public void initView(){
        //初始化轮播图
        MyViewFlipper myViewFlipper = view.findViewById(R.id.my_view_flipper);
        myViewFlipper.setFlipInterval(5000);
        myViewFlipper.setInAnimation(context,R.anim.right_in);
        myViewFlipper.setOutAnimation(context,R.anim.right_out);
        myViewFlipper.startFlipping();//轮播图自动播放
        //添加轮播图子视图
        for(int i=0;i<resIds.length;i++){
            ImageView iv = new ImageView(context);
            iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
            iv.setBackgroundResource(resIds[i]);
            myViewFlipper.addView(iv);
        }
        //初始化轮播图的圆点
        LinearLayout linearLayout = view.findViewById(R.id.dot_linear_layout);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10,0,0,0);//图片间距
        for(int i=0;i<resIds.length;i++){
            ImageView view = new ImageView(context);
            view.setScaleType(ImageView.ScaleType.FIT_CENTER);
            if(i==0){
                view.setBackgroundResource(R.drawable.dot_select);
                linearLayout.addView(view);
            }else{
                view.setBackgroundResource(R.drawable.dot_unselect);
                linearLayout.addView(view,lp);
            }
        }
        myViewFlipper.setDotLinearLayout(linearLayout);//给轮播图添加圆点

        //初始化首页的6大图标
        List<ImageText> imageTexts = new ArrayList<>();
        int[] imageId = {R.drawable.ic_music,R.drawable.ic_song_download,R.drawable.ic_lately,R.drawable.ic_song_like,R.drawable.ic_listen,R.drawable.ic_zk};
        String[] text = {"本地音乐","下载音乐","最近播放","我喜欢","听歌识曲","ZK天地"};
        for(int i=0;i<text.length;i++){
            ImageText imageText = new ImageText(imageId[i],text[i]);
            imageTexts.add(imageText);
        }
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context,3);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_option);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(new MyOptionAdapter(context, imageTexts));

        //初始化playlist(歌单)
        initPlayList();
    }

    private void initPlayList(){
        String url = "https://c.y.qq.com/splcloud/fcgi-bin/fcg_get_diss_by_tag.fcg";
        //设置查询参数
        Map<String,String> map = new ArrayMap<>();
        map.put("categoryId","10000000");
        map.put("format","json");
        map.put("ein","20");
        //获取歌单数据
        HttpUtil.sendQQRequestGet(url,new okhttp3.Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                List<PlayListSimple> list = JsonUtil.getPlaylist(response);
                Message message = new Message();
                message.what = UPDATE_PLAYLIST;
                message.obj = list;
                handler.sendMessage(message);
            }
            @Override
            public void onFailure(Call call, IOException e) {
                HttpUtil.failed(context,"网络请求失败");
            }
        },map);
    }
}