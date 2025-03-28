package com.serliunx.statemanagement.machine;

import com.serliunx.statemanagement.machine.handler.StateHandler;
import com.serliunx.statemanagement.machine.handler.StateHandlerProcessParams;
import com.serliunx.statemanagement.machine.handler.StateHandlerWrapper;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * 状态处理器触发
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @since 2025/3/28
 */
public final class HandlerInvocationDelegate {

    /**
     * 触发处理器
     *
     * @param from	源状态
     * @param to	目的状态
     */
    public static <S> void invokeHandlers(StateMachineContext<S> context, S from, S to) {
        // 触发离开处理器
        doInvokeHandlers(context, context.leaveHandlers.get(from), from, to);

        // 触发进入处理器
        doInvokeHandlers(context, context.entryHandlers.get(to), from, to);

        // 触发交换处理器
        final String key = from.toString() + "-" + to.toString();
        doInvokeHandlers(context, context.exchangeHandlers.get(key), from, to);
    }

    /**
     * 触发逻辑
     */
    public static <S> void doInvokeHandlers(StateMachineContext<S> context,
                                            List<StateHandlerWrapper<S>> handlerWrappers, S from, S to) {
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
