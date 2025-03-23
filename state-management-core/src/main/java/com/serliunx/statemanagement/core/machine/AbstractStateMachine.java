package com.serliunx.statemanagement.core.machine;

import com.serliunx.statemanagement.core.machine.handler.StateHandler;
import com.serliunx.statemanagement.core.machine.handler.StateHandlerProcessParams;
import com.serliunx.statemanagement.core.machine.handler.StateHandlerWrapper;
import com.serliunx.statemanagement.core.manager.AbstractStateManager;

import java.util.List;
import java.util.Map;
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
     * @param entryHandlers 	进入事件处理器集合
     * @param leaveHandlers 	离开事件处理器集合
     * @param exchangeHandlers	交换事件处理器集合
     * @param executor 			异步执行器
     * @param async 			是否异步执行
     */
    AbstractStateMachine(List<S> stateList,
                         Map<S, List<StateHandlerWrapper<S>>> entryHandlers,
                         Map<S, List<StateHandlerWrapper<S>>> leaveHandlers,
                         Map<String, List<StateHandlerWrapper<S>>> exchangeHandlers,
                         Map<Object, List<Consumer<StateMachine<S>>>> eventRegistries,
                         Executor executor,
                         Boolean async
    ) {
        super(stateList);
        context = new StateMachineContext<>(entryHandlers, leaveHandlers, exchangeHandlers, eventRegistries, executor, async);
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
        doInvokeHandlers(context.leaveHandlers.get(from), from, to);

        // 触发进入处理器
        doInvokeHandlers(context.entryHandlers.get(to), from, to);

        // 触发交换处理器
        final String key = from.toString() + "-" + to.toString();
        doInvokeHandlers(context.exchangeHandlers.get(key), from, to);
    }

    /**
     * 触发
     */
    private void doInvokeHandlers(List<StateHandlerWrapper<S>> handlerWrappers, S from, S to) {
        if (handlerWrappers == null)
            return;
        handlerWrappers.forEach(hw -> {
            final StateHandler<S> stateHandler;
            if (hw == null ||
                    (stateHandler = hw.getStateHandler()) == null)
                return;
            final StateHandlerProcessParams<S> params = new StateHandlerProcessParams<>(from, to, null);

            /*
             * 一、异步逻辑校验: 首先判断是否需要异步执行状态处理器, 具体的状态逻辑处理器优先级大于全局
             * 即： 如果全局指定了同步执行, 但此时特定的状态处理器注册时指定为异步执行的话. 该处理器
             * 为异步执行.
             *
             * 二、 当确定了为异步执行时会选择合适的异步执行器(通常都是线程池), 如果状态处理器注册
             * 时指定了异步执行器, 则优先使用该异步执行器；反则会使用全局的异步执行器。如果上一步骤
             * 中确定为异步执行但当前步骤没有寻找到合适的异步执行器则会报空指针异常(当前版本不会出现)
             */
            if (hw.getAsync() == null ?
                    (context.async != null && context.async) :
                    hw.getAsync()) {
                final Executor executor;
                if ((executor = hw.getExecutor() == null ?
                        context.executor : hw.getExecutor()) == null)
                    // 不应该发生
                    throw new Error();
                executor.execute(() -> stateHandler.handle(params));
            } else
                stateHandler.handle(params);
        });
    }
}
