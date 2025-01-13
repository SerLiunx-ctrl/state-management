package com.serliunx.statemanagement;

import com.serliunx.statemanagement.machine.StateMachine;
import com.serliunx.statemanagement.machine.StateMachineBuilder;
import com.serliunx.statemanagement.support.PrinterState;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.Executors;

/**
 * 状态机测试
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.0
 * @since 2024/12/28
 */
@Slf4j
public class MachineTest {

	@Test
	public void testStandardStateMachine() throws Exception {
		StateMachine<PrinterState> stateMachine = StateMachineBuilder.from(PrinterState.values())
				.async()
				.standard()
				.executor(Executors.newFixedThreadPool(16))
				.whenLeave(PrinterState.IDLE, h -> {
					System.out.println(Thread.currentThread().getName() + ": leave IDLE");
				})
				.whenEntry(PrinterState.STOPPING, h -> {
					System.out.println(Thread.currentThread().getName() + ": entry STOPPING, from " + h.getFrom());
				})
				.whenEntry(PrinterState.STOPPED, h -> {
					System.out.println(Thread.currentThread().getName() + ": entry STOPPED, from " + h.getFrom());
				})
				.build();

		stateMachine.switchNext(false);
		stateMachine.close();
	}
}
