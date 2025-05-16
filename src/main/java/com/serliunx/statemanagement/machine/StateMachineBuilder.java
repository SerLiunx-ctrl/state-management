package com.serliunx.statemanagement.machine;

import com.serliunx.statemanagement.machine.handler.StateHandler;
import com.serliunx.statemanagement.machine.handler.StateHandlerWrapper;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

/**
 * 状态机构建
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.0
 * @since 2024/12/28
 */
public final class StateMachineBuilder<S> implements StateEventRegistry<S> {

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
	 * 初始化状态
	 */
	private S initialState;

	/**
	 * 各种事件
	 */
	private final Map<S, List<StateHandlerWrapper<S>>> entryHandlers = new HashMap<>(64);
	private final Map<S, List<StateHandlerWrapper<S>>> leaveHandlers = new HashMap<>(64);
	private final Map<String, List<StateHandlerWrapper<S>>> exchangeHandlers = new HashMap<>(64);
	private final Map<Object, List<Consumer<StateMachine<S>>>> eventRegistries = new HashMap<>(64);

	private StateMachineBuilder(List<S> states) {
		this.stateList = states;
	}

	private StateMachineBuilder(S[] states) {
		this(Arrays.asList(states));
	}

	/**
	 * 定义初始状态
	 *
	 * @param initialState	初始状态
	 * @return 状态机构建
	 */
	public StateMachineBuilder<S> withInitial(S initialState) {
		this.initialState = initialState;
		return this;
	}

	@Override
	public StateMachineBuilder<S> exchange(S from, S to, StateHandler<S> handler, Boolean async, Executor executor) {
		final String key = from.toString() + "-" + to.toString();
		final List<StateHandlerWrapper<S>> stateHandlerWrappers = exchangeHandlers.computeIfAbsent(key,
				k -> new ArrayList<>());
		stateHandlerWrappers.add(new StateHandlerWrapper<>(handler, executor, async));
		return this;
	}

	@Override
	public StateMachineBuilder<S> exchange(S from, S to, StateHandler<S> handler, Boolean async) {
		return exchange(from, to, handler, async, null);
	}

	@Override
	public StateMachineBuilder<S> exchange(S from, S to, StateHandler<S> handler) {
		return exchange(from, to, handler, null);
	}

	@Override
	public StateMachineBuilder<S> whenLeave(S state, StateHandler<S> handler, Boolean async, Executor executor) {
		final List<StateHandlerWrapper<S>> stateHandlerWrappers = leaveHandlers.computeIfAbsent(state,
				k -> new ArrayList<>());
		stateHandlerWrappers.add(new StateHandlerWrapper<>(handler, executor, async));
		return this;
	}

	@Override
	public StateMachineBuilder<S> whenLeave(S state, StateHandler<S> handler, Boolean async) {
		return whenLeave(state, handler, async, null);
	}

	@Override
	public StateMachineBuilder<S> whenLeave(S state, StateHandler<S> handler) {
		return whenLeave(state, handler, null);
	}

	@Override
	public StateMachineBuilder<S> whenEntry(S state, StateHandler<S> handler, Boolean async, Executor executor) {
		final List<StateHandlerWrapper<S>> stateHandlerWrappers = entryHandlers.computeIfAbsent(state,
				k -> new ArrayList<>());
		stateHandlerWrappers.add(new StateHandlerWrapper<>(handler, executor, async));
		return this;
	}

	@Override
	public StateMachineBuilder<S> whenEntry(S state, StateHandler<S> handler, Boolean async) {
		return whenEntry(state, handler, async, null);
	}

	@Override
	public StateMachineBuilder<S> whenEntry(S state, StateHandler<S> handler) {
		return whenEntry(state, handler, null);
	}

	/**
	 * 注册当前状态机感兴趣的事件
	 *
	 * @param event	事件
	 * @param logic	切换逻辑
	 * @return 当前对象, 链式调用
	 */
	public StateMachineBuilder<S> whenHappened(Object event, Consumer<StateMachine<S>> logic) {
		List<Consumer<StateMachine<S>>> consumers = eventRegistries.computeIfAbsent(event, k -> new ArrayList<>());
		consumers.add(logic);
		return this;
	}

	/**
	 * 指定状态机的执行器
	 * <p>
	 * 优先级低于添加事件时指定的执行器
	 *
	 * @param executor 执行器
	 * @return 当前对象, 链式调用
	 */
	public StateMachineBuilder<S> executor(Executor executor) {
		this.executor = executor;
		return this;
	}

	/**
	 * 定义状态机是否异步执行
	 *
	 * @param async 是否异步执行
	 * @return 当前对象, 链式调用
	 */
	public StateMachineBuilder<S> async(Boolean async) {
		this.async = async;
		return this;
	}

	/**
	 * 定义状态机为异步执行
	 *
	 * @return 当前对象, 链式调用
	 */
	public StateMachineBuilder<S> async() {
		return async(true);
	}

	/**
	 * 指定状态机的类型
	 * <p>
	 *     状态机并发与否并不影响事件的执行逻辑
	 * </p>
	 *
	 * @param type 类型
	 * @return 当前对象, 链式调用
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
	 * <p>
	 *     状态机并发与否并不影响事件的执行逻辑
	 * </p>
	 *
	 * @return 当前对象, 链式调用
	 */
	public StateMachineBuilder<S> standard() {
		return type(StateMachineType.STANDARD);
	}

	/**
	 * 指定状态机的类型为并发型
	 * <p>
	 *     状态机并发与否并不影响事件的执行逻辑
	 * </p>
	 *
	 * @return 当前对象, 链式调用
	 */
	public StateMachineBuilder<S> concurrent() {
		return type(StateMachineType.CONCURRENT);
	}

	/**
	 * 执行构建
	 *
	 * @param <M>	状态机类型
	 * @return 状态机
	 */
	@SuppressWarnings("unchecked")
	public <M extends StateMachine<S>> M build() {
		if (type == null) {
			throw new NullPointerException();
		}
		if (type.equals(StateMachineType.STANDARD)) {
			return (M)new StandardStateMachine<>(stateList, entryHandlers,
					leaveHandlers, exchangeHandlers, eventRegistries, executor, async, initialState);
		} else if (type.equals(StateMachineType.CONCURRENT)) {
			return (M)new DefaultConcurrentStateMachine<>(stateList, entryHandlers,
					leaveHandlers, exchangeHandlers, eventRegistries, executor, async, initialState);
		}
		throw new IllegalArgumentException("未知的状态机类型: " + type);
	}

	/**
	 * 状态机构建器
	 *
	 * @param <S>		状态类型
	 * @param states	状态集合
	 * @return 状态机构建器实例
	 */
	public static <S> StateMachineBuilder<S> from(S[] states) {
		return new StateMachineBuilder<>(states);
	}

	/**
	 * 状态机构建器
	 *
	 * @param <S>		状态类型
	 * @param states	状态集合
	 * @return 状态机构建器实例
	 */
	public static <S> StateMachineBuilder<S> from(List<S> states) {
		return new StateMachineBuilder<>(states);
	}
}
