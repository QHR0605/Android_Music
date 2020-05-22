package com.czk.music.ui.home.musichall;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.collection.ArrayMap;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.czk.music.R;
import com.czk.music.adapter.MusicHallPlaylistAdapter;
import com.czk.music.bean.PlayListSimple;
import com.czk.music.component.MyViewFlipper;
import com.czk.music.util.HttpUtil;
import com.czk.music.util.JsonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class MusicHallFragment extends Fragment {
    private View view;
    private Context mContext;
    private RecyclerView recyclerView;

    private List<String> linkUrl = new ArrayList<>();
    private List<String> picUrl = new ArrayList<>();

    private List<PlayListSimple> mPlayList;
    private List<PlayListSimple> mList = new ArrayList<>();
    private int current = 0;//当前页
    private int TotalPage ;//总页数
    private Button mBtn;
    private MusicHallPlaylistAdapter mMusicHallPlaylistAdapter;
    //private MyViewFlipper myViewFlipper;
    //异步消息处理
    private final int INIT_VIEW = 1;
    private final int UPDATE_PLAYLIST = 2;
    private Handler handler = new Handler(){

        public  void  handleMessage(Message msg){
            switch (msg.what){
                case INIT_VIEW:
                    //初始化轮播图
                    MyViewFlipper myViewFlipper = view.findViewById(R.id.my_view_flipper);
                    myViewFlipper.setFlipInterval(5000);
                    myViewFlipper.setInAnimation(mContext,R.anim.right_in);
                    myViewFlipper.setOutAnimation(mContext,R.anim.right_out);
                    myViewFlipper.startFlipping();//轮播图自动播放
                    //初始化轮播图的圆点
                    LinearLayout linearLayout = view.findViewById(R.id.dot_linear_layout);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(10,0,0,0);//图片间距
                    for(int i=0;i<picUrl.size();i++){
                        ImageView view = new ImageView(mContext);
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
                    //添加轮播图子视图
                    for(int i=0;i<picUrl.size();i++){
                        ImageView iv = new ImageView(mContext);
                        iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        Glide.with(mContext)
                                .load(Uri.parse(picUrl.get(i)))
                                .centerCrop()
                                .placeholder(R.drawable.loading_spinner)
                                .error(R.drawable.loading_error)
                                .into(iv);
                        final String url = linkUrl.get(i);
                        iv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Bundle bundle = new Bundle();
                                bundle.putString("url",url);
                                Navigation.findNavController(v).navigate(R.id.action_nav_home_to_nav_blank_fragment,bundle);
                            }
                        });
                        myViewFlipper.addView(iv);
                    }
                    break;
                case UPDATE_PLAYLIST:
                    //更新playlist
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext,3);
                    recyclerView = view.findViewById(R.id.recycler_music_playlist);
                    recyclerView.setLayoutManager(gridLayoutManager);
                    recyclerView.setAdapter(mMusicHallPlaylistAdapter);
                    break;
                default:
                    break;
            }
        }
    };



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_music_hall, container, false);
        initView();
        initPageView();
        initPlayList();
        return view;
    }

    private void initView() {
        mContext = view.getContext();
        mMusicHallPlaylistAdapter = new MusicHallPlaylistAdapter(mContext, mList);
        mBtn = view.findViewById(R.id.music_hall_btn);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current++;
                int page = (current)%TotalPage;
                changePlayList(page);
            }
        });



    }
    //初始化轮播图
    private void initPageView(){
        String url = "https://c.y.qq.com/musichall/fcgi-bin/fcg_yqqhomepagerecommend.fcg?g_tk=701075963&uin=0&format=json&inCharset=utf-8&outCharset=utf-8&notice=0&platform=h5&needNewCode=1&_=1512548815061";
        if(linkUrl.size()>0){
            Message message = new Message();
            message.what = INIT_VIEW;
            handler.sendMessage(message);
            return;
        }
        //获取轮播图数据
        HttpUtil.sendUQQRequestGet(url,new okhttp3.Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String data = response.body().string();
                    JSONArray jsonArray = new JSONObject(data).getJSONObject("data").getJSONArray("slider");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        linkUrl.add(jsonObject.getString("linkUrl"));
                        picUrl.add(jsonObject.getString("picUrl"));
                    }
                    Message message = new Message();
                    message.what = INIT_VIEW;
                    handler.sendMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onFailure(Call call, IOException e) {
                HttpUtil.failed(mContext,"网络请求失败");
            }
        },null);
    }

   /* private void initRecommendSong(){
        LinearLayout recommendSongLayout = view.findViewById(R.id.playlist_layout);
        ImageView imageView = view.findViewById(R.id.playlist_iv);
        TextView textView1 = view.findViewById(R.id.playlist_song1);
        TextView textView2 = view.findViewById(R.id.playlist_song2);
        TextView textView3 = view.findViewById(R.id.playlist_song3);

        String url = "https://c.y.qq.com/v8/fcg-bin/fcg_v8_toplist_cp.fcg?g_tk=5381&uin=0&format=json&inCharset=utf-8&outCharset=utf-8¬ice=0&platform=h5&needNewCode=1&tpl=3&page=detail&type=top&topid=36&_=1520777874472";
        //获取歌单数据
        HttpUtil.sendQQRequestGet(url,new okhttp3.Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                List<Song> songs = JsonUtil.getRecommendSong(response);

            }
            @Override
            public void onFailure(Call call, IOException e) {
                HttpUtil.failed(mContext,"网络请求失败");
            }
        },null);
    }*/
    //初始化推荐歌单
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
                mPlayList = JsonUtil.getPlaylist(response);
                TotalPage = mPlayList.size()/6;
                changePlayList(0);
                Message message = new Message();
                message.what = UPDATE_PLAYLIST;
                handler.sendMessage(message);
            }
            @Override
            public void onFailure(Call call, IOException e) {
                HttpUtil.failed(mContext,"网络请求失败");
            }
        },map);
    }
    private void changePlayList(int page){
        mList.clear();
        for(int i = page * 6;i<page*6+6;i++){
            mList.add(mPlayList.get(i));
        }
        mMusicHallPlaylistAdapter.changePlayListSimples(mList);
    }
}
