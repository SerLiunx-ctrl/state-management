package com.serliunx.statemanagement.manager;

/**
 * 将指定状态管理器标记为循环的状态管理器
 * <p>
 *     允许单向、双向循环
 * </p>
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.0
 * @since 2025/1/13
 */
public interface CircleStateManager {

    /**
     * 是否为循环的状态管理器
     *
     * @return 属于循环状态管理器返回真, 否则返回假.
     */
    default boolean isCircle() {
        return true;
    }
}