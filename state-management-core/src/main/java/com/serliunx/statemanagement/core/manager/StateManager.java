package com.serliunx.statemanagement.core.manager;

/**
 * 状态管理器
 * <p>
 * 将状态集合按照一定的逻辑流转
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.0
 * @since 2024/12/28
 */
public interface StateManager<S> {

	/**
	 * 获取当前状态
	 *
	 * @return 当前最新状态
	 */
	S current();

	/**
	 * 切换到指定状态
	 *
	 * @param state 新的状态
	 * @return 切换成功返回真, 否则返回假
	 */
	boolean switchTo(S state);

	/**
	 * 重置回默认状态, 一般为状态集合中的第一个
	 */
	void reset();

	/**
	 * 获取当前状态数量
	 * @return 数量
	 */
	int size();

	/**
	 * 是否可切换
	 *
	 * @return 可切换返回真, 否则返回假
	 */
	default boolean isSwitchable() {
		return true;
	}
}
