package com.serliunx.statemanagement.manager;

import java.util.List;

/**
 * 单向流转的状态管理器的默认实现
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.0
 * @since 2024/12/28
 */
public class DefaultUnidirectionalStateManager<S> extends AbstractStateManager<S>
		implements UnidirectionalStateManager<S> {

	/**
	 * @param stateList 状态列表
	 */
	public DefaultUnidirectionalStateManager(List<S> stateList) {
		super(stateList);
	}

	/**
	 * @param states 状态数组
	 */
	public DefaultUnidirectionalStateManager(S[] states) {
		super(states);
	}

	@Override
	public S switchNextAndGet() {
		try {
			writeLock.lock();
			next();
			return get();
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public S getAndSwitchNext() {
		try {
			writeLock.lock();
			S current = get();
			next();
			return current;
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public void switchNext() {
		try {
			writeLock.lock();
			next();
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public boolean switchTo(S state) {
		try {
			writeLock.lock();
			final int i;
			if ((i = indexOf(state)) == -1 ||
					(!isLast() && i < currentIndex()) ||
					(isLast() && i != getDefault())) {
				return false;
			}
			updateCurrentIndex(i);
		} finally {
			writeLock.unlock();
		}
		return true;
	}

	/**
	 * 保留默认的切换方式供子类使用
	 */
	protected boolean defaultSwitchTo(S state) {
		return super.switchTo(state);
	}
}
