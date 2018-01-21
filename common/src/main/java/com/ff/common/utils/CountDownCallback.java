package com.ff.common.utils;

/**
 * Created by Ace on 2017/11/14.
 */

public interface CountDownCallback{
    void onTick(int secUntilFinished);
    void onFinish();
}
