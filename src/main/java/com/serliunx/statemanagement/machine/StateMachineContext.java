package com.serliunx.statemanagement.machine;

import com.serliunx.statemanagement.machine.handler.StateHandler;
import com.serliunx.statemanagement.machine.handler.StateHandlerWrapper;
import com.serliunx.statemanagement.support.DefaultCountableRejectedExecutionHandler;
import com.serliunx.statemanagement.support.ExecutorUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

/**
 * 状态机上下文集合, 用于构建参数封装
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.0
 * @since 2025/2/2
 */
public final class StateMachineContext<S> implements StateEventRegistry<S> {

	/**
	 * 进入事件集合
	 */
	public Map<S, List<StateHandlerWrapper<S>>> entryHandlers;
	/**
	 * 离开事件集合
	 */
	public Map<S, List<StateHandlerWrapper<S>>> leaveHandlers;
	/**
	 * 交换事件集合
	 */
	public Map<String, List<StateHandlerWrapper<S>>> exchangeHandlers;
	/**
	 * 事件注册集合
	 */
	public Map<Object, List<Consumer<StateMachine<S>>>> eventRegistries;
	/**
	 * 异步执行器
	 */
	public Executor executor;
	/**
	 * 是否异步执行
	 * <p>
	 * 当具体的执行器没有指定是否异步时, 将根据该值决定是否异步执行.
	 */
	public Boolean async;
	/**
	 * 初始化状态
	 */
	public S initialState;

	public StateMachineContext(Map<S, List<StateHandlerWrapper<S>>> entryHandlers,
							   Map<S, List<StateHandlerWrapper<S>>> leaveHandlers,
							   Map<String, List<StateHandlerWrapper<S>>> exchangeHandlers,
							   Map<Object, List<Consumer<StateMachine<S>>>> eventRegistries,
							   Executor executor,
							   Boolean async,
							   S initialState
	) {
		this.entryHandlers = entryHandlers;
		this.leaveHandlers = leaveHandlers;
		this.exchangeHandlers = exchangeHandlers;
		this.executor = executorAutoConfiguration(executor);
		this.async = async;
		this.eventRegistries = eventRegistries;
		this.initialState = initialState;
	}

	public StateMachineContext(Map<S, List<StateHandlerWrapper<S>>> entryHandlers,
						 Map<S, List<StateHandlerWrapper<S>>> leaveHandlers,
						 Map<String, List<StateHandlerWrapper<S>>> exchangeHandlers,
						 Map<Object, List<Consumer<StateMachine<S>>>> eventRegistries,
						 Executor executor,
						 Boolean async
	) {
		this(entryHandlers, leaveHandlers, exchangeHandlers, eventRegistries, executor, async, null);
	}

	@Override
	public StateMachineContext<S> whenEntry(S state, StateHandler<S> handler, Boolean async, Executor executor) {
		final List<StateHandlerWrapper<S>> stateHandlerWrappers = entryHandlers.computeIfAbsent(state,
				k -> new ArrayList<>());
		stateHandlerWrappers.add(new StateHandlerWrapper<>(handler, executor, async));
		return this;
	}

	@Override
	public StateMachineContext<S> whenEntry(S state, StateHandler<S> handler, Boolean async) {
		return whenEntry(state, handler, async, null);
	}

	@Override
	public StateMachineContext<S> whenEntry(S state, StateHandler<S> handler) {
		return whenEntry(state, handler, null);
	}

	@Override
	public StateMachineContext<S> whenLeave(S state, StateHandler<S> handler, Boolean async, Executor executor) {
		final List<StateHandlerWrapper<S>> stateHandlerWrappers = leaveHandlers.computeIfAbsent(state,
				k -> new ArrayList<>());
		stateHandlerWrappers.add(new StateHandlerWrapper<>(handler, executor, async));
		return this;
	}

	@Override
	public StateMachineContext<S> whenLeave(S state, StateHandler<S> handler, Boolean async) {
		return whenLeave(state, handler, async, null);
	}

	@Override
	public StateMachineContext<S> whenLeave(S state, StateHandler<S> handler) {
		return whenLeave(state, handler, null);
	}

	@Override
	public StateMachineContext<S> exchange(S from, S to, StateHandler<S> handler, Boolean async, Executor executor) {
		final List<StateHandlerWrapper<S>> stateHandlerWrappers = exchangeHandlers.computeIfAbsent(from.toString()
						+ "-" + to.toString(), k -> new ArrayList<>());
		stateHandlerWrappers.add(new StateHandlerWrapper<>(handler, executor, async));
		return this;
	}

	@Override
	public StateMachineContext<S> exchange(S from, S to, StateHandler<S> handler, Boolean async) {
		return exchange(from, to, handler, async, null);
	}

	@Override
	public StateMachineContext<S> exchange(S from, S to, StateHandler<S> handler) {
		return exchange(from, to, handler, null);
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
