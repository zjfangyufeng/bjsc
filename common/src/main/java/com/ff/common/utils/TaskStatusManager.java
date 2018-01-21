package com.ff.common.utils;

import android.content.Context;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.util.LruCache;
import android.text.TextUtils;

import com.ff.common.ToolUtils;
import com.ff.common.application.ApplicationProxy;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

public class TaskStatusManager {
    private boolean initialized = false;
    private Context context;
    private ExecutorService mExecutor;

    // SUBMIT.
    private final static String SUBMIT_STATUS = "question_task_can_submit_status";
    private Object mutex = new Object();

    public interface TaskStatus {
        int INVALID     =  -1;
        int DOWNLOADING =   0;
        int DOWNLOADED  =   1;
        int INSTALLED   =   1;
        int FINISHED    =   3;
    }

    // INSTALLED.
    private final static String INSTALL_TASK_STATUS = "install_task_status";
    private Object mm = new Object();
    private final static int MAX_SIZE = 100;
    private final static int STEP = 1;
    private LruCache<String, Integer> mStatusLruCache = new LruCache<>(MAX_SIZE);

    // COMPLETED.
    private static final String COMPLETED_TASKS = "completed_tasks";
    private Map<String, Integer> mCompletedMap = new HashMap<>();


    // UNINSTALLED.
    private final static String UNINSTALLED_APP_SET = "uninstalled_app_set";
    private Set mUninstallAppSet = Collections.newSetFromMap(new HashMap());
    private Object mmm = new Object();

    private static TaskStatusManager INSTANCE;
    public static TaskStatusManager getInstance() {
        if (null == INSTANCE) {
            synchronized (TaskStatusManager.class) {
                if (null == INSTANCE) {
                    INSTANCE = new TaskStatusManager();
                }
            }
        }
        return INSTANCE;
    }

