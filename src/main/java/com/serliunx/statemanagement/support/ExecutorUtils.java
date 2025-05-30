package com.serliunx.statemanagement.support;

import java.util.concurrent.*;

/**
 * 线程池相关工具类
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.0
 * @since 2024/12/28
 */
public final class ExecutorUtils {

	private ExecutorUtils() {throw new UnsupportedOperationException();}

	/**
	 * 快速获取自适应参数的线程池
	 * <p>
	 *     核心线程数量为当前处理器数量的两倍; 最大线程数量为当前处理器数量的四倍.
	 * </p>
 	 *
	 * @param rejectedExecutionHandler 拒绝策略
	 * @return 执行器(线程池)
	 */
	public static Executor adaptiveThreadPool(RejectedExecutionHandler rejectedExecutionHandler) {
		final int processors = Runtime.getRuntime().availableProcessors();
		return new ThreadPoolExecutor(processors * 2, processors * 4, 5,
				TimeUnit.MINUTES, new ArrayBlockingQueue<>(processors * 8),
				new NamedThreadFactory("state-process-%s"), rejectedExecutionHandler);
	}

}
