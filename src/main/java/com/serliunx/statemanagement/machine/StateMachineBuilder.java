package com.serliunx.statemanagement.machine;

import com.serliunx.statemanagement.machine.handler.StateHandler;
import com.serliunx.statemanagement.machine.handler.StateHandlerWrapper;

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
	private final List<S> stateList;
	/**
	 * 执行器
	 */
	private Executor executor;
	/**
	 * 是否异步执行
	 */
	private Boolean async;
	/**
	 * 状态机类型
	 */
	private StateMachineType type = StateMachineType.STANDARD;

	/**
	 * 各种事件
	 */
	private final Map<S, List<StateHandlerWrapper<S>>> entryHandlers = new HashMap<>(64);
	private final Map<S, List<StateHandlerWrapper<S>>> leaveHandlers = new HashMap<>(64);
	private final Map<String, List<StateHandlerWrapper<S>>> exchangeHandlers = new HashMap<>(64);

	private StateMachineBuilder(List<S> states) {
		this.stateList = states;
	}

	private StateMachineBuilder(S[] states) {
		this(Arrays.asList(states));
	}


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
	 * 指定状态机的类型
	 * <li> 状态机并发与否并不影响事件的执行逻辑
	 *
	 * @param type 类型
	 */
	public StateMachineBuilder<S> type(StateMachineType type) {
		if (type == null) {
			throw new NullPointerException();
		}
		this.type = type;
		return this;
	}

	/**
	 * 指定状态机的类型为标准型
	 * <li> 状态机并发与否并不影响事件的执行逻辑
	 */
	public StateMachineBuilder<S> standard() {
		return type(StateMachineType.STANDARD);
	}

	/**
	 * 指定状态机的类型为并发型
	 * <li> 状态机并发与否并不影响事件的执行逻辑
	 */
	public StateMachineBuilder<S> concurrent() {
		return type(StateMachineType.CONCURRENT);
	}

	/**
	 * 构建
	 */
	@SuppressWarnings("unchecked")
	public <M extends StateMachine<S>> M build() {
		if (type == null) {
			throw new NullPointerException();
		}
		if (type.equals(StateMachineType.STANDARD)) {
			return (M)new StandardStateMachine<>(stateList, entryHandlers,
					leaveHandlers, exchangeHandlers, executor, async);
		}
		throw new IllegalArgumentException("未知的状态机类型: " + type);
	}

	/**
	 * 状态机构建器
	 *
	 * @param states 状态集合
	 * @return 状态机构建器实例
	 */
	public static <S> StateMachineBuilder<S> from(S[] states) {
		return new StateMachineBuilder<>(states);
	}

	/**
	 * 状态机构建器
	 *
	 * @param states 状态集合
	 * @return 状态机构建器实例
	 */
	public static <S> StateMachineBuilder<S> from(List<S> states) {
		return new StateMachineBuilder<>(states);
	}
}
