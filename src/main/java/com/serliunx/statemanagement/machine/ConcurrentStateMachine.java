package com.serliunx.statemanagement.machine;

/**
 * 基本行为与{@link StateMachine} 一致, 最大不同是切换状态不再使用直接的锁机制, 具体由实现类决定;
 * <p>
 * 默认实现{@link DefaultConcurrentStateMachine}, 状态切换序列由CAS实现.
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.0
 * @since 2025/2/6
 * @see DefaultConcurrentStateMachine
 */
public interface ConcurrentStateMachine<S> extends StateMachine<S> {

    /**
     * 尝试使用CAS更新状态, 成功更新时触发状态处理器
     *
     * @param expectedValue 前置状态
     * @param newValue      更新的状态值
     * @return 成功更新返回真, 否则返回假
     */
    boolean compareAndSet(S expectedValue, S newValue);

    /**
     * 尝试使用CAS更新状态
     *
     * @param expectedValue     前置状态
     * @param newValue          更新的状态值
     * @param invokeHandlers    是否触发状态处理器, 仅在成功更新时才触发
     * @return 成功更新返回真, 否则返回假
     */
    boolean compareAndSet(S expectedValue, S newValue, boolean invokeHandlers);
}
