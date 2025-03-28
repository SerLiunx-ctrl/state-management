package com.serliunx.statemanagement.machine;

import com.serliunx.statemanagement.machine.handler.StateHandler;
import com.serliunx.statemanagement.machine.handler.StateHandlerProcessParams;
import com.serliunx.statemanagement.machine.handler.StateHandlerWrapper;
import com.serliunx.statemanagement.manager.AbstractStateManager;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 状态机抽象实现, 实现最基本功能
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.0
 * @since 2024/12/31
 */
public abstract class AbstractStateMachine<S> extends AbstractStateManager<S> implements StateMachine<S> {

    /**
     * 状态机上下文
     */
    protected final StateMachineContext<S> context;

    /**
     * 默认的构造函数
     *
     * @param stateList 状态列表
     * @param context   状态机上下文
     */
    public AbstractStateMachine(List<S> stateList, StateMachineContext<S> context) {
        super(stateList);
        this.context = context;
    }

    @Override
    public void close() throws Exception {
        final Executor executor = context.executor;
        if (executor == null) {
            return;
        }
        if (executor instanceof ExecutorService) {
            ExecutorService es = (ExecutorService) executor;
            es.shutdown();
            if (!es.awaitTermination(10, TimeUnit.SECONDS)) {
                es.shutdownNow();
            }
        } else if (executor instanceof AutoCloseable) {
            AutoCloseable ac = (AutoCloseable) executor;
            ac.close();
        }
    }

    @Override
    public void reset(boolean invokeHandlers) {
        try {
            writeLock.lock();
            S oldState = get();
            super.reset();
            S newState = get();
            if (invokeHandlers)
                invokeHandlers(oldState, newState);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean switchTo(S state, boolean invokeHandlers) {
        int i = indexOf(state);
        if (i == -1 || i == currentIndex()) {
            return false;
        }
        try {
            writeLock.lock();
            // 重新检查
            if (i == currentIndex()) {
                return false;
            }
            S oldState = get();

            updateCurrentIndex(i);

            S newState = get();
            if (invokeHandlers)
                invokeHandlers(oldState, newState);
            return true;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public S switchPrevAndGet(boolean invokeHandlers) {
        try {
            writeLock.lock();
            S oldState = get();
            prev();
            S newState = get();
            if (invokeHandlers)
                invokeHandlers(oldState, newState);
            return newState;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public S getAndSwitchPrev(boolean invokeHandlers) {
        try {
            writeLock.lock();
            S oldState = get();
            prev();
            S newState = get();
            if (invokeHandlers)
                invokeHandlers(oldState, newState);
            return oldState;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void switchPrev(boolean invokeHandlers) {
        try {
            writeLock.lock();
            S oldState = get();
            prev();
            S newState = get();
            if (invokeHandlers)
                invokeHandlers(oldState, newState);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public S switchNextAndGet(boolean invokeHandlers) {
        try {
            writeLock.lock();
            S oldState = get();
            next();
            S newState = get();
            if (invokeHandlers)
                invokeHandlers(oldState, newState);
            return newState;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public S getAndSwitchNext(boolean invokeHandlers) {
        try {
            writeLock.lock();
            S oldState = get();
            next();
            S newState = get();
            if (invokeHandlers)
                invokeHandlers(oldState, newState);
            return oldState;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void switchNext(boolean invokeHandlers) {
        try {
            writeLock.lock();
            S oldState = get();
            next();
            S newState = get();
            if (invokeHandlers)
                invokeHandlers(oldState, newState);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void publish(Object event) {
        List<Consumer<StateMachine<S>>> consumers = context.eventRegistries.get(event);
        if (consumers == null ||
                consumers.isEmpty()) {
            return;
        }

        final Executor executor = context.executor;
        final boolean async = context.async != null && context.async && executor != null;
        consumers.forEach(consumer -> {
            if (async)
                executor.execute(() -> consumer.accept(this));
            else
                consumer.accept(this);
        });
    }

    @Override
    public S switchPrevAndGet() {
        return switchPrevAndGet(true);
    }

    @Override
    public S getAndSwitchPrev() {
        return getAndSwitchPrev(true);
    }

    @Override
    public void switchPrev() {
        switchPrev(true);
    }

    @Override
    public S switchNextAndGet() {
        return switchNextAndGet(true);
    }

    @Override
    public S getAndSwitchNext() {
        return getAndSwitchNext(true);
    }

    @Override
    public void switchNext() {
        switchNext(true);
    }

    @Override
    public boolean switchTo(S state) {
        return switchTo(state, true);
    }

    @Override
    public void reset() {
        reset(true);
    }

    /**
     * 触发处理器
     *
     * @param from	源状态
     * @param to	目的状态
     */
    protected final void invokeHandlers(S from, S to) {
        // 触发离开处理器
        HandlerInvocationDelegate.doInvokeHandlers(context, context.leaveHandlers.get(from), from, to);

        // 触发进入处理器
        HandlerInvocationDelegate.doInvokeHandlers(context, context.entryHandlers.get(to), from, to);

        // 触发交换处理器
        final String key = from.toString() + "-" + to.toString();
        HandlerInvocationDelegate.doInvokeHandlers(context, context.exchangeHandlers.get(key), from, to);
    }
}
