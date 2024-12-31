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
				.whenLeave(PrinterState.PRINTING, h -> {
					System.out.println(Thread.currentThread().getName() + ": leave PRINTING");
				})
				.whenEntry(PrinterState.STOPPING, h -> {
					System.out.println(Thread.currentThread().getName() + ": entry STOPPING, from " + h.getFrom());
				})
				.whenEntry(PrinterState.STOPPED, h -> {
					System.out.println(Thread.currentThread().getName() + ": entry STOPPED, from " + h.getFrom());
				})
				.build();

		System.out.println(stateMachine.getClass());

		stateMachine.switchTo(PrinterState.PRINTING);
		stateMachine.switchNext();
		stateMachine.switchNext();

		System.out.println(stateMachine.current());

		stateMachine.close();
	}
}
