package com.czk.music.base;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.czk.music.R;
import com.czk.music.util.ApplicationUtil;

/**
 * Created by TWOSIX on 2020/4/12.
 * qq邮箱： 1023110828@qq.com
 * Describe:所有activity的父类
 */
public class BaseActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //打印当前活动名
        Log.d("BaseActivity",getClass().getSimpleName());
        //添加活动
        ApplicationUtil.addActivity(this);
        //设置主题
        SharedPreferences sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
        String ThemeName = sharedPreferences.getString("theme","清风绿");
        changeThemeByName(ThemeName);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy( );
        ApplicationUtil.removeActivity(this);
    }

    public void changeThemeByName(String name){
        switch (name){
            case "清风绿":
                setTheme(R.style.MyTheme);
                break;
            case "宝石蓝":
                setTheme(R.style.MyBlue);
                break;
            case "荣耀黑":
                setTheme(R.style.MyBlack);
                break;
            case "嘤嘤粉":
                setTheme(R.style.MyPink);
                break;
            case "玫瑰红":
                setTheme(R.style.MyRed);
                break;
            case "黄非黄":
                setTheme(R.style.MyYellow);
                break;
            default:
                setTheme(R.style.MyTheme);
                break;
        }
    }
}
