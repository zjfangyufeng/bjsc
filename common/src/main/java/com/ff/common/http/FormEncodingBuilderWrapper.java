package com.ff.common.http;

import com.ff.common.ToolUtils;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import java.util.Map;
import java.util.Set;

/**
 * Created by fangyufeng on 2015/12/9.
 */
public class FormEncodingBuilderWrapper {
    FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();

    private FormEncodingBuilderWrapper add(String name, String value) {
        if(!ToolUtils.isNull(name)){
            if(ToolUtils.isNull(value)){
                formEncodingBuilder.add(name, "");
            } else{
                formEncodingBuilder.add(name, value);
            }
        }
        return this;
    }

    public RequestBody build(Map<String,String> map) {
        Set<Map.Entry<String, String>> entries = map.entrySet();
        for(Map.Entry<String, String> entry: entries){
            add(entry.getKey(), entry.getValue());
        }
        return formEncodingBuilder.build();
    }

}
