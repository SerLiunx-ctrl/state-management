package com.serliunx.statemanagement;

import com.serliunx.statemanagement.machine.StateMachine;
import com.serliunx.statemanagement.machine.StateMachineBuilder;
import com.serliunx.statemanagement.support.PrinterEvent;
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
				.async(false)
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
				.whenHappened(PrinterEvent.TURN_ON, m -> {
					m.switchTo(PrinterState.SCANNING);
				})
				.whenHappened(PrinterEvent.TURN_OFF, m -> {
					if (m.switchTo(PrinterState.STOPPING))
						m.switchTo(PrinterState.STOPPED);
				})
				.build();

		stateMachine.publish(PrinterEvent.TURN_ON);

		stateMachine.close();
	}
}
