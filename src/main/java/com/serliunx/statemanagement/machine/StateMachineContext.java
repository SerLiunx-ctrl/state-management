package com.serliunx.statemanagement.machine;

import com.serliunx.statemanagement.machine.handler.StateHandlerWrapper;
import com.serliunx.statemanagement.support.DefaultCountableRejectedExecutionHandler;
import com.serliunx.statemanagement.support.ExecutorUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * 状态机上下文集合, 用于构建参数封装
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.0
 * @since 2025/2/2
 */
public final class StateMachineContext<S> {

	/**
	 * 进入事件集合
	 */
	final Map<S, List<StateHandlerWrapper<S>>> entryHandlers;
	/**
	 * 离开事件集合
	 */
	final Map<S, List<StateHandlerWrapper<S>>> leaveHandlers;
	/**
	 * 交换事件集合
	 */
	final Map<String, List<StateHandlerWrapper<S>>> exchangeHandlers;
	/**
	 * 事件注册集合
	 */
	final Map<Object, List<Consumer<StateMachine<S>>>> eventRegistries;
	/**
	 * 异步执行器
	 */
	final Executor executor;
	/**
	 * 是否异步执行
	 * <p>
	 * 当具体的执行器没有指定是否异步时, 将根据该值决定是否异步执行.
	 */
	final Boolean async;

	StateMachineContext(Map<S, List<StateHandlerWrapper<S>>> entryHandlers,
						 Map<S, List<StateHandlerWrapper<S>>> leaveHandlers,
						 Map<String, List<StateHandlerWrapper<S>>> exchangeHandlers,
						 Map<Object, List<Consumer<StateMachine<S>>>> eventRegistries,
						 Executor executor,
						 Boolean async
	) {
		this.entryHandlers = entryHandlers;
		this.leaveHandlers = leaveHandlers;
		this.exchangeHandlers = exchangeHandlers;
		this.executor = executorAutoConfiguration(executor);
		this.async = async;
		this.eventRegistries = eventRegistries;
	}

	/**
	 * 执行器为空时自动创建一个适合当前操作系统的执行器（线程池）
	 */
	private Executor executorAutoConfiguration(Executor source) {
		if (source == null) {
			return ExecutorUtils.adaptiveThreadPool(new DefaultCountableRejectedExecutionHandler());
		}
		return source;
	}
}