    public void init(final Context context, final ExecutorService executor) {
        if (initialized) {
            return;
        }
        initialized = true;
        this.context = context.getApplicationContext();
        this.mExecutor = executor;
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (mCompletedMap) {
                    String string = PreferenceManager.getDefaultSharedPreferences(TaskStatusManager.this.context).getString(COMPLETED_TASKS, null);
                    if (!TextUtils.isEmpty(string)) {
                        try {
                            JSONObject json = new JSONObject(string);
                            for (Iterator<String> keys = json.keys(); keys.hasNext(); ) {
                                String key = keys.next();
                                mCompletedMap.put(key, json.getInt(key));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    public boolean getSubmitStatus(String taskId) {
        boolean canSubmit = false;
        String string = PreferenceManager.getDefaultSharedPreferences(ApplicationProxy.getInstance().getContext()).getString(SUBMIT_STATUS, null);
        if (TextUtils.isEmpty(string)) return canSubmit;
        try {
            JSONObject json = new JSONObject(string);
            canSubmit = json.optBoolean(taskId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return canSubmit;
    }

    public void updateSubmitStatus(final String taskId, final boolean canSubmit) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (mutex) {
                    String string = PreferenceManager.getDefaultSharedPreferences(ApplicationProxy.getInstance().getContext()).getString(SUBMIT_STATUS, null);
                    try {
                        JSONObject json = TextUtils.isEmpty(string) ? new JSONObject() : new JSONObject(string);
                        json.put(taskId, canSubmit);
                        PreferenceManager.getDefaultSharedPreferences(ApplicationProxy.getInstance().getContext()).edit().putString(SUBMIT_STATUS, json.toString()).commit();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public Integer getDay(String taskId) {
        Assert.assertTrue(initialized);
        synchronized (mCompletedMap) {
            return mCompletedMap.get(taskId);
        }
    }

    /** When load lock screen data successfully via network, clear it*/
    public void clearCompletedList() {
        Assert.assertTrue(initialized);
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (mCompletedMap) {
                    try {
                        mCompletedMap.clear();
                        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(COMPLETED_TASKS, null).commit();
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void add(final String taskId, final int day) {
        Assert.assertTrue(initialized);
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (mCompletedMap) {
                    String string = PreferenceManager.getDefaultSharedPreferences(context).getString(COMPLETED_TASKS, null);
                    try {
                        JSONObject json = TextUtils.isEmpty(string) ? new JSONObject() : new JSONObject(string);
                        json.put(taskId, day);
                        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(COMPLETED_TASKS, json.toString()).commit();
                        mCompletedMap.put(taskId, day);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private final static int CONFUSE = 0; // Can't confirm it's source.
    private final static int OURS    = 1;
    private final static int OTHERS  = 2;
    private int source(String taskId) {
        Assert.assertTrue(initialized);
        int status = get(taskId);
        switch (status) {
            case TaskStatus.INSTALLED:
                return OURS;
            case TaskStatus.DOWNLOADING:
                return OTHERS;
            default:
                return CONFUSE;
        }
    }

    private static String encode(String taskId, String status) {
        String original = status + "," + taskId;
        return ToolUtils.byteArrayToHex(original.getBytes());
    }

    private static int decode(String data) {
        return Integer.valueOf(ToolUtils.hexToString(data).substring(0, 1));
    }

    public int get(String taskId) {
        Assert.assertTrue(initialized);
        synchronized (mm) {
            int status = TaskStatus.INVALID;
            Integer obj = mStatusLruCache.get(taskId);
            if (obj != null) return obj;
            String string = PreferenceManager.getDefaultSharedPreferences(context).getString(INSTALL_TASK_STATUS, null);
            if (TextUtils.isEmpty(string)) return status;
            try {
                JSONObject json = new JSONObject(string);
                String hexString = json.getString(taskId);
                status = decode(hexString);
                mStatusLruCache.put(taskId, status); // Add it into LruCache.
            } catch (JSONException ignored) {}
            return status;
        }
    }

    public void store(final String taskId, final String status) {
        Assert.assertTrue(initialized);
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (mm) {
                    String string = PreferenceManager.getDefaultSharedPreferences(context).getString(INSTALL_TASK_STATUS, null);
                    try {
                        JSONObject json = new JSONObject();
                        int original;
                        if (!TextUtils.isEmpty(string)) {
                            json = new JSONObject(string);
                            try {
                                original = decode(json.getString(taskId));
                                int upgrade = Integer.valueOf(status);
                                if (upgrade <= original) return; /** Never Downgrade */
                                if (upgrade > original && (upgrade - original) != STEP) {
                                    return;
                                }
                            } catch (JSONException notExist) {
                                if (Integer.valueOf(status) != TaskStatus.DOWNLOADING) {
                                    return;
                                }
                            }
                        }
                        json.put(taskId, encode(taskId, status));
                        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(INSTALL_TASK_STATUS, json.toString()).commit();
                        mStatusLruCache.put(taskId, Integer.valueOf(status));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    // Called when app is uninstalled.
    public void delete(final String taskId) {
        Assert.assertTrue(initialized);
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (mm) {
                    String string = PreferenceManager.getDefaultSharedPreferences(context).getString(INSTALL_TASK_STATUS, null);
                    try {
                        JSONObject json;
                        if (TextUtils.isEmpty(string))  return;
                        json = new JSONObject(string);
                        Object obj = json.remove(taskId);
                        if (obj != null) {
                            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(INSTALL_TASK_STATUS, json.toString()).commit();
                            mStatusLruCache.remove(taskId);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    public void addUninstallPackageName(final String packageName) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (mmm) {
                    String string = PreferenceManager.getDefaultSharedPreferences(context).getString(UNINSTALLED_APP_SET, null);
                    try {
                        JSONObject json;
                        json = TextUtils.isEmpty(string) ? new JSONObject() : new JSONObject(string);
                        json.put(ToolUtils.byteArrayToHex(packageName.getBytes()), true);
                        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(UNINSTALLED_APP_SET, json.toString()).commit();
                        mUninstallAppSet.add(packageName);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    public boolean exists(final String packageName) {
        if (mUninstallAppSet.contains(packageName)) {
            return true;
        }
        String string = PreferenceManager.getDefaultSharedPreferences(context).getString(UNINSTALLED_APP_SET, null);
        try {
            JSONObject json;
            if (TextUtils.isEmpty(string)) return false;
            json = new JSONObject(string);
            json.getBoolean(ToolUtils.byteArrayToHex(packageName.getBytes()));
            mUninstallAppSet.add(packageName);
           return true;
        } catch (JSONException notExist) {
            return false;
        }
    }

    public boolean filter(boolean ours, int day, String taskId, String packageName, String filePath) {
        if (exists(packageName)) {
            return true;
        }
        Integer mDay = getDay(taskId);
        if (mDay != null && day != -1 && day == mDay) {
            return true;
        }
        if (ours) {
//            int source = source(taskId);
//            if ((CONFUSE == source || OURS == source) && !ToolUtils.isApplicationInstalledByPackageName(packageName)) {
//                addUninstallPackageName(packageName);
//                MyStatistics.catchRemovedApp(packageName, false);
//                return true;
//            } else if (OTHERS == source){
//                return true;
//            }
        } else {
            int source = source(taskId);
            if (OTHERS == source || CONFUSE == source) {
                if (ToolUtils.isApplicationInstalledByPackageName(packageName)) {
//                    MyStatistics.catchInstalledApp(packageName);
                    return true;
                }
//                else if (!ToolUtils.isNull(filePath) && new File(getFixedFilepath(filePath)).exists()) {
//                    MyStatistics.catchInstalledAppFilepath(packageName);
//                    return true;
//                }
            }
//            else if (OURS == source && !ToolUtils.isApplicationInstalledByPackageName(packageName)){
//                MyStatistics.catchRemovedApp(packageName, false);
//                return true;
//            }
        }
        return false;
    }

    public String getFixedFilepath(String filepath) {
        if(!ToolUtils.isNull(filepath) && !filepath.startsWith("/")){
            return Environment.getExternalStorageDirectory() + "/" + filepath;
        }
        return Environment.getExternalStorageDirectory()+filepath;
    }
}
