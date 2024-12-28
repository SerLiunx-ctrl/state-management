package com.serliunx.statemanagement.machine.handler;

import com.serliunx.statemanagement.manager.BidirectionalStateManager;

/**
 * 状态处理器入参
 * <p>
 * 用于状态机处理事件
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.0
 * @since 2024/12/28
 */
@SuppressWarnings("all")
public final class StateHandlerProcessParams<S> {

	/**
	 * 源状态
	 */
	private final S from;
	/**
	 * 目标状态
	 */
	private final S to;
	/**
	 * 附加参数
	 */
	private final Object attach;

	/**
	 * @param from 						原状态
	 * @param to 						目标状态
	 * @param attach 					附加参数
	 * @param bidirectionalStateManager 状态机内置的状态管理器
	 */
	public StateHandlerProcessParams(S from, S to, Object attach) {
		this.from = from;
		this.to = to;
		this.attach = attach;
	}

	public S getFrom() {
		return from;
	}

	public S getTo() {
		return to;
	}

	public Object getAttach() {
		return attach;
	}
}
