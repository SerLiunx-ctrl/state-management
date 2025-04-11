package com.serliunx.statemanagement.machine.support;

import com.serliunx.statemanagement.machine.ConcurrentStateMachine;
import com.serliunx.statemanagement.machine.StateMachine;
import com.serliunx.statemanagement.machine.StateMachineBuilder;

import java.util.List;

/**
 * 状态机工具类集合
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @since 2025/4/11
 */
public final class StateMachines {

    /**
     * 获取一个仅包含状态的并发型状态机
     * <p>
     *     所生成的状态机没有任务事件逻辑，此时仅用作普通的双向状态管理器使用
     * </p>
     *
     * @param states 状态集合
     *
     * @return 仅包含状态的并发型状态机
     * @param <S>   状态
     * @see ConcurrentStateMachine
     * @see com.serliunx.statemanagement.manager.BidirectionalStateManager
     */
    public static <S> ConcurrentStateMachine<S> concurrentStateMachine(S[] states) {
        return StateMachineBuilder.from(states)
                .async(false)
                .concurrent()
                .build();
    }

    /**
     * 获取一个仅包含状态的并发型状态机
     * <p>
     *     所生成的状态机没有任务事件逻辑，此时仅用作普通的双向状态管理器使用
     * </p>
     *
     * @param states 状态集合
     *
     * @return 仅包含状态的并发型状态机
     * @param <S>   状态
     * @see ConcurrentStateMachine
     * @see com.serliunx.statemanagement.manager.BidirectionalStateManager
     */
    public static <S> ConcurrentStateMachine<S> concurrentStateMachine(List<S> states) {
        return StateMachineBuilder.from(states)
                .async(false)
                .concurrent()
                .build();
    }

    /**
     * 获取一个仅包含状态的普通状态机
     * <p>
     *     所生成的状态机没有任务事件逻辑，此时仅用作普通的双向状态管理器使用
     * </p>
     *
     * @param states 状态集合
     *
     * @return 仅包含状态的普通状态机
     * @param <S>   状态
     * @see StateMachine
     * @see com.serliunx.statemanagement.manager.BidirectionalStateManager
     */
    public static <S> StateMachine<S> defaultStateMachine(S[] states) {
        return StateMachineBuilder.from(states)
                .async(false)
                .build();
    }

    /**
     * 获取一个仅包含状态的普通状态机
     * <p>
     *     所生成的状态机没有任务事件逻辑，此时仅用作普通的双向状态管理器使用
     * </p>
     *
     * @param states 状态集合
     *
     * @return 仅包含状态的普通状态机
     * @param <S>   状态
     * @see StateMachine
     * @see com.serliunx.statemanagement.manager.BidirectionalStateManager
     */
    public static <S> StateMachine<S> defaultStateMachine(List<S> states) {
        return StateMachineBuilder.from(states)
                .async(false)
                .build();
    }
}
