package com.czk.music.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;
/**
 * Created by TWOSIX on 2020/4/7.
 * qq邮箱： 1023110828@qq.com
 * Describe:首页来回滑动的三个页面 --ViewPager的适配器
 */
public class HomeFragmentPagerAdapter extends FragmentPagerAdapter{

    private List<Fragment> mFragments;
    private String[] titles={"我的","音乐馆","发现"};
    public HomeFragmentPagerAdapter(@NonNull FragmentManager fm, List<Fragment> mFragments) {
        super(fm);
        this.mFragments = mFragments;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
