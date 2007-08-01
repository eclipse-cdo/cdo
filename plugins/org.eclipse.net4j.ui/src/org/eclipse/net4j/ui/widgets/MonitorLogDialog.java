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
  private String[] emphasizePrefix;

  private TextStyle taskStyle;

  private TextStyle normalStyle;

  private TextStyle emphasizedStyle;

  private TextStyle problemStyle;

  public MonitorLogDialog(Shell parentShell, int shellStyle, String title, String message, IDialogSettings settings)
  {
    super(parentShell, shellStyle, title, message, settings);
    Display display = Display.getCurrent();
    taskStyle = new TextStyle(null, display.getSystemColor(SWT.COLOR_BLACK), null);
    normalStyle = new TextStyle(null, display.getSystemColor(SWT.COLOR_DARK_GRAY), null);
    emphasizedStyle = new TextStyle(null, display.getSystemColor(SWT.COLOR_BLUE), null);
    problemStyle = new TextStyle(null, display.getSystemColor(SWT.COLOR_RED), null);
  }

  public MonitorLogDialog(Shell parentShell, String title, String message, IDialogSettings settings)
  {
    this(parentShell, DEFAULT_SHELL_STYLE, title, message, settings);
  }

  public String[] getEmphasizePrefix()
  {
    return emphasizePrefix;
  }

  public void setEmphasizePrefix(String... emphasizePrefix)
  {
    this.emphasizePrefix = emphasizePrefix;
  }

  public TextStyle getNormalStyle()
  {
    return normalStyle;
  }

  public TextStyle getTaskStyle()
  {
    return taskStyle;
  }

  public TextStyle getProblemStyle()
  {
    return problemStyle;
  }

  public TextStyle getEmphasizedStyle()
  {
    return emphasizedStyle;
  }

  public void handleTask(String task, int level)
  {
    setTextStyle(taskStyle);
    append(task);
    append("\n");
  }

  public void handleMessage(String msg, int level)
  {
    boolean emphasized = false;
    if (emphasizePrefix != null)
    {
      for (String prefix : emphasizePrefix)
      {
        if (msg.startsWith(prefix))
        {
          setTextStyle(emphasizedStyle);
          emphasized = true;
          break;
        }
      }
    }

    if (!emphasized)
    {
      setTextStyle(normalStyle);
    }

    append(msg);
    append("\n");
  }

  @Override
  public void append(Throwable t)
  {
    setTextStyle(problemStyle);
    super.append(t);
  }
}