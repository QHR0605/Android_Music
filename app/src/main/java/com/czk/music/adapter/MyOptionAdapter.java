package com.czk.music.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.czk.music.R;
import com.czk.music.bean.ImageText;

import java.util.List;
/**
 * Created by TWOSIX on 2020/4/7.
 * qq邮箱： 1023110828@qq.com
 * Describe:我的界面里6个操作
 */
public class MyOptionAdapter extends RecyclerView.Adapter<MyOptionAdapter.ViewHolder> {
    private Context mContext;
    private List<ImageText> imageTexts;
    public MyOptionAdapter(@NonNull Context context, List<ImageText> imageTexts) {
        this.mContext = context;
        this.imageTexts = imageTexts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyOptionAdapter.ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_recycler_view_home_option,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        ImageText imageText = imageTexts.get(position);
        holder.textView.setText(imageText.getTitle());
        holder.imageView.setBackgroundResource(imageText.getImageId());
        holder.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        //点击事件
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (position){
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        Navigation.findNavController(v).navigate(R.id.action_nav_home_to_nav_history_song);
                        break;
                    case 3:
                        Navigation.findNavController(v).navigate(R.id.action_nav_home_to_nav_like_song);
                        break;
                    case 4:
                        break;
                    case 5:
                        break;

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageTexts.size();
    }


    class ViewHolder extends  RecyclerView.ViewHolder{
        private TextView textView;
        private ImageView imageView;
        private LinearLayout linearLayout;
        public ViewHolder(View itemView){
            super(itemView);
            linearLayout = itemView.findViewById(R.id.option_layout);
            textView = itemView.findViewById(R.id.option_text);
            imageView = itemView.findViewById(R.id.option_icon);
        }
    }
}
