package com.loveqrc.touchlib;

import java.util.ArrayList;
import java.util.List;

public class ViewGroup extends View {

    public ViewGroup(int left, int top, int right, int bottom) {
        super(left, top, right, bottom);
    }

    List<View> childList = new ArrayList<>();
    private View[] mChildren = new View[0];

    public void addView(View view) {
        if (view == null) {
            return;
        }
        childList.add(view);
        mChildren = (View[]) childList.toArray(new View[childList.size()]);
    }

    private TouchTarget mFirstTouchTarget;


    /**
     * onInterceptTouchEvent 拦截事件
     * @return  true 代表拦截当前事件，那么事件就不会分发给ViewGroup的child View ，会调用自身的 super.dispatchTouchEvent(event)
     *          false 代表不拦截当前事件，不拦截事件，那么在dispatchTouchEvent会遍历child View，寻找能消费事件的child View
     */
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    /**
     * @param event 事件
     * @param child 如果child 不为null，那么事件分发给它，否则，调用调用自身的 super.dispatchTouchEvent(event)
     * @return 是否消费了该事件
     */
    private boolean dispatchTransformedTouchEvent(MotionEvent event, View child) {
        boolean handled = false;
//        当前View消费了
        if (child != null) {
            handled = child.dispatchTouchEvent(event);
        } else {
            handled = super.dispatchTouchEvent(event);
        }

        return handled;
    }


    public boolean dispatchTouchEvent(MotionEvent event) {
        boolean handled = false;
        //是否拦截当前事件
        boolean intercepted = onInterceptTouchEvent(event);
        //触碰的对象
        TouchTarget newTouchTarget = null;
        int actionMasked = event.getActionMasked();

        if (actionMasked != MotionEvent.ACTION_CANCEL && !intercepted) {
            if (actionMasked == MotionEvent.ACTION_DOWN) {
                //ViewGroup child View 数组
                final View[] children = mChildren;
                //倒序遍历，最后的通常是需要处理事件的
                for (int i = children.length - 1; i >= 0; i--) {
                    View child = mChildren[i];
                    //isContainer 方法判断事件是否落在View中
                    if (!child.isContainer(event.getX(), event.getY())) {
                        continue;
                    }
                    //找到可以接收事件的View,把事件分发给他，
                    //如果dispatchTransformedTouchEvent返回了True代表消费了事件
                    if (dispatchTransformedTouchEvent(event, child)) {
                        handled = true;
                        //通过child包装成TouchTarget对象
                        newTouchTarget = addTouchTarget(child);
                        break;
                    }

                }
            }
        }
        //如果TouchTarget为null，那么事件就发就自己处理
        //mFirstTouchTarget == null 在onInterceptTouchEvent返回true时，或没有找到可以消费的child View时成立
        if (mFirstTouchTarget == null) {
            handled = dispatchTransformedTouchEvent(event, null);
        }
        return handled;
    }




    private TouchTarget addTouchTarget(View child) {
        final TouchTarget target = TouchTarget.obtain(child);
        target.next = mFirstTouchTarget;
        mFirstTouchTarget = target;
        return target;
    }

    //    手写RecyClerView   回收池策略·
    private static final class TouchTarget {
        public View child;//当前缓存的View
        //回收池（一个对象）
        private static TouchTarget sRecycleBin;
        private static final Object sRecycleLock = new Object[0];
        public TouchTarget next;
        //        size
        private static int sRecycledCount;

        //        up事件
        public static TouchTarget obtain(View child) {
            TouchTarget target;
            synchronized (sRecycleLock) {
                if (sRecycleBin == null) {
                    target = new TouchTarget();
                } else {
                    target = sRecycleBin;
                }
                sRecycleBin = target.next;
                sRecycledCount--;
                target.next = null;
            }
            target.child = child;
            return target;
        }

        public void recycle() {

            if (child == null) {
                throw new IllegalStateException("已经被回收过了");
            }
            synchronized (sRecycleLock) {

                if (sRecycledCount < 32) {
                    next = sRecycleBin;
                    sRecycleBin = this;
                    sRecycledCount += 1;
                }
            }
        }
    }

}
