package com.ff.common.model;

/**
 * Created by wangjiang on 2016/5/20.
 */
public interface TaskType {
    String DOWNLOADTASK     = "dtask"; // 锁屏上面下载任务
    String DGET             = "dget";  // 其他地方下载任务
    String SIGNTASK         = "vget";  // 签到任务
    String QSIGNTASK        = "qvget"; // 答题签到任务
    String BROWSETASK       = "vtask"; // 浏览任务
    String SHARETASK        = "ntask"; // 分享任务
    String READSHARETASK    = "ztask"; // 阅读分享任务
    String QUESTIONTASK     = "qtask"; // 锁屏上面答题任务
    String QGET             = "qget";  // 其他地方答题任务
    String SCREENSHOT_TASK  = "screenshot_task";  // 截图任务
    String SCREENSHOT_CPL_TASK  = "screenshot_cpl_task";  // cpl截图任务
    String SCREENSHOT_GAME_TASK  = "screenshot_game_task";  // 游戏截图任务
    String INVESTMENT_TASK  = "investment_task";  // 投资任务
}
