package com.serliunx.statemanagement.manager;

/**
 * 双向流转的状态管理器
 * <p>
 * 基于单向流转的状态管理器{@link UnidirectionalStateManager} 实现, 在其基础上允许了反方向切换状态
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.0
 * @since 2024/12/28
 */
public interface BidirectionalStateManager<S> extends UnidirectionalStateManager<S> {

	/**
	 * 切换至上一个状态并返回切换后的状态
	 *
	 * @return 切换后的状态
	 */
	S switchPrevAndGet();

	/**
	 * 获取当前状态并切换至上一个状态
	 *
	 * @return 切换前的状态
	 */
	S getAndSwitchPrev();

	/**
	 * 切换至上一个状态
	 */
	void switchPrev();
}
