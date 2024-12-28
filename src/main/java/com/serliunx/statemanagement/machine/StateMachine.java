package com.serliunx.statemanagement.machine;

import com.serliunx.statemanagement.manager.BidirectionalStateManager;

/**
 * 状态机定义
 * <p>
 * 基于双向的状态管理器扩展 {@link BidirectionalStateManager}, 切换逻辑依赖于内置的状态管理器；
 * 同时可以多种监听事件, 包括:
 * <li> 切换至指定状态时触发	(进入事件)
 * <li> 切出指定状态时触发	(离开事件)
 * <li> 从A切换到B状态时触发	(交换事件)
 * <p>
 * 推荐使用{@link StateMachineBuilder} 来构建状态机.
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.0
 * @since 2024/12/28
 * @see StateMachineBuilder
 * @see BidirectionalStateManager
 * @see com.serliunx.statemanagement.manager.StateManager
 */
public interface StateMachine<S> extends BidirectionalStateManager<S> {


}
