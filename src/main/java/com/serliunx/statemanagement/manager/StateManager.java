package com.serliunx.statemanagement.manager;

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
	 *
	 * @return 数量
	 */
	int size();

	/**
	 * 是否可切换
	 * <p>
	 * 默认情况下, 状态集合中的状态数量大于1时就可以切换。部分实现在特定情况下可能不允许切换,
	 * 比如断路的单向状态管理器 {@link BreakageUnidirectionalStateManager}, 当状态为最后一个时
	 * 则不允许向前、或者向后切换
	 * </p>
	 *
	 * @return 可切换返回真, 否则返回假
	 */
	default boolean isSwitchable() {
		return size() > 1;
	}

	/**
	 * 校验当前状态是否为指定的状态
	 *
	 * @param state	指定的状态
	 * @return	符合返回真, 否则返回假
	 */
	default boolean is(S state) {
		return current().equals(state);
	}

	/**
	 * 如果是指定的状态则切换到另一个状态
	 * <li> 例: 检测当前状态是否为 1 且可切换, 如何为 1 则将状态切换到 2
	 * <li> 结合了 {@link #current()}、 {@link #switchTo(Object)} 及 {@link #isSwitchable()}
	 *
	 * @param now		当前状态
	 * @param newState	新的状态
	 * @return	如果当前状态不符合或者不可切换则返回假, 否则走切换逻辑, 此时结果取决于切换的结果.
	 */
	default boolean switchToIfMatch(S now, S newState) {
		if (isSwitchable() || now.equals(current()))
			return switchTo(newState);
		return false;
	}

	/**
	 * 如果当前状态为指定的状态则运行所指定的逻辑
	 *
	 * @param state		状态
	 * @param action	逻辑
	 */
	default void computeIfMatch(S state, Runnable action) {
		if (is(state))
			action.run();
	}
}
