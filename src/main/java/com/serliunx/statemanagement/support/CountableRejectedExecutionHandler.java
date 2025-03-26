package com.serliunx.statemanagement.support;

import java.util.concurrent.RejectedExecutionHandler;

/**
 * 附带计数的拒绝策略
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.0
 * @since 2025/3/5
 */
public interface CountableRejectedExecutionHandler extends RejectedExecutionHandler {

    /**
     * 获取当前拒绝的任务数量
     *
     * @return 当目前为止所拒绝的任务数量
     */
    long getCount();

    /**
     * 获取最后一次被拒绝的任务
     *
     * @return  最后一次被拒绝的任务
     */
    Runnable getLastRejectedTask();
}
