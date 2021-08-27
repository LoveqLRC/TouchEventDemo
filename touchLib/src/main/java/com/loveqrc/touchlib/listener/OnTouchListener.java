package com.loveqrc.touchlib.listener;

import com.loveqrc.touchlib.MotionEvent;
import com.loveqrc.touchlib.View;

public interface OnTouchListener {
    boolean onTouch(View v, MotionEvent event);
}
