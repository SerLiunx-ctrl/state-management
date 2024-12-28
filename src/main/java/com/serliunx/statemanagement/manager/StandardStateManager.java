package com.serliunx.statemanagement.manager;

import java.util.List;

/**
 * 最简单的状态管理器实现
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.0
 * @since 2024/12/28
 */
public final class StandardStateManager<S> extends AbstractStateManager<S> {

	/**
	 * @param stateList 状态列表
	 */
	public StandardStateManager(List<S> stateList) {
		super(stateList);
	}

	/**
	 * @param states 状态数组
	 */
	public StandardStateManager(S[] states) {
		super(states);
	}
}
