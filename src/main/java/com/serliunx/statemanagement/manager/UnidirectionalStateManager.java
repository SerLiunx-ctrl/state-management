package com.serliunx.statemanagement.manager;

/**
 * 单向流转的状态管理器
 * <p>
 *     其基本逻辑等同于 {@link StateManager}, 但存在以下不同:
 *     状态只能单方向流动(最后一个状态允许切换至第一个状态), 如果有A, B, C, D 四种状态则存在以下几种情况:
 * </p>
 * <ul>
 *  <li> A 至 B 允许直接切换, A 至 C 允许直接切换, C 至 D 允许直接切换 等等..
 *  <li> B 至 A 不允许切换, C 至 A 不允许切换, D 至 C 不允许切换 等等..
 *  <li> 特例: D 至 A 是允许的, 因为 D 是最后一个状态, 故可以切换至第一个状态.
 * </ul>
 * 即状态的切换只允许一个方向，不允许向前流动，除非到达最后一个状态!
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.0
 * @since 2024/12/28
 */
public interface UnidirectionalStateManager<S> extends StateManager<S>, CircleStateManager {

	/**
	 * 切换至下一个状态并返回切换后的状态
	 *
	 * @return 切换后的状态
	 */
	S switchNextAndGet();

	/**
	 * 返回并切换至下一个状态
	 *
	 * @return 切换前的状态
	 */
	S getAndSwitchNext();

	/**
	 * 切换至下一个状态
	 */
	void switchNext();
}
