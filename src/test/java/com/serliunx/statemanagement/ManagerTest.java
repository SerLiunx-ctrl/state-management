package com.serliunx.statemanagement;

import com.serliunx.statemanagement.manager.BreakageUnidirectionalStateManager;
import com.serliunx.statemanagement.manager.DefaultUnidirectionalStateManager;
import com.serliunx.statemanagement.manager.UnidirectionalStateManager;
import com.serliunx.statemanagement.support.PrinterState;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * 状态管理器测试
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.0
 * @since 2024/12/28
 */
@Slf4j
public class ManagerTest {

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
