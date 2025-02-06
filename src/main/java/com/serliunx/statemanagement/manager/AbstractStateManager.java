package com.serliunx.statemanagement.manager;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 状态管理器的抽象实现
 * <p>
 * 提供了最基本功能的实现以及部分供子类使用的参数，如：锁、当前状态的序号等.
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.0
 * @since 2024/12/28
 */
public abstract class AbstractStateManager<S> implements StateManager<S> {

	/**
	 * 状态列表
	 */
	private final List<S> stateList;

	/**
	 * 当前状态的序号
	 * <p> 请保证仅在有写锁的情况下去修改
	 */
	private volatile int index;

	/**
	 * 锁
	 */
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	/**
	 * 读锁
	 */
	protected final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
	/**
	 * 写锁
	 */
	protected final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

	/**
	 * @param stateList 状态列表
	 */
	public AbstractStateManager(List<S> stateList) {
		this.stateList = stateList;
		index = 0;
	}

	/**
	 * @param states 状态数组
	 */
	public AbstractStateManager(S[] states) {
		this(Arrays.asList(states));
	}

	/**
	 * 保留空构造器
	 */
	public AbstractStateManager() {
		this((List<S>) null);
	}

	@Override
	public S current() {
		try {
			readLock.lock();
			return get();
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public boolean switchTo(S state) {
		int i = indexOf(state);
		if (i == -1 || i == index) {
			return false;
		}
		try {
			writeLock.lock();
			// 重新检查
			if (i == index) {
				return false;
			}
			index = i;
		} finally {
			writeLock.unlock();
		}
		return true;
	}

	@Override
	public void reset() {
		try {
			writeLock.lock();
			index = getDefault();
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public int size() {
		return stateList.size();
	}

	@Override
	public boolean isSwitchable() {
		return stateList.size() > 1;
	}

	/**
	 * 将序号移动至下一个
	 * <li> 自动归零
	 * <li> 仅在持有写锁的情况下访问
	 */
	@SuppressWarnings("all")
	protected void next() {
		if (++index >= stateList.size())
			index = 0;
	}

	/**
	 * 将序号移动至上一个
	 * <li> 自动归零
	 * <li> 仅在持有写锁的情况下访问
	 */
	@SuppressWarnings("all")
	protected void prev() {
		if (--index < 0)
			index = 0;
	}

	/**
	 * 获取当前状态
	 * <p>
	 * 类及子类访问当前状态时不允许使用{@link #current()}，因为会造成死锁
	 *
	 * <li> 仅在持有锁的情况下访问
	 *
	 * @return 当前状态
	 */
	protected S get() {
		return stateList.get(index);
	}

	/**
	 * 获取指定下标的状态
	 *
	 * @param index	下标
	 * @return 状态
	 */
	protected S get(int index) {
		return stateList.get(index);
	}

	/**
	 * 获取指定状态在状态列表中的序号
	 *
	 * @param state 状态
	 * @return 序号 {@link List#indexOf(Object)}
	 */
	protected int indexOf(S state) {
		return stateList.indexOf(state);
	}

	/**
	 * 判断当前状态是否为状态列表中的最后一个
	 *
	 * @return 是最后一个时返回真, 否则返回假.
	 */
	protected boolean isLast() {
		return index == stateList.size() - 1;
	}

	/**
	 * 判断当前状态是否为状态列表中的第一个
	 *
	 * @return 是第一个时返回真, 否则返回假.
	 */
	protected boolean isFirst() {
		return index == getDefault();
	}

	/**
	 * 获取当前状态的序号
	 *
	 * @return 当前状态序号
	 */
	protected int currentIndex() {
		return index;
	}

	/**
	 * 更新当前状态的序号
	 */
	protected void updateCurrentIndex(int newIndex) {
		index = newIndex;
	}

	/**
	 * 状态序号默认值(等同于默认状态)
	 */
	protected int getDefault() {
		return 0;
	}
}
