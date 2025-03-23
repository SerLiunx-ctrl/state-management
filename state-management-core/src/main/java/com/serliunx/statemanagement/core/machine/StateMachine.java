package com.serliunx.statemanagement.core.machine;

import com.serliunx.statemanagement.core.manager.StateManager;
import com.serliunx.statemanagement.core.manager.BidirectionalStateManager;

/**
 * 状态机定义
 * <p>
 * 基于双向的状态管理器扩展 {@link BidirectionalStateManager}, 切换逻辑依赖于内置的状态管理器；
 * 同时可以多种监听事件, 包括:
 * <li> 切换至指定状态时触发	(进入事件)
 * <li> 切出指定状态时触发	(离开事件)
 * <li> 从A切换到B状态时触发	(交换事件)
 * <p>
 * 请使用 {@link StateMachineBuilder} 来构建状态机.
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.0
 * @since 2024/12/28
 * @see StateMachineBuilder
 * @see BidirectionalStateManager
 * @see StateManager
 */
public interface StateMachine<S> extends BidirectionalStateManager<S>, AutoCloseable {

    /**
     * 切换至下一个状态
     *
     * @param invokeHandlers    是否唤醒状态处理器
     */
    void switchNext(boolean invokeHandlers);

    /**
     * 返回并切换至下一个状态
     *
     * @param invokeHandlers    是否唤醒状态处理器
     * @return 切换前的状态
     */
    S getAndSwitchNext(boolean invokeHandlers);

    /**
     * 切换至下一个状态并返回切换后的状态
     *
     * @param invokeHandlers    是否唤醒状态处理器
     * @return 切换后的状态
     */
    S switchNextAndGet(boolean invokeHandlers);

    /**
     * 切换至上一个状态
     *
     * @param invokeHandlers    是否唤醒状态处理器
     */
    void switchPrev(boolean invokeHandlers);

    /**
     * 获取当前状态并切换至上一个状态
     *
     * @param invokeHandlers    是否唤醒状态处理器
     * @return 切换前的状态
     */
    S getAndSwitchPrev(boolean invokeHandlers);

    /**
     * 切换至上一个状态并返回切换后的状态
     *
     * @param invokeHandlers    是否唤醒状态处理器
     * @return 切换后的状态
     */
    S switchPrevAndGet(boolean invokeHandlers);

    /**
     * 重置回默认状态, 一般为状态集合中的第一个
     *
     * @param invokeHandlers    是否唤醒状态处理器
     */
    void reset(boolean invokeHandlers);

    /**
     * 切换至指定状态
     * <li> 在使用状态机的情况, 仅切换成功才会触发注册的各种事件.
     *
     * @param invokeHandlers    是否唤醒状态处理器
     * @param state             新的状态
     * @return 切换成功返回真, 否则返回假
     */
    boolean switchTo(S state, boolean invokeHandlers);

    /**
     * 发布事件
     *
     * @param event 事件
     */
    void publish(Object event);

    /**
     * 切换至指定状态
     * <li> 在使用状态机的情况, 仅切换成功才会触发注册的各种事件.
     *
     * @param state 新的状态
     * @return 切换成功返回真, 否则返回假
     */
    boolean switchTo(S state);

    @Override
    default void close() throws Exception {}
}
