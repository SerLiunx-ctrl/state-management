package com.serliunx.statemanagement;

import com.serliunx.statemanagement.machine.ConcurrentStateMachine;
import com.serliunx.statemanagement.machine.StateMachine;
import com.serliunx.statemanagement.machine.StateMachineBuilder;
import com.serliunx.statemanagement.support.PrinterEvent;
import com.serliunx.statemanagement.support.PrinterState;
import org.junit.Test;

import java.util.concurrent.Executors;

/**
 * 状态机测试
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.0
 * @since 2024/12/28
 */
public class MachineTest {

	@Test
	public void testStandardStateMachine() throws Exception {
		StateMachine<PrinterState> stateMachine = StateMachineBuilder.from(PrinterState.values())
				.async(false)
				.standard()
				.exchange(PrinterState.IDLE, PrinterState.SCANNING, h -> {
					System.out.println("hello~");
				})
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

		stateMachine.switchTo(PrinterState.SCANNING);
	}

	@Test
	public void testConcurrentStateMachine() throws Exception {
		ConcurrentStateMachine<PrinterState> stateMachine = StateMachineBuilder.from(PrinterState.values())
				.async()
				.whenEntry(PrinterState.STOPPING, h -> {
					System.out.println(1111);
				})
				.whenHappened(PrinterEvent.TURN_OFF, l -> {
					if (l.switchTo(PrinterState.STOPPING))
						l.switchTo(PrinterState.STOPPED);
				})
				.concurrent()
				.build();

		stateMachine.publish(PrinterEvent.TURN_OFF);

		stateMachine.close();
	}
}
