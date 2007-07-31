/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.ui.widgets;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.om.monitor.IMessageHandler;
import org.eclipse.net4j.util.om.monitor.MonitorUtil;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Eike Stepper
 */
public class MonitorDialog extends ProgressMonitorDialog
{
  public static final int DEFAULT_SHELL_STYLE = SWT.SHELL_TRIM;

  private String title;

  private Exception exception;

  private StringBuilder log = new StringBuilder();

  private IMessageHandler logHandler = new IMessageHandler()
  {
    public void handleMessage(String msg, int level)
    {
      log.append(msg);
      log.append("\n");
    }
  };

  private IDialogSettings settings;

  public MonitorDialog(Shell parentShell, int shellStyle, String title, IDialogSettings settings)
  {
    super(parentShell);
    setShellStyle(shellStyle);
    this.title = title;
    this.settings = settings;
  }

  public MonitorDialog(Shell parentShell, String title, IDialogSettings settings)
  {
    this(parentShell, DEFAULT_SHELL_STYLE, title, settings);
  }

  public void run(boolean fork, boolean cancelable, final Runnable runnable)
  {
    try
    {
      super.run(fork, cancelable, new IRunnableWithProgress()
      {
        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
        {
          MonitorUtil.Eclipse.startMonitoring(monitor, logHandler);
          try
          {
            runnable.run();
          }
          catch (RuntimeException ex)
          {
            exception = WrappedException.unwrap(ex);
            log.append(exception.getMessage());
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            exception.printStackTrace(new PrintStream(bytes));
            log.append(bytes);
          }
          finally
          {
            MonitorUtil.Eclipse.stopMonitoring();
          }
        }
      });
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  @Override
  protected void finishedRun()
  {
    super.finishedRun();
    if (exception != null)
    {
      new LogDialog(getShell(), getShellStyle(), "Aborted " + title, "An error occured. See the log for details.", log
          .toString(), settings).open();
    }
    else if (log.length() != 0)
    {
      new LogDialog(getShell(), getShellStyle(), "Finished " + title,
          "The operation finished successfully. See the log for details.", log.toString(), settings).open();
    }
    else
    {
      MessageDialog.openInformation(getShell(), "Finished " + title, "The operation finished successfully.");
    }
  }

  @Override
  @Deprecated
  public final void run(boolean fork, boolean cancelable, IRunnableWithProgress runnable)
      throws InvocationTargetException, InterruptedException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void configureShell(Shell newShell)
  {
    super.configureShell(newShell);
    newShell.setText(title);
  }

  @Override
  protected IDialogSettings getDialogBoundsSettings()
  {
    if (settings == null)
    {
      return null;
    }

    IDialogSettings section = settings.getSection(title);
    if (section == null)
    {
      section = settings.addNewSection(title);
    }

    return section;
  }
}
