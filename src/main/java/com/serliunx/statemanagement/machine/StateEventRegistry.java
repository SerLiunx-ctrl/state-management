package com.serliunx.statemanagement.machine;

import com.serliunx.statemanagement.machine.external.FlexibleStateMachine;
import com.serliunx.statemanagement.machine.handler.StateHandler;

import java.util.concurrent.Executor;

/**
 * 状态机之状态事件注册
 * <p>
 * 注册状态切换时的事件, 一般用于状态机构建和支持动态调整的状态机{@link FlexibleStateMachine};
 * 当然实际不仅于此, 任何相关的都可以使用.
 * </p>
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @since 2025/3/28
 * @see FlexibleStateMachine
 */
public interface StateEventRegistry<S> {

    /**
     * 添加进入事件
     * <p>
     *     切换到了指定状态时执行的逻辑
     * </p>
     *
     * @param state		状态
     * @param handler	处理逻辑
     * @param async		是否异步执行
     * @param executor	异步执行器, 异步执行时将使用, 不指定时将使用状态机内置的执行器
     * @return 当前对象, 链式调用
     */
    StateEventRegistry<S> whenEntry(S state, StateHandler<S> handler, Boolean async, Executor executor);

    /**
     * 添加进入事件
     * <p>
     *     切换到了指定状态时执行的逻辑
     * </p>
     *
     * @param state		状态
     * @param handler	处理逻辑
     * @param async		是否异步执行
     * @return 当前对象, 链式调用
     */
    StateEventRegistry<S> whenEntry(S state, StateHandler<S> handler, Boolean async);

    /**
     * 添加进入事件
     * <p>
     *     切换到了指定状态时执行的逻辑
     * </p>
     *
     * @param state		状态
     * @param handler	处理逻辑
     * @return 当前对象, 链式调用
     */
    StateEventRegistry<S> whenEntry(S state, StateHandler<S> handler);

    /**
     * 添加离开事件
     * <p>
     *     从指定状态切换到别的状态时执行的逻辑
     * </p>
     *
     * @param state		状态
     * @param handler	处理逻辑
     * @param async		是否异步执行
     * @param executor	异步执行器, 异步执行时将使用, 不指定时将使用状态机内置的执行器
     * @return 当前对象, 链式调用
     */
    StateEventRegistry<S> whenLeave(S state, StateHandler<S> handler, Boolean async, Executor executor);

    /**
     * 添加离开事件
     * <p>
     *     从指定状态切换到别的状态时执行的逻辑
     * </p>
     *
     * @param state		状态
     * @param handler	处理逻辑
     * @param async		是否异步执行
     * @return 当前对象, 链式调用
     */
    StateEventRegistry<S> whenLeave(S state, StateHandler<S> handler, Boolean async);

    /**
     * 添加离开事件
     * <p>
     *     从指定状态切换到别的状态时执行的逻辑
     * </p>
     *
     * @param state		状态
     * @param handler	处理逻辑
     * @return 当前对象, 链式调用
     */
    StateEventRegistry<S> whenLeave(S state, StateHandler<S> handler);

    /**
     * 添加交换事件
     * <p>
     *     从A状态切换至B状态时触发
     * </p>
     *
     * @param from		源状态
     * @param to		目的状态
     * @param handler	处理器
     * @param async		是否异步执行
     * @param executor	异步执行器, 异步执行时将使用, 不指定时将使用状态机内置的执行器
     * @return 当前对象, 链式调用
     */
    StateEventRegistry<S> exchange(S from, S to, StateHandler<S> handler, Boolean async, Executor executor);

    /**
     * 添加交换事件
     * <p>
     *     从A状态切换至B状态时触发
     * </p>
     *
     * @param from		源状态
     * @param to		目的状态
     * @param handler	处理器
     * @param async		是否异步执行
     * @return 当前对象, 链式调用
     */
    StateEventRegistry<S> exchange(S from, S to, StateHandler<S> handler, Boolean async);

    /**
     * 添加交换事件
     * <p>
     *     从A状态切换至B状态时触发
     * </p>
     *
     * @param from		源状态
     * @param to		目的状态
     * @param handler	处理器
     * @return 当前对象, 链式调用
     */
    StateEventRegistry<S> exchange(S from, S to, StateHandler<S> handler);
}
