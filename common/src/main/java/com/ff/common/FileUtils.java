package com.ff.common;

/**
 * Created by fangyufeng on 2016/8/24.
 */
public class FileUtils {

    public static String getFileNameFromPath(String path,boolean withSuffix){
        String result = "default_file_name";
        try {
            String[] split = path.split("/");
            result = split[split.length-1];
            if(!withSuffix){
                int i = result.lastIndexOf(".");
                if(i>0)
                    result = result.substring(0,i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getFileSuffix(String path){
        String result = "";
        try {
            String[] split = path.split("\\.");
            if(split.length>1)
                result = split[split.length-1];
        } catch (Exception e) {
        }
        return result;
    }

}
