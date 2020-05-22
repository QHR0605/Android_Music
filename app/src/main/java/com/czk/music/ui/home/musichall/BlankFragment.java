package com.czk.music.ui.home.musichall;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.fragment.app.Fragment;

import com.czk.music.R;


public class BlankFragment extends Fragment {
    private View view;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_blank, container, false);
        mContext = view.getContext();
        WebView webView = view.findViewById(R.id.web_webView);
        webView.getSettings().setJavaScriptEnabled(true) ;
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(getArguments().getString("url"));

        return view;
    }

}
