package com.serliunx.statemanagement.core.machine.handler;

import java.util.concurrent.Executor;

/**
 * 处理器封装
 * <p>
 * 用于添加处理器时设置处理器的行为
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.0
 * @since 2024/12/28
 */
@SuppressWarnings("all")
public final class StateHandlerWrapper<S> {

	/**
	 * 状态处理器
	 */
	private final StateHandler<S> stateHandler;
	/**
	 * 执行器
	 * <li> 用于异步执行处理逻辑
	 */
	private final Executor executor;
	/**
	 * 是否异步执行
	 */
	private final Boolean async;

	/**
	 * @param stateHandler	状态处理器
	 * @param executor		执行器
	 * @param async			是否异步执行
	 */
	public StateHandlerWrapper(StateHandler<S> stateHandler, Executor executor, Boolean async) {
		this.stateHandler = stateHandler;
		this.executor = executor;
		this.async = async;
	}

	public StateHandler<S> getStateHandler() {
		return stateHandler;
	}

	public Executor getExecutor() {
		return executor;
	}

	public Boolean getAsync() {
		return async;
	}
}
