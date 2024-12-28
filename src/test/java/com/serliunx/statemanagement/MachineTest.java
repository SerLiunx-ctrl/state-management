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
	public void testStandardStateMachine() {
		StateMachine<PrinterState> stateMachine = StateMachineBuilder.from(PrinterState.class)
				.async(false)
				.whenLeave(PrinterState.PRINTING, h -> {
					System.out.println(Thread.currentThread().getName() + ": leave PRINTING");
				})
				.states(PrinterState.values())
				.executor(Executors.newFixedThreadPool(16))
				.whenEntry(PrinterState.SCANNING, h -> {
					System.out.println(Thread.currentThread().getName() + ": " + h.getFrom() + " >>> " + h.getTo());
				}, false)
				.whenEntry(PrinterState.PRINTING, h -> {
					System.out.println(Thread.currentThread().getName() + ": " + h.getFrom() + " >>> " + h.getTo());
				}, false, Executors.newFixedThreadPool(1))
				.exchange(PrinterState.STOPPED, PrinterState.IDLE, h -> {
					System.out.println(Thread.currentThread().getName() + ": " + h.getFrom() + " >>> " + h.getTo());
				})
				.build();
	}
}
