package com.serliunx.statemanagement.machine;

import com.serliunx.statemanagement.machine.handler.StateHandlerWrapper;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
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
        int current = indexOf(expectedValue);
        int newIndex = indexOf(newValue);
        if (current == -1 || newIndex == -1)
            return false;
        return index.compareAndSet(current, newIndex);
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
        exchangeToTarget(0);
        // TODO invokeHandlers
    }

    @Override
    public boolean switchTo(S state, boolean invokeHandlers) {
        int i = indexOf(state);
        if (i == -1 ||
                i == index.get()) {
            return false;
        }
        exchangeToTarget(i);
        return true;
    }

    @Override
    public S switchPrevAndGet(boolean invokeHandlers) {
        S oldState = get(index.get());

        S newState = get(index.get());
        if (invokeHandlers)
            invokeHandlers(oldState, newState);
        return newState;
    }

    @Override
    public S getAndSwitchPrev(boolean invokeHandlers) {
        return super.getAndSwitchPrev(invokeHandlers);
    }

    @Override
    public void switchPrev(boolean invokeHandlers) {
        super.switchPrev(invokeHandlers);
    }

    @Override
    public S switchNextAndGet(boolean invokeHandlers) {
        return super.switchNextAndGet(invokeHandlers);
    }

    @Override
    public S getAndSwitchNext(boolean invokeHandlers) {
        return super.getAndSwitchNext(invokeHandlers);
    }

    @Override
    public void switchNext(boolean invokeHandlers) {
        super.switchNext(invokeHandlers);
    }

    @Override
    public void publish(Object event) {

    }

    /**
     * 是否为默认状态
     */
    private boolean isDefault() {
        return index.get() == 0;
    }

    /**
     * 移动下标至上一个状态
     */
    public void exchangeToPrev() {
        final int size = size();
    }

    /**
     * 切换到指定状态值
     * <li> 使用CAS一直尝试, 直到成功
     *
     * @param target    目标值
     */
    private void exchangeToTarget(int target) {
        int currentValue;
        do {
            currentValue = index.get();
        } while (!index.compareAndSet(currentValue, target));
    }
}
