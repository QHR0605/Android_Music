package com.czk.music.ui.theme;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.czk.music.MainActivity;
import com.czk.music.R;

public class themeFragment extends Fragment {
    private Activity activity;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_theme, container, false);
        init();
        return view;
    }

    private void init() {
        activity = (MainActivity) getActivity();

    }

}
