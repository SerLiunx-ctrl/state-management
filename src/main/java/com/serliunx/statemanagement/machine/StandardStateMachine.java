package com.serliunx.statemanagement.machine;

import com.serliunx.statemanagement.machine.handler.StateHandlerWrapper;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

/**
 * 状态机的标准实现
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.0
 * @since 2024/12/28
 */
public class StandardStateMachine<S> extends AbstractStateMachine<S> implements StateMachine<S> {

	/**
	 * 默认的构造函数
	 *
	 * @param entryHandlers 	进入事件处理器集合
	 * @param leaveHandlers 	离开事件处理器集合
	 * @param exchangeHandlers	交换事件处理器集合
	 * @param executor 			异步执行器
	 * @param async 			是否异步执行
	 */
	StandardStateMachine(List<S> stateList,
						 Map<S, List<StateHandlerWrapper<S>>> entryHandlers,
						 Map<S, List<StateHandlerWrapper<S>>> leaveHandlers,
						 Map<String, List<StateHandlerWrapper<S>>> exchangeHandlers,
						 Map<Object, List<Consumer<StateMachine<S>>>> eventRegistries,
						 Executor executor,
						 Boolean async,
						 S initialState
	) {
		super(stateList, new StateMachineContext<>(entryHandlers, leaveHandlers, exchangeHandlers, eventRegistries,
				executor, async, initialState));

		final int initialIndex = indexOf(context.initialState);
		if (initialIndex != -1) {
			updateCurrentIndex(initialIndex);
		}
	}
}
