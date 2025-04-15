package com.serliunx.statemanagement;

import com.serliunx.statemanagement.machine.ConcurrentStateMachine;
import com.serliunx.statemanagement.machine.StateMachine;
import com.serliunx.statemanagement.machine.StateMachineBuilder;
import com.serliunx.statemanagement.support.PrinterEvent;
import com.serliunx.statemanagement.support.PrinterState;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 状态机测试
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.0
 * @since 2024/12/28
 */
public class MachineTest {

	private final ExecutorService executor = Executors.newFixedThreadPool(5);

	@Test
	public void testStandardStateMachine() throws Exception {
		StateMachine<PrinterState> stateMachine = StateMachineBuilder.from(PrinterState.values())
				.async(true)
				.standard()
				.withInitial(PrinterState.STOPPING)
				.build();

		System.out.println(stateMachine.current());
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
				.withInitial(PrinterState.STOPPING)
				.concurrent()
				.build();

//		stateMachine.publish(PrinterEvent.TURN_OFF);
		System.out.println(stateMachine.current());
		stateMachine.close();
	}

	@Test
	public void testConcurrentStateMachine2() throws Exception {
		ConcurrentStateMachine<PrinterState> stateMachine = StateMachineBuilder.from(PrinterState.values())
				.async(false)
				.concurrent()
				.whenEntry(PrinterState.STOPPING, h -> {
					System.out.println("entering stopping...");
				})
				.build();

		for (int i = 0; i < 5; i++) {
			executor.execute(() -> {
				System.out.println(stateMachine.compareAndSet(PrinterState.IDLE, PrinterState.STOPPING, true));
			});
		}

	}
}
