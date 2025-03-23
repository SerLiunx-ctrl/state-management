package com.serliunx.statemanagement.core.manager;

import java.util.List;

/**
 * 双向流转的状态管理器的默认实现
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.0
 * @since 2024/12/28
 */
public class DefaultBidirectionalStateManager<S> extends DefaultUnidirectionalStateManager<S>
		implements BidirectionalStateManager<S> {

	/**
	 * @param stateList 状态列表
	 */
	public DefaultBidirectionalStateManager(List<S> stateList) {
		super(stateList);
	}

	/**
	 * @param states 状态数组
	 */
	public DefaultBidirectionalStateManager(S[] states) {
		super(states);
	}

	@Override
	public S switchPrevAndGet() {
		try {
			writeLock.lock();
			prev();
			return get();
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public S getAndSwitchPrev() {
		try {
			writeLock.lock();
			S current = get();
			prev();
			return current;
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public void switchPrev() {
		try {
			writeLock.lock();
			prev();
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public boolean switchTo(S state) {
		return defaultSwitchTo(state);
	}
}
