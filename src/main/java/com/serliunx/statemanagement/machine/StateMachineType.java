package com.serliunx.statemanagement.machine;

/**
 * 状态机类型
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @since 2024/12/31
 */
public enum StateMachineType {
    /**
     * 标准, 切换使用读写锁
     */
    STANDARD,

    /**
     * 并发型, 切换使用CAS乐观锁
     */
    CONCURRENT;
}
