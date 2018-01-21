package com.ff.common.custom_view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by fangyufeng on 2017/5/17.
 */

public class DraggableView extends ImageView {

    public DraggableView(Context context) {
        super(context);
    }

    public DraggableView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DraggableView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    float lastX,lastY;
    boolean isMove;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                lastX= event.getX();
                lastY= event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(event.getX() - lastX) > 10
                        || Math.abs(event.getY() - lastY) > 10) {
                    isMove = true;
                    int x = (int) event.getX()+getLeft()-getMeasuredWidth()/2;
                    int y = (int) event.getY()+getTop()-getMeasuredHeight()/2;
                    int i = x + getMeasuredWidth();
                    int i1 = y + getMeasuredHeight();
                    this.layout(x, y, i, i1);
                }
                break;
            case MotionEvent.ACTION_UP:
                if(isMove){
                    isMove = false;
                }else {
                    performClick();
                }
                break;
        }
        return true;
    }

}
