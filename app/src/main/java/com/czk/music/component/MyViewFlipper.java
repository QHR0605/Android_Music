package com.czk.music.component;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.czk.music.R;

/**
 * Created by TWOSIX on 2020/4/7.
 * qq邮箱： 1023110828@qq.com
 * Describe:实现了轮播图的自定义的ViewFlipper
 */
public class MyViewFlipper extends ViewFlipper {
    private Context mContext;
    private ViewFlipper viewFlipper;
    private final int MIN_MOVE = 100;//滑动效果生效的最小距离
    private int start;//记录滑动的开始位置
    private int currentPosition = 0;//当前轮播图的位置
    private int countView;//轮播图的个数
    private ImageView dotIV;

    private LinearLayout dotLinearLayout;//轮播图的圆点
    public void setDotLinearLayout(LinearLayout dotLinearLayout) {
        this.dotLinearLayout = dotLinearLayout;
        countView = dotLinearLayout.getChildCount();
        dotIV = (ImageView) dotLinearLayout.getChildAt(currentPosition);
    }

    public MyViewFlipper(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        viewFlipper = this;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);//父亲不拦截触摸事件
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void showNext() {
        super.showNext();
        //小圆点改变
        dotIV.setBackgroundResource(R.drawable.dot_unselect);
        currentPosition=(currentPosition+1)%countView;
        dotIV = (ImageView) dotLinearLayout.getChildAt(currentPosition);
        dotIV.setBackgroundResource(R.drawable.dot_select);
    }

    @Override
    public void showPrevious() {
        super.showPrevious();
        //小圆点改变
        countView = this.getChildCount();
        dotIV.setBackgroundResource(R.drawable.dot_unselect);
        currentPosition=(currentPosition-1+countView)%countView;
        dotIV = (ImageView) dotLinearLayout.getChildAt(currentPosition);
        dotIV.setBackgroundResource(R.drawable.dot_select);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int end=0;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                start = (int)event.getX();
                this.stopFlipping();//手指按下的时候，轮播图停止播放
                break;
            case MotionEvent.ACTION_MOVE:
//                end = (int)event.getX();
                break;
            case MotionEvent.ACTION_UP:
                end = (int)event.getX();
                if((start-end)>=MIN_MOVE){
                    this.setInAnimation(mContext,R.anim.right_in);
                    this.setOutAnimation(mContext,R.anim.right_out);
                    this.showNext();
                } else if((end-start)>=MIN_MOVE){
                    this.setInAnimation(mContext,R.anim.left_in);
                    this.setOutAnimation(mContext,R.anim.left_out);
                    Animation animation = new Animation(){};//空的动画，目的是使动画恢复右移动画
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {}
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            viewFlipper.setInAnimation(mContext,R.anim.right_in);
                            viewFlipper.setOutAnimation(mContext,R.anim.right_out);
                        }
                        @Override
                        public void onAnimationRepeat(Animation animation) { }
                    });
                    this.setAnimation(animation);
                    this.showPrevious();
                }else{
                    Toast.makeText(mContext,"你点了我一下，我就假装跳转到其他页面了，啦啦啦",Toast.LENGTH_SHORT).show();
                }
                this.startFlipping();//手指抬起的时候，轮播图开始播放
                break;
        }
        return true;//返回true事件终结
    }


}
