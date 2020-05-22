package com.czk.music.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class ApplicationUtil extends Application {
    private static Context context;
    //public static int ThemeColor = R.color.colorGreen;
    public static List<Activity> activities = new ArrayList<>();
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        LitePal.initialize(this);
    }
    public static Context getContext() {
        return context ;
    }

    //添加活动
    public static void addActivity(Activity activity) {
        activities.add(activity) ;
    }
    //删除活动
    public static void removeActivity (Activity activity) {
        activities.remove(activity) ;
    }
    //销毁所有活动
    public static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish() ;
            }
        }
    }
}
