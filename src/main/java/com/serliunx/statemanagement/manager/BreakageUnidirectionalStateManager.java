package com.serliunx.statemanagement.manager;

import com.serliunx.statemanagement.exception.StateException;

import java.util.List;

/**
 * 断路的单向状态管理器
 * <p> 逻辑与{@link UnidirectionalStateManager}大体相同, 不同的点在于:
 * <li> 最后一个状态无法转向第一个状态, 即为一次性的状态管理器.
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.0
 * @since 2025/1/13
 */
public final class BreakageUnidirectionalStateManager<S> extends DefaultUnidirectionalStateManager<S> {

    /**
     * 是否在切换失败时抛出异常
     */
    private final boolean allowThrow;

    public BreakageUnidirectionalStateManager(List<S> stateList, boolean allowThrow) {
        super(stateList);
        this.allowThrow = allowThrow;
    }

    public BreakageUnidirectionalStateManager(S[] states, boolean allowThrow) {
        super(states);
        this.allowThrow = allowThrow;
    }

    public BreakageUnidirectionalStateManager(List<S> stateList) {
        this(stateList, true);
    }

    public BreakageUnidirectionalStateManager(S[] states) {
        this(states, true);
    }

    @Override
    public S switchNextAndGet() {
        if (isLast()) {
            if (allowThrow)
                throw new StateException("The last state has been reached and cannot be switched again!");
            return null;
        }
        return super.switchNextAndGet();
    }

    @Override
    public S getAndSwitchNext() {
        if (isLast()) {
            if (allowThrow)
                throw new StateException("The last state has been reached and cannot be switched again!");
            return null;
        }
        return super.getAndSwitchNext();
    }

    @Override
    public void switchNext() {
        if (isLast()) {
            if (allowThrow)
                throw new StateException("The last state has been reached and cannot be switched again!");
            return;
        }
        super.switchNext();
    }

    @Override
    public boolean switchTo(S state) {
        if (isLast()) {
            if (allowThrow)
                throw new StateException("The last state has been reached and cannot be switched again!");
            return false;
        }
        return super.switchTo(state);
    }

    @Override
    public boolean isCircle() {
        return false;
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException("Cannot reset state for BreakageUnidirectionalStateManager!");
    }

    @Override
    public boolean isSwitchable() {
        return !isLast();
    }
}
