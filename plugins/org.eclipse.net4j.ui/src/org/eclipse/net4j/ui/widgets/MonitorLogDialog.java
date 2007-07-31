package org.eclipse.net4j.ui.widgets;

import org.eclipse.net4j.util.om.monitor.OMMonitorHandler;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Eike Stepper
 */
public final class MonitorLogDialog extends LogDialog implements OMMonitorHandler
{
  private TextStyle normal;

  private TextStyle blue;

  public MonitorLogDialog(Shell parentShell, int shellStyle, String title, String message, IDialogSettings settings)
  {
    super(parentShell, shellStyle, title, message, settings);
    normal = new TextStyle(null, Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY), null);
    blue = new TextStyle(null, Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE), null);
  }

  public MonitorLogDialog(Shell parentShell, String title, String message, IDialogSettings settings)
  {
    this(parentShell, DEFAULT_SHELL_STYLE, title, message, settings);
  }

  public void handleTask(String task, int level)
  {
    setTextStyle(blue);
    append(task);
    append("\n");
  }

  public void handleMessage(String msg, int level)
  {
    setTextStyle(normal);
    append(msg);
    append("\n");
  }
}