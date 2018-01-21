package com.ff.common.http;

import org.json.JSONObject;

/**
 * Created by fangyufeng on 2015/9/17.
 */
public interface OnResult {
    public void onPrepare();
    public void onSuccess(final JSONObject resultObj);
    public void onFail(final String errorMsg);
    public void onFinally();

}
