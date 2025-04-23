package com.serliunx.statemanagement;

import com.serliunx.statemanagement.machine.ConcurrentStateMachine;
import com.serliunx.statemanagement.machine.StateMachine;
import com.serliunx.statemanagement.machine.StateMachineBuilder;
import com.serliunx.statemanagement.support.PrinterEvent;
import com.serliunx.statemanagement.support.PrinterState;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 状态机测试
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.0
 * @since 2024/12/28
 */
public class MachineTest {

	private static final Logger log = LoggerFactory.getLogger(MachineTest.class);

	private final ExecutorService executor = Executors.newFixedThreadPool(5);

	@Test
	public void testStandardStateMachine() throws Exception {
		StateMachine<PrinterState> stateMachine = StateMachineBuilder.from(PrinterState.values())
				.async(true)
				.standard()
				.withInitial(PrinterState.STOPPING)
				.build();

		log.info("{}", stateMachine.current());
	}

	@Test
	public void testConcurrentStateMachine() throws Exception {
		ConcurrentStateMachine<PrinterState> stateMachine = StateMachineBuilder.from(PrinterState.values())
				.async(false)
				.whenEntry(PrinterState.STOPPING, h -> {
					log.info("enter stopping~");
				})
				.whenHappened(PrinterEvent.TURN_OFF, l -> {
					if (l.switchTo(PrinterState.STOPPING))
						l.switchTo(PrinterState.STOPPED);
				})
				.withInitial(PrinterState.STOPPING)
				.concurrent()
				.build();

		log.info("{}", stateMachine.current());
		stateMachine.close();
	}

	@Test
	public void testConcurrentStateMachine2() throws Exception {
		ConcurrentStateMachine<PrinterState> stateMachine = StateMachineBuilder.from(PrinterState.values())
				.async(false)
				.concurrent()
				.whenEntry(PrinterState.STOPPING, h -> {
					log.info("stopping...");
				})
				.build();

		for (int i = 0; i < 5; i++) {
			executor.execute(() -> {
				log.info("{}", stateMachine.compareAndSet(PrinterState.IDLE, PrinterState.STOPPING, true));
			});
		}

		TimeUnit.SECONDS.sleep(5);
	}
}
