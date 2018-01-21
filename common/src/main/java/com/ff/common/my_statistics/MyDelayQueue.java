package com.ff.common.my_statistics;

import com.ff.common.application.ApplicationProxy;

import java.util.LinkedList;

import static com.ff.common.my_statistics.MyStatistics.UNINSTALL;
import static com.ff.common.my_statistics.MyStatistics.catchInstalledOrRemovedApp;

/**
 * Created by fangyufeng on 6/26/2017.
 */

public class MyDelayQueue {

    static LinkedList<DelayedTask> list;

    public static void process(){
        if(!isEmpty()){
            final DelayedTask first = list.getFirst();
            ApplicationProxy.getInstance().getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(!first.canceled){
                        catchInstalledOrRemovedApp(first.pName,UNINSTALL);
                    }
                    synchronized (list){
                        list.remove(first);
                    }
                    process();
                }
            },first.delayTime);
        }
    }

    public static void add(DelayedTask task){
        if(list == null ){
            list = new LinkedList<>();
        }
        list.add(task);
    }

    public static  boolean isEmpty(){
        if(list!=null&&list.size()>0){
            return false;
        }
        return true;
    }

    public static  void cancelTask(String pName){
        if(!isEmpty()){
            synchronized (list){
                for(DelayedTask task : list){
                    if(task.pName.equals(pName)){
                        task.canceled = true;
                    }
                }
            }
        }
    }

    public static class DelayedTask{

        String pName;
        long delayTime;
        public boolean canceled;

        public DelayedTask(String pName,long delayTime) {
            this.pName = pName;
            this.delayTime = delayTime;
        }

    }

}
