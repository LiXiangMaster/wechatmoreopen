package com.shenqi.duokai.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by wangms on 16/3/21.
 */
public class DoubleTapRelativeLayout extends RelativeLayout{
    private GestureDetector mDetector;
    private  OnDoubleTapListener mListener;

    public DoubleTapRelativeLayout(Context context) {
        super(context);
    }

    public DoubleTapRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (mDetector == null)
            mDetector = new GestureDetector(getContext(), new GestureListener());

        return mDetector.onTouchEvent(e);
    }

    public void setDoubleTapListener(OnDoubleTapListener listener) {
        mListener = listener;
    }

    public interface OnDoubleTapListener {
        void onDoubleTapped(View v);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            //float x = e.getX();
            //float y = e.getY();

            if (mListener != null)
                mListener.onDoubleTapped(DoubleTapRelativeLayout.this);

            return true;
        }
    }
}
