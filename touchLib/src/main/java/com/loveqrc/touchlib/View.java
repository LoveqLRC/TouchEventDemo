package com.loveqrc.touchlib;

import com.loveqrc.touchlib.listener.OnClickListener;
import com.loveqrc.touchlib.listener.OnTouchListener;

public class View {

    private OnTouchListener mOnTouchListener;
    private OnClickListener onClickListener;

    public void setOnTouchListener(OnTouchListener mOnTouchListener) {
        this.mOnTouchListener = mOnTouchListener;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    private int left;
    private int top;
    private int right;
    private int bottom;

    public View(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public boolean isContainer(int x, int y) {

        if (x >= left && x < right && y >= top && y < bottom) {
            return true;
        }
        return false;
    }

    /**
     * view接收事件的入口
     */
    public boolean dispatchTouchEvent(MotionEvent event) {
        boolean result = false;

        //如果设置了OnTouchListener，并且mOnTouchListener.onTouch返回了True，
        //设置Result为True,那么代表事件到这已经消费完成了。
        if (mOnTouchListener != null && mOnTouchListener.onTouch(this, event)) {
            result = true;
        }

        //没有设置OnTouchListener，或者mOnTouchListener.onTouch返回了false时，result为false
        //此时会回调View.onTouchEvent方法
        if(!result&& onTouchEvent(event)){
            result = true;
        }
        return result;
    }

    public boolean onTouchEvent(MotionEvent event) {
        //如果设置了onClickListener，那么返回True代表事件到这已经消费完成了。
        if (onClickListener != null) {
            onClickListener.onClick(this);
            return true;
        }
        return false;
    }
}
