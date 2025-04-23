package com.serliunx.statemanagement;

import com.serliunx.statemanagement.machine.ConcurrentStateMachine;
import com.serliunx.statemanagement.machine.StateMachine;
import com.serliunx.statemanagement.machine.support.StateMachines;
import com.serliunx.statemanagement.support.PrinterState;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 状态机工具类测试
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @since 2025/4/11
 */
public class StateMachinesTest {

    private static final Logger log = LoggerFactory.getLogger(StateMachinesTest.class);

    @Test
    public void testConcurrentStateMachines() throws Exception {
        ConcurrentStateMachine<PrinterState> machine = StateMachines.concurrentStateMachine(PrinterState.values());
        log.info("{}", machine.current());
        log.info("{}", machine.switchPrevAndGet());
        log.info("{}", machine.current());
        machine.close();
    }

    @Test
    public void testStateMachines() throws Exception {
        StateMachine<PrinterState> machine = StateMachines.defaultStateMachine(PrinterState.values());
        log.info("{}", machine.current());
        log.info("{}", machine.switchPrevAndGet());
        log.info("{}", machine.current());
        machine.close();
    }
}
