package com.czk.music.util;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import java.util.Map;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpUtil {
    static OkHttpClient client = new OkHttpClient();

    public static void sendQQRequestPost(final String address, okhttp3.Callback callback,Map<String,String> map) {
        FormBody.Builder builder = new  FormBody.Builder();
        if (map!=null) {
            for (Map.Entry<String,String> entry:map.entrySet()) {
                builder.add(entry.getKey(),entry.getValue());
            }
        }
        FormBody body = builder.build();
        Request request = new Request.Builder()
                .url(address)
                .post(body)
                .addHeader("referer","https://c.y.qq.com/")
                .addHeader("host","c.y.qq.com")
                .build();
        client.newCall(request).enqueue(callback);
    }
    public static void sendQQRequestGet(final String address, okhttp3.Callback callback,Map<String,String> map) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(address).newBuilder();
        if (map!=null) {
            for (Map.Entry<String,String> entry:map.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(),entry.getValue());
            }
        }
        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .addHeader("referer","https://c.y.qq.com/")
                .addHeader("host","c.y.qq.com")
                .addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36")
                .build();
        client.newCall(request).enqueue(callback);
    }
    //出错处理
    public static void failed(Context context,String text){
        Looper.prepare();
        Toast.makeText(context,text,Toast.LENGTH_SHORT).show();
        Looper.loop();
    }
}
