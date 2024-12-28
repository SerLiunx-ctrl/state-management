package com.serliunx.statemanagement.machine;

import com.serliunx.statemanagement.machine.handler.StateHandler;
import com.serliunx.statemanagement.machine.handler.StateHandlerWrapper;
import com.serliunx.statemanagement.manager.BidirectionalStateManager;

import java.util.*;
import java.util.concurrent.Executor;

/**
 * 状态机构建
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.0
 * @since 2024/12/28
 */
public final class StateMachineBuilder<S> {

	/**
	 * 状态管理器
	 */
	private List<S> stateList;
	/**
	 * 执行器
	 */
	private Executor executor;
	/**
	 * 是否异步执行
	 */
	private Boolean async;

	/**
	 * 各种事件
	 */
	private final Map<S, List<StateHandlerWrapper<S>>> entryHandlers = new HashMap<>(64);
	private final Map<S, List<StateHandlerWrapper<S>>> leaveHandlers = new HashMap<>(64);
	private final Map<String, List<StateHandlerWrapper<S>>> exchangeHandlers = new HashMap<>(64);

	// private-ctor
	private StateMachineBuilder() {}

	/**
	 * 添加交换事件
	 * <li> 从A状态切换至B状态时触发
	 *
	 * @param from		源状态
	 * @param to		目的状态
	 * @param handler	处理器
	 * @param async		是否异步执行
	 * @param executor	异步执行器, 异步执行时将使用, 不指定时将使用状态机内置的执行器
	 */
	public StateMachineBuilder<S> exchange(S from, S to, StateHandler<S> handler, Boolean async, Executor executor) {
		final String key = from.toString() + "-" + to.toString();
		final List<StateHandlerWrapper<S>> stateHandlerWrappers = exchangeHandlers.computeIfAbsent(key,
				k -> new ArrayList<>());
		stateHandlerWrappers.add(new StateHandlerWrapper<>(handler, executor, async));
		return this;
	}

	/**
	 * 添加交换事件
	 * <li> 从A状态切换至B状态时触发
	 *
	 * @param from		源状态
	 * @param to		目的状态
	 * @param handler	处理器
	 * @param async		是否异步执行
	 */
	public StateMachineBuilder<S> exchange(S from, S to, StateHandler<S> handler, Boolean async) {
		return exchange(from, to, handler, async, null);
	}

	/**
	 * 添加交换事件
	 * <li> 从A状态切换至B状态时触发
	 *
	 * @param from		源状态
	 * @param to		目的状态
	 * @param handler	处理器
	 */
	public StateMachineBuilder<S> exchange(S from, S to, StateHandler<S> handler) {
		return exchange(from, to, handler, null);
	}

	/**
	 * 添加离开事件
	 * <li> 从指定状态切换到别的状态时执行的逻辑
	 *
	 * @param state		状态
	 * @param handler	处理逻辑
	 * @param async		是否异步执行
	 * @param executor	异步执行器, 异步执行时将使用, 不指定时将使用状态机内置的执行器
	 */
	public StateMachineBuilder<S> whenLeave(S state, StateHandler<S> handler, Boolean async, Executor executor) {
		final List<StateHandlerWrapper<S>> stateHandlerWrappers = leaveHandlers.computeIfAbsent(state,
				k -> new ArrayList<>());
		stateHandlerWrappers.add(new StateHandlerWrapper<>(handler, executor, async));
		return this;
	}

	/**
	 * 添加离开事件
	 * <li> 从指定状态切换到别的状态时执行的逻辑
	 *
	 * @param state		状态
	 * @param handler	处理逻辑
	 * @param async		是否异步执行
	 */
	public StateMachineBuilder<S> whenLeave(S state, StateHandler<S> handler, Boolean async) {
		return whenLeave(state, handler, async, null);
	}

	/**
	 * 添加离开事件
	 * <li> 从指定状态切换到别的状态时执行的逻辑
	 *
	 * @param state		状态
	 * @param handler	处理逻辑
	 */
	public StateMachineBuilder<S> whenLeave(S state, StateHandler<S> handler) {
		return whenLeave(state, handler, null);
	}

	/**
	 * 添加进入事件
	 * <li> 切换到了指定状态时执行的逻辑
	 *
	 * @param state		状态
	 * @param handler	处理逻辑
	 * @param async		是否异步执行
	 * @param executor	异步执行器, 异步执行时将使用, 不指定时将使用状态机内置的执行器
	 */
	public StateMachineBuilder<S> whenEntry(S state, StateHandler<S> handler, Boolean async, Executor executor) {
		final List<StateHandlerWrapper<S>> stateHandlerWrappers = entryHandlers.computeIfAbsent(state,
				k -> new ArrayList<>());
		stateHandlerWrappers.add(new StateHandlerWrapper<>(handler, executor, async));
		return this;
	}

	/**
	 * 添加进入事件
	 * <li> 切换到了指定状态时执行的逻辑
	 *
	 * @param state		状态
	 * @param handler	处理逻辑
	 * @param async		是否异步执行
	 */
	public StateMachineBuilder<S> whenEntry(S state, StateHandler<S> handler, Boolean async) {
		return whenEntry(state, handler, async, null);
	}

	/**
	 * 添加进入事件
	 * <li> 切换到了指定状态时执行的逻辑
	 *
	 * @param state		状态
	 * @param handler	处理逻辑
	 */
	public StateMachineBuilder<S> whenEntry(S state, StateHandler<S> handler) {
		return whenEntry(state, handler, null);
	}

	/**
	 * 指定状态机的执行器
	 * <p>
	 * 优先级低于添加事件时指定的执行器
	 *
	 * @param executor 执行器
	 */
	public StateMachineBuilder<S> executor(Executor executor) {
		this.executor = executor;
		return this;
	}

	/**
	 * 定义状态机是否异步执行
	 *
	 * @param async 是否异步执行
	 */
	public StateMachineBuilder<S> async(Boolean async) {
		this.async = async;
		return this;
	}

	/**
	 * 定义状态机为异步执行
	 */
	public StateMachineBuilder<S> async() {
		return async(true);
	}

	/**
	 * 设置状态列表
	 */
	public StateMachineBuilder<S> states(S[] states) {
		return states(Arrays.asList(states));
	}

	/**
	 * 设置状态列表
	 */
	public StateMachineBuilder<S> states(List<S> states) {
		stateList = states;
		return this;
	}

	/**
	 * 构建
	 */
	@SuppressWarnings("unchecked")
	public <M extends StateMachine<S>> M build() {
		return (M) new StandardStateMachine<>(stateList, entryHandlers,
				leaveHandlers, exchangeHandlers, executor, async);
	}

	/**
	 * 状态机构建器
	 *
	 * @param stateClass 状态类
	 * @return 状态机构建器实例
	 * @param <S> 状态类参数
	 */
	@SuppressWarnings("all")
	public static <S> StateMachineBuilder<S> from(Class<? extends S> stateClass) {
		return new StateMachineBuilder<>();
	}
}
