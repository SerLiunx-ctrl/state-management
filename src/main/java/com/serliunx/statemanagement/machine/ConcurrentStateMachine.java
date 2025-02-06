package com.serliunx.statemanagement.machine;

/**
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.0
 * @since 2025/2/6
 */
public interface ConcurrentStateMachine<S> extends StateMachine<S> {

    boolean compareAndSet(S expectedValue, S newValue);
}
