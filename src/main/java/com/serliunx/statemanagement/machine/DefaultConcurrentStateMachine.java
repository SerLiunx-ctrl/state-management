package com.serliunx.statemanagement.machine;

import com.serliunx.statemanagement.machine.handler.StateHandlerWrapper;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * 并发型状态机的默认实现, 内置的状态序列切换使用CAS实现.
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.0
 * @since 2025/2/6
 */
public class DefaultConcurrentStateMachine<S> extends AbstractStateMachine<S> implements ConcurrentStateMachine<S> {

    /**
     * 当前状态
     */
    private final AtomicInteger index = new AtomicInteger(0);

    DefaultConcurrentStateMachine(List<S> stateList,
                                         Map<S, List<StateHandlerWrapper<S>>> entryHandlers,
                                         Map<S, List<StateHandlerWrapper<S>>> leaveHandlers,
                                         Map<String, List<StateHandlerWrapper<S>>> exchangeHandlers,
                                         Map<Object, List<Consumer<StateMachine<S>>>> eventRegistries,
                                         Executor executor,
                                         Boolean async) {
        super(stateList, entryHandlers, leaveHandlers, exchangeHandlers, eventRegistries, executor, async);
    }

    @Override
    public boolean compareAndSet(S expectedValue, S newValue) {
        return compareAndSet(expectedValue, newValue, false);
    }

    @Override
    public boolean compareAndSet(S expectedValue, S newValue, boolean invokeHandlers) {
        int current = indexOf(expectedValue);
        int newIndex = indexOf(newValue);
        if (current == -1 || newIndex == -1)
            return false;

        S oldState = get(index.get());
        boolean result = index.compareAndSet(current, newIndex);
        if (result && invokeHandlers) {
            S newState = get(index.get());
            invokeHandlers(oldState, newState);
        }

        return result;
    }

    /**
     * 使用CAS不断尝试将当前状态重置回默认值(0)
     *
     * @param invokeHandlers    是否唤醒状态处理器
     */
    @Override
    public void reset(boolean invokeHandlers) {
        if (isDefault())
            return;
        S oldState = get(index.get());
        exchangeToTarget(0);
        S newState = get(index.get());
        if (invokeHandlers)
            invokeHandlers(oldState, newState);
    }

    @Override
    public boolean switchTo(S state, boolean invokeHandlers) {
        int i = indexOf(state);
        if (i == -1 ||
                i == index.get()) {
            return false;
        }
        S oldState = get(index.get());
        exchangeToTarget(i);
        if (invokeHandlers)
            invokeHandlers(oldState, state);
        return true;
    }

    @Override
    public S switchPrevAndGet(boolean invokeHandlers) {
        S oldState = get(index.get());
        exchangeToPrev();
        S newState = get(index.get());
        if (invokeHandlers)
            invokeHandlers(oldState, newState);
        return newState;
    }

    @Override
    public S getAndSwitchPrev(boolean invokeHandlers) {
        S oldState = get(index.get());
        exchangeToPrev();
        S newState = get(index.get());
        if (invokeHandlers)
            invokeHandlers(oldState, newState);
        return oldState;
    }

    @Override
    public void switchPrev(boolean invokeHandlers) {
        S oldState = get(index.get());
        exchangeToPrev();
        S newState = get(index.get());
        if (invokeHandlers)
            invokeHandlers(oldState, newState);
    }

    @Override
    public S switchNextAndGet(boolean invokeHandlers) {
        S oldState = get(index.get());
        exchangeToNext();
        S newState = get(index.get());
        if (invokeHandlers)
            invokeHandlers(oldState, newState);
        return newState;
    }

    @Override
    public S getAndSwitchNext(boolean invokeHandlers) {
        S oldState = get(index.get());
        exchangeToNext();
        S newState =get(index.get());
        if (invokeHandlers)
            invokeHandlers(oldState, newState);
        return oldState;
    }

    @Override
    public void switchNext(boolean invokeHandlers) {
        S oldState = get(index.get());
        exchangeToNext();
        S newState = get(index.get());
        if (invokeHandlers)
            invokeHandlers(oldState, newState);
    }

    @Override
    public void publish(Object event) {

    }

    /**
     * 是否为默认状态
     */
    protected boolean isDefault() {
        return index.get() == 0;
    }

    /**
     * 移动下标至上一个状态
     * <li> 使用CAS一直尝试, 直到成功
     */
    protected void exchangeToPrev() {
        final int size = size();
        int currentValue;
        do {
            currentValue = index.get();
        } while (!index.compareAndSet(currentValue, currentValue == 0 ? size - 1 : currentValue - 1));
    }

    /**
     * 移动下标至下一个状态
     * <li> 使用CAS一直尝试, 直到成功
     */
    protected void exchangeToNext() {
        final int size = size();
        int currentValue;
        do {
            currentValue = index.get();
        } while (!index.compareAndSet(currentValue, currentValue == size - 1 ? 0 : currentValue + 1));
    }

    /**
     * 切换到指定状态值
     * <li> 使用CAS一直尝试, 直到成功
     *
     * @param target    目标值
     */
    protected void exchangeToTarget(int target) {
        int currentValue;
        do {
            currentValue = index.get();
        } while (!index.compareAndSet(currentValue, target));
    }
}
