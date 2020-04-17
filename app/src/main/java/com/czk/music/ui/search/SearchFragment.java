package com.czk.music.ui.search;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.collection.ArrayMap;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.czk.music.MainActivity;
import com.czk.music.R;
import com.czk.music.adapter.HistorySearchAdapter;
import com.czk.music.adapter.HotSearchAdapter;
import com.czk.music.adapter.SearchSongListAdapter;
import com.czk.music.bean.SearchKey;
import com.czk.music.bean.Song;
import com.czk.music.interfaces.IonItemClick;
import com.czk.music.util.HttpUtil;
import com.czk.music.service.MusicService;
import com.czk.music.util.JsonUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class SearchFragment extends Fragment {
    private Context mContext;
    private View view;

    private RecyclerView recyclerView ;//存放搜索歌曲的recycleview
    private SwipeRefreshLayout refreshLayout;//实现上拉加载的SwipeRefreshLayout
    private LinearLayoutManager linearLayoutManager;//存放搜索歌曲的recycleview的manager
    private SearchSongListAdapter SearchSongAdapter;//存放搜索歌曲的Adapter
    private int VisibleItemIndex;//当前滚动item的索引

    private EditText editText;//搜索框
    private int p=1 ;//分页
    private int n=20 ;//查询的数量
    private String w="" ;//查询歌曲的关键字
    private int curnum=0;//当前查询到的歌曲
    private int totalnum;//当前关键字所能查询到的所有歌曲
    final String url = "https://c.y.qq.com/soso/fcgi-bin/client_search_cp";

    private LinearLayout linearLayout;//存放历史记录的layout
    private List<SearchKey> list;//历史搜索记录
    private HistorySearchAdapter adapter;//历史记录的适配器
    private RecyclerView historyView ;//存放历史记录的recycleview

    private MusicService.MusicBind musicBinder;

    private final int UPDATE_HOTKEY = 1;
    private final int UPDATE_SONGS = 2;
    private final int REFRESH_SONGS = 3;
    private Handler handler = new Handler(){
        public  void  handleMessage(final Message msg){
            switch (msg.what){
                case UPDATE_HOTKEY:
                    //加载热搜词
                    final List<String> hotKeys = (List<String>) msg.obj;
                    RecyclerView hotView = view.findViewById(R.id.search_recycler_view);
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext,3);
                    hotView.setLayoutManager(gridLayoutManager);
                    HotSearchAdapter adapter = new HotSearchAdapter(mContext, hotKeys);
                    adapter.setIonItemClick(new IonItemClick() {
                        @Override
                        public void onClick(int position) {
                            searchSong(hotKeys.get(position));
                        }
                    });
                    hotView.setAdapter(adapter);
                    break;
                //查询歌曲
                case UPDATE_SONGS:
                    linearLayoutManager = new LinearLayoutManager(mContext);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    SearchSongAdapter= new SearchSongListAdapter(mContext,mSongs);
                    SearchSongAdapter.setIonItemClick(new IonItemClick() {
                        @Override
                        public void onClick(int position) {
                            Song song =mSongs.get(position);
                            musicBinder.songItemClick(position,song,mSongs);
                        }
                    });
                    recyclerView.setAdapter(SearchSongAdapter);
                    break;
                //上拉加载更多歌曲
                case REFRESH_SONGS:
                    SearchSongAdapter.addSong((List<Song>)msg.obj);
                    refreshLayout.setRefreshing(false);
                default:
                    break;
            }
        }
    };
    private List<Song> mSongs;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getContext();
        view = inflater.inflate(R.layout.fragment_search,container,false);
        initView();
        initBind();
        initEvent();
        return view;
    }

    private void initView(){
        linearLayout = view.findViewById(R.id.search_history_linear_layout);
        recyclerView = view.findViewById(R.id.search_song_list);
        editText = view.findViewById(R.id.search_edit_text);
        historyView = view.findViewById(R.id.search_user_history_recycler_view);
        refreshLayout = view.findViewById(R.id.search_refresh);
        initHotKey();
        initHistoryKey();
    }
    //从activity获取musicBinder
    private void initBind(){
        MainActivity mainActivity = (MainActivity) getActivity();
        musicBinder = mainActivity.getMusicBinder();
    }
    private void initEvent() {
        //点击返回
        ImageView imageView = view.findViewById(R.id.search_back);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //回退到上一个页面
                getActivity().onBackPressed();
            }
        });
        //回车搜索
        editText.setOnEditorActionListener(new EditText.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //回车键搜索歌词
                    String key= editText.getText().toString();
                    searchSong(key);
                }
                return true;
            }
        });
        //监听文本框变化事件
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if("".equals(s.toString())){
                    //输入框字符为空
                    linearLayout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                }
            }
        });
        //清空历史记录
        TextView cleanView = view.findViewById(R.id.search_clean_history);
        cleanView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.removeAllData();
            }
        });
        //上拉加载
        refreshLayout.setColorSchemeColors(R.attr.colorPrimary);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //处理网络请求等耗时操作,这里我没有实现刷新功能故省略

                //操作处理完成后，通知刷新结束
                refreshLayout.setRefreshing(false);
            }
        });
        //下拉加载
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && VisibleItemIndex + 1 == SearchSongAdapter.getItemCount()) {
                    refreshLayout.setRefreshing(true);
                    refreshSong();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                VisibleItemIndex = linearLayoutManager.findLastVisibleItemPosition();
            }
        });
    }
    //从网络加载热搜词
    private void initHotKey(){

        String url = "https://c.y.qq.com/splcloud/fcgi-bin/gethotkey.fcg";
        HttpUtil.sendQQRequestGet(url,new okhttp3.Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                List<String> list = JsonUtil.getHotKey(response);
                Message message = new Message();
                message.what = UPDATE_HOTKEY;
                message.obj = list;
                handler.sendMessage(message);
            }
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                HttpUtil.failed(mContext,"加载热搜失败");
            }
        },null);
    }
    //根据关键字搜索歌曲
    public void searchSong(String key){
        key = key.trim();
        if(!"".equals(key)&&!key.equals(w)){
            w=key;
            p=1;//重新搜索就将p重置为1
            curnum = 0;//重置查询的歌曲数量
            Map<String,String> map = new ArrayMap<>();
            map.put("format","json");
            map.put("aggr","1");
            map.put("cr","1");
            map.put("flag_qc","0");
            map.put("p",String.valueOf(p));
            map.put("n",String.valueOf(n));
            map.put("w",w);
            HttpUtil.sendQQRequestGet(url,new okhttp3.Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String data = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(data).getJSONObject("data").getJSONObject("song");
                        curnum += jsonObject.getInt("curnum");
                        totalnum = jsonObject.getInt("totalnum");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mSongs = JsonUtil.getSearchSong(data);
                    Message message = new Message();
                    message.what = UPDATE_SONGS;
                    handler.sendMessage(message);

                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    HttpUtil.failed(mContext,"搜索歌曲失败");
                }
            },map);
            recyclerView.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);
            //将搜索框里的文字改为key
            editText.setText(key);
            //将搜索记录添加到历史记录里
            SearchKey searchKey = new SearchKey(key);
            adapter.addData(searchKey);
        }
    }
    //上拉加载歌曲
    public void refreshSong(){
        if(curnum<totalnum) {
            p++;
            Map<String, String> map = new ArrayMap<>();
            map.put("format", "json");
            map.put("aggr", "1");
            map.put("cr", "1");
            map.put("flag_qc", "0");
            map.put("p", String.valueOf(p));
            map.put("n", String.valueOf(n));
            map.put("w", w);
            HttpUtil.sendQQRequestGet(url, new okhttp3.Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String data = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(data).getJSONObject("data").getJSONObject("song");
                        curnum += jsonObject.getInt("curnum");
                        totalnum = jsonObject.getInt("totalnum");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    List<Song> songs = JsonUtil.getSearchSong(data);
                    Message message = new Message();
                    message.what = REFRESH_SONGS;
                    message.obj = songs;
                    handler.sendMessage(message);
                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    HttpUtil.failed(mContext, "搜索歌曲失败");
                }
            }, map);
        }else {
            refreshLayout.setRefreshing(false);
        }
    }
    //从数据库加载历史记录
    public void initHistoryKey(){
        list = LitePal.findAll(SearchKey.class);
        if(list!=null){
            final int size = list.size();
            adapter = new HistorySearchAdapter(mContext,list);
            adapter.setIonItemClick(new IonItemClick() {
                @Override
                public void onClick(int position) {
                    String key = list.get(size-1-position).getKey();
                    searchSong(key);
                }
            });
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            historyView.setLayoutManager(layoutManager);
            historyView.setAdapter(adapter);
        }
    }
      /* //dip转px
    private int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.getSupportActionBar().show();
    }
}
