package com.github.eclipse.yatb;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.RuntimeProcess;
import org.eclipse.debug.internal.ui.views.console.ProcessConsole;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsolePageParticipant;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.IPageSite;

public class ConsoleActions implements IConsolePageParticipant {

	private IPageBookViewPage page;
	private Action terminateHardAction;
	private Action terminateAllHardAction;
	private Action terminateSoftAction;
	private Action terminateAllSoftAction;
	private IActionBars bars;
	private IConsole console;

	@Override
	public void init(final IPageBookViewPage page, final IConsole console) {
		this.console = console;
		this.page = page;
		IPageSite site = page.getSite();
		this.bars = site.getActionBars();

		terminateHardAction = createTerminateHardButton();
	    terminateAllHardAction = createTerminateAllHardButton();
	    terminateSoftAction = createTerminateSoftButton();
	    terminateAllSoftAction = createTerminateAllSoftButton();

		bars.getMenuManager().add(new Separator());

		IToolBarManager toolbarManager = bars.getToolBarManager();

		toolbarManager.appendToGroup(IConsoleConstants.LAUNCH_GROUP, terminateHardAction);
		toolbarManager.appendToGroup(IConsoleConstants.LAUNCH_GROUP, terminateAllHardAction);
		toolbarManager.appendToGroup(IConsoleConstants.LAUNCH_GROUP, terminateSoftAction);
		toolbarManager.appendToGroup(IConsoleConstants.LAUNCH_GROUP, terminateAllSoftAction);

		bars.updateActionBars();
	}

	private Action createTerminateHardButton() {
    ImageDescriptor imageDescriptor = ImageDescriptor.createFromFile(getClass(), "/icons/terminate_hard.gif");
    return new Action("Kill Process", imageDescriptor) {
      @Override
      public void run() {
        if (console instanceof ProcessConsole) {
          RuntimeProcess runtimeProcess = (RuntimeProcess) ((ProcessConsole) console)
              .getAttribute(IDebugUIConstants.ATTR_CONSOLE_PROCESS);
          ILaunch launch = runtimeProcess.getLaunch();
          stopProcess(launch, true);
        }
      }
    };
  }

  private Action createTerminateAllHardButton() {
    ImageDescriptor imageDescriptor = ImageDescriptor.createFromFile(getClass(), "/icons/terminate_all_hard.gif");
    return new Action("Kill All Processes", imageDescriptor) {
      @Override
      public void run() {
        ILaunch[] launches = DebugPlugin.getDefault().getLaunchManager().getLaunches();
        for (ILaunch launch : launches) {
          stopProcess(launch, true);
        }
      }
    };
  }
  
  private Action createTerminateSoftButton() {
    ImageDescriptor imageDescriptor = ImageDescriptor.createFromFile(getClass(), "/icons/terminate_soft.gif");
    return new Action("Request Shutdown from Process", imageDescriptor) {
      @Override
      public void run() {
        if (console instanceof ProcessConsole) {
          RuntimeProcess runtimeProcess = (RuntimeProcess) ((ProcessConsole) console)
              .getAttribute(IDebugUIConstants.ATTR_CONSOLE_PROCESS);
          ILaunch launch = runtimeProcess.getLaunch();
          stopProcess(launch, false);
        }
      }
    };
  }

  private Action createTerminateAllSoftButton() {
    ImageDescriptor imageDescriptor = ImageDescriptor.createFromFile(getClass(), "/icons/terminate_all_soft.gif");
    return new Action("Request Shutdown from all Processes", imageDescriptor) {
      @Override
      public void run() {
        ILaunch[] launches = DebugPlugin.getDefault().getLaunchManager().getLaunches();
        for (ILaunch launch : launches) {
          stopProcess(launch, false);
        }
      }
    };
  }

	private void stopProcess(ILaunch launch, boolean hard) {
		if (launch != null && !launch.isTerminated()) {
			try {
				if (Platform.OS_WIN32.equals(Platform.getOS())) {
					launch.terminate();
				} else {
					for (IProcess p : launch.getProcesses()) {
						try {
							Method m = p.getClass().getDeclaredMethod("getSystemProcess");
							m.setAccessible(true);
							Process proc = (Process) m.invoke(p);

							Field f = proc.getClass().getDeclaredField("pid");
							f.setAccessible(true);
							int pid = (int) f.get(proc);

							Runtime rt = Runtime.getRuntime();
							if (hard) rt.exec("kill -SIGKILL " + pid);
							else      rt.exec("kill -SIGTERM " + pid);
						} catch (Exception ex) {
							Activator.log(ex);
						}
					}
				}
			} catch (DebugException e) {
				Activator.log(e);
			}
		}
	}

	@Override
	public void dispose() {
		terminateHardAction = null;
		terminateAllHardAction = null;
		terminateSoftAction = null;
		terminateAllSoftAction = null;
		bars = null;
		page = null;
	}

	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}

	@Override
	public void activated() {
		updateVis();
	}

	@Override
	public void deactivated() {
		updateVis();
	}

	private void updateVis() {
		if (page == null)
			return;
		terminateHardAction.setEnabled(true);
		terminateAllHardAction.setEnabled(true);
		terminateSoftAction.setEnabled(true);
		terminateAllSoftAction.setEnabled(true);
		bars.updateActionBars();
	}

}