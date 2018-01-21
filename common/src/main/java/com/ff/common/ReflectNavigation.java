package com.ff.common;

import android.content.Context;
import android.content.Intent;

/**
 * Created by Ace on 2017/11/14.
 */

public class ReflectNavigation {

    public static void navigation(Context context, final String destination, final Intent intent){
        if(context == null || destination == null || intent == null){
            throw new NullPointerException("context == null || destination == null || intent == null");
        }
        Class<?> clazz = null;
        try {
            clazz = Class.forName(destination);
            if(clazz != null){
                intent.setClass(context, clazz);
                context.startActivity(intent);
            }
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public static Intent getNavigationIntent(Context context, final String destination){
        if(context == null || destination == null ){
            throw new NullPointerException("context == null || destination == null ");
        }
        Class<?> clazz = null;
        try {
            clazz = Class.forName(destination);
            if(clazz != null){
                Intent intent = new Intent();
                intent.setClass(context, clazz);
                return intent;
            }
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        return null;
    }

}
