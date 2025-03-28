package com.serliunx.statemanagement.machine.external;

import com.serliunx.statemanagement.machine.StateEventRegistry;
import com.serliunx.statemanagement.machine.StateMachine;
import com.serliunx.statemanagement.machine.handler.StateHandler;
import com.serliunx.statemanagement.machine.handler.StateHandlerWrapper;

import java.util.List;

/**
 * 可变的、灵活的状态机, 支持在运行的过程中动态的增减状态及状态切换的事件
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @since 2025/3/28
 */
public interface FlexibleStateMachine<S> extends StateMachine<S>, StateEventRegistry<S> {

    /**
     * 获取指定状态下所有的离开事件处理器.
     * <p>
     * 通过 {@link StateEventRegistry#whenLeave(Object, StateHandler)} 等方法注册.
     *
     * @param state 状态
     * @return  所有与指定状态相关的离开事件处理器
     */
    List<StateHandlerWrapper<S>> allLeaveHandlers(S state);

    /**
     * 获取指定状态下所有的进入事件处理器.
     * <p>
     * 通过 {@link StateEventRegistry#whenEntry(Object, StateHandler)} 等方法注册.
     *
     * @param state 状态
     * @return  所有与指定状态相关的进入事件处理器
     */
    List<StateHandlerWrapper<S>> allEntryHandlers(S state);

    /**
     * 获取指定状态下所有的交换事件处理器.
     * <p>
     * 通过 {@link StateEventRegistry#exchange(Object, Object, StateHandler)} 等方法注册.
     *
     * @param from  源状态
     * @param to    目标状态
     * @return  所有与指定状态相关的交换事件处理器
     */
    List<StateHandlerWrapper<S>> allExchangeHandlers(S from, S to);
}
