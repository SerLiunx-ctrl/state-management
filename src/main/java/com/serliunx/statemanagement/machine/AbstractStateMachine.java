package com.serliunx.statemanagement.machine;

import com.serliunx.statemanagement.machine.handler.StateHandler;
import com.serliunx.statemanagement.machine.handler.StateHandlerProcessParams;
import com.serliunx.statemanagement.machine.handler.StateHandlerWrapper;
import com.serliunx.statemanagement.manager.AbstractStateManager;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 * 状态机抽象实现, 实现最基本功能
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.0
 * @since 2024/12/31
 */
public abstract class AbstractStateMachine<S> extends AbstractStateManager<S> implements StateMachine<S> {

    /**
     * 进入事件集合
     */
    protected final Map<S, List<StateHandlerWrapper<S>>> entryHandlers;
    /**
     * 离开事件集合
     */
    protected final Map<S, List<StateHandlerWrapper<S>>> leaveHandlers;
    /**
     * 交换事件集合
     */
    protected final Map<String, List<StateHandlerWrapper<S>>> exchangeHandlers;
    /**
     * 异步执行器
     */
    protected final Executor executor;
    /**
     * 是否异步执行
     * <p>
     * 当具体的执行器没有指定是否异步时, 将根据该值决定是否异步执行.
     */
    protected final Boolean async;

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
                         Executor executor,
                         Boolean async
    ) {
        super(stateList);
        this.entryHandlers = entryHandlers;
        this.leaveHandlers = leaveHandlers;
        this.exchangeHandlers = exchangeHandlers;
        this.executor = executor;
        this.async = async;
    }

    @Override
    public void close() throws Exception {
        if (executor == null) {
            return;
        }
        if (executor instanceof ExecutorService) {
            ExecutorService es = (ExecutorService) executor;
            es.shutdown();
        } else if (executor instanceof AutoCloseable) {
            AutoCloseable ac = (AutoCloseable) executor;
            ac.close();
        }
    }

    @Override
    public S switchPrevAndGet() {
        try {
            writeLock.lock();
            S oldState = get();
            prev();
            S newState = get();
            invokeHandlers(oldState, newState);
            return newState;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public S getAndSwitchPrev() {
        try {
            writeLock.lock();
            S oldState = get();
            prev();
            S newState = get();
            invokeHandlers(oldState, newState);
            return oldState;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void switchPrev() {
        try {
            writeLock.lock();
            S oldState = get();
            prev();
            S newState = get();
            invokeHandlers(oldState, newState);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public S switchNextAndGet() {
        try {
            writeLock.lock();
            S oldState = get();
            next();
            S newState = get();
            invokeHandlers(oldState, newState);
            return newState;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public S getAndSwitchNext() {
        try {
            writeLock.lock();
            S oldState = get();
            next();
            S newState = get();
            invokeHandlers(oldState, newState);
            return oldState;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void switchNext() {
        try {
            writeLock.lock();
            S oldState = get();
            next();
            S newState = get();
            invokeHandlers(oldState, newState);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean switchTo(S state) {
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
            invokeHandlers(oldState, newState);
            return true;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void reset() {
        try {
            writeLock.lock();
            S oldState = get();
            super.reset();
            S newState = get();
            invokeHandlers(oldState, newState);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 触发处理器
     *
     * @param from	源状态
     * @param to	目的状态
     */
    protected final void invokeHandlers(S from, S to) {
        // 触发离开处理器
        doInvokeHandlers(leaveHandlers.get(from), from, to);

        // 触发进入处理器
        doInvokeHandlers(entryHandlers.get(to), from, to);

        // 触发交换处理器
        final String key = from.toString() + "-" + to.toString();
        doInvokeHandlers(exchangeHandlers.get(key), from, to);
    }

    /**
     * 触发
     */
    private void doInvokeHandlers(List<StateHandlerWrapper<S>> handlerWrappers, S from, S to) {
        if (handlerWrappers == null) {
            return;
        }
        // 全局的异步状态
        final boolean isGlobalAsync = async != null && async;

        handlerWrappers.forEach(hw -> {
            final StateHandler<S> stateHandler;
            if (hw == null ||
                    (stateHandler = hw.getStateHandler()) == null) {
                return;
            }

            final Executor executorToRun = hw.getExecutor() == null ? executor : hw.getExecutor();
            final boolean runInAsync = hw.getAsync() == null ? isGlobalAsync : hw.getAsync();
            final StateHandlerProcessParams<S> params = new StateHandlerProcessParams<>(from, to, null);

            if (runInAsync) {
                if (executorToRun == null) {
                    throw new NullPointerException();
                }
                executorToRun.execute(() -> stateHandler.handle(params));
            } else {
                stateHandler.handle(params);
            }
        });
    }
}
