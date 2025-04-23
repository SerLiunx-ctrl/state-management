package com.serliunx.statemanagement;

import com.serliunx.statemanagement.manager.BreakageUnidirectionalStateManager;
import com.serliunx.statemanagement.manager.DefaultUnidirectionalStateManager;
import com.serliunx.statemanagement.manager.UnidirectionalStateManager;
import com.serliunx.statemanagement.support.PrinterState;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 状态管理器测试
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.0
 * @since 2024/12/28
 */
public class ManagerTest {

	private static final Logger log = LoggerFactory.getLogger(ManagerTest.class);

	@Test
	public void testUnidirectionalStateManager() {
		UnidirectionalStateManager<PrinterState> unidirectionalStateManager =
				new DefaultUnidirectionalStateManager<>(PrinterState.values());

		System.out.println(unidirectionalStateManager.switchTo(PrinterState.IDLE));
		System.out.println(unidirectionalStateManager.switchTo(PrinterState.SCANNING));
	}

	@Test
	public void testBreakageUnidirectionalStateManager() {
		UnidirectionalStateManager<PrinterState> bum = new BreakageUnidirectionalStateManager<>(PrinterState.values());

		while (bum.isSwitchable()) {
			System.out.println(bum.getAndSwitchNext());
		}
		System.out.println(bum.current());
	}
}
