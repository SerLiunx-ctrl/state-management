package com.serliunx.statemanagement;

import com.serliunx.statemanagement.machine.ConcurrentStateMachine;
import com.serliunx.statemanagement.machine.StateMachine;
import com.serliunx.statemanagement.machine.support.StateMachines;
import com.serliunx.statemanagement.support.PrinterState;
import org.junit.Test;

/**
 * 状态机工具类测试
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @since 2025/4/11
 */
public class StateMachinesTest {

    @Test
    public void testConcurrentStateMachines() throws Exception {
        ConcurrentStateMachine<PrinterState> machine = StateMachines.concurrentStateMachine(PrinterState.values());
        System.out.println(machine.current());
        System.out.println(machine.switchPrevAndGet());
        System.out.println(machine.current());
        machine.close();
    }

    @Test
    public void testStateMachines() throws Exception {
        StateMachine<PrinterState> machine = StateMachines.defaultStateMachine(PrinterState.values());
        System.out.println(machine.current());
        System.out.println(machine.switchNextAndGet());
        System.out.println(machine.current());
        machine.close();
    }
}
