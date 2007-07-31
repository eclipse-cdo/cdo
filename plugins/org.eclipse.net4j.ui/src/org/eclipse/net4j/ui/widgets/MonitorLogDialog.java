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
public class MonitorLogDialog extends LogDialog implements OMMonitorHandler
{
  private String emphasizePrefix;

  private TextStyle normal;

  private TextStyle blue;

  private TextStyle red;

  private TextStyle green;

  public MonitorLogDialog(Shell parentShell, int shellStyle, String title, String message, IDialogSettings settings)
  {
    super(parentShell, shellStyle, title, message, settings);
    Display display = Display.getCurrent();
    normal = new TextStyle(null, display.getSystemColor(SWT.COLOR_DARK_GRAY), null);
    blue = new TextStyle(null, display.getSystemColor(SWT.COLOR_BLUE), null);
    red = new TextStyle(null, display.getSystemColor(SWT.COLOR_RED), null);
    green = new TextStyle(null, display.getSystemColor(SWT.COLOR_GREEN), null);
  }

  public MonitorLogDialog(Shell parentShell, String title, String message, IDialogSettings settings)
  {
    this(parentShell, DEFAULT_SHELL_STYLE, title, message, settings);
  }

  public String getEmphasizePrefix()
  {
    return emphasizePrefix;
  }

  public void setEmphasizePrefix(String emphasizePrefix)
  {
    this.emphasizePrefix = emphasizePrefix;
  }

  public TextStyle getNormal()
  {
    return normal;
  }

  public TextStyle getBlue()
  {
    return blue;
  }

  public TextStyle getRed()
  {
    return red;
  }

  public TextStyle getGreen()
  {
    return green;
  }

  public void handleTask(String task, int level)
  {
    setTextStyle(blue);
    append(task);
    append("\n");
  }

  public void handleMessage(String msg, int level)
  {
    if (msg.startsWith(emphasizePrefix))
    {
      setTextStyle(green);
    }
    else
    {
      setTextStyle(normal);
    }

    append(msg);
    append("\n");
  }

  @Override
  public void append(Throwable t)
  {
    setTextStyle(red);
    super.append(t);
  }
}