/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup.dialogs;

import org.eclipse.emf.cdo.releng.setup.helper.Progress;
import org.eclipse.emf.cdo.releng.setup.helper.ProgressLog;
import org.eclipse.emf.cdo.releng.setup.helper.ProgressLogRunnable;
import org.eclipse.emf.cdo.releng.setup.presentation.SetupEditorPlugin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ProgressLogDialog extends TitleAreaDialog implements ProgressLog
{
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");

  private Text text;

  private Button okButton;

  private Button cancelButton;

  private boolean cancelled;

  private String lastLine;

  public ProgressLogDialog(Shell parentShell)
  {
    super(parentShell);
    setShellStyle(SWT.BORDER | SWT.MAX | SWT.RESIZE | SWT.TITLE | SWT.APPLICATION_MODAL);
  }

  @Override
  protected Control createDialogArea(Composite parent)
  {
    setTitle("Log");
    Composite area = (Composite)super.createDialogArea(parent);
    Composite container = new Composite(area, SWT.NONE);
    GridLayout gl_container = new GridLayout(1, false);
    gl_container.marginWidth = 10;
    gl_container.marginHeight = 10;
    container.setLayout(gl_container);
    container.setLayoutData(new GridData(GridData.FILL_BOTH));

    text = new Text(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
    text.setFont(SWTResourceManager.getFont("Courier New", 10, SWT.NORMAL));
    text.setEditable(false);
    text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
    text.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

    return area;
  }

  @Override
  protected void createButtonsForButtonBar(Composite parent)
  {
    okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    okButton.setEnabled(false);

    cancelButton = createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    cancelButton.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        cancelled = true;
        setFinished();
      }
    });
  }

  @Override
  public synchronized void create()
  {
    super.create();
    Progress.set(this);
  }

  @Override
  public boolean close()
  {
    Progress.set(null);
    return super.close();
  }

  /**
   * Return the initial size of the dialog.
   */
  @Override
  protected Point getInitialSize()
  {
    return new Point(1000, 600);
  }

  public boolean isCancelled()
  {
    return cancelled;
  }

  public void addLine(String line)
  {
    if (isCancelled())
    {
      throw new OperationCanceledException();
    }

    if (line == null || line.length() == 0 || line.startsWith("Scanning Git") || line.startsWith("Re-indexing (fully)")
        || line.endsWith(" remaining.") || line.startsWith("Calculating Decorations") || line.startsWith("Decorating ")
        || line.startsWith("http://") || line.startsWith("The user operation is waiting"))
    {
      return;
    }

    if (line.endsWith("/s)"))
    {
      int index = line.lastIndexOf(" (");
      if (index != -1)
      {
        line = line.substring(0, index);
      }
    }

    if (line.equals(lastLine))
    {
      return;
    }

    final String message = line;
    lastLine = message;

    asyncExec(new Runnable()
    {
      public void run()
      {
        try
        {
          text.append("[" + DATE_FORMAT.format(new Date()) + "] " + message + "\n");
        }
        catch (Exception ex)
        {
          //$FALL-THROUGH$
        }
      }
    });
  }

  public void setFinished()
  {
    asyncExec(new Runnable()
    {
      public void run()
      {
        try
        {
          okButton.setEnabled(true);
          cancelButton.setEnabled(false);
        }
        catch (Exception ex)
        {
          //$FALL-THROUGH$
        }
      }
    });
  }

  private void asyncExec(Runnable runnable)
  {
    try
    {
      getShell().getDisplay().asyncExec(runnable);
    }
    catch (NullPointerException ex)
    {
      //$FALL-THROUGH$
    }
    catch (SWTException ex)
    {
      if (ex.code != SWT.ERROR_WIDGET_DISPOSED)
      {
        throw ex;
      }

      //$FALL-THROUGH$
    }
  }

  public static void run(Shell shell, final String jobName, final ProgressLogRunnable runnable)
  {
    try
    {
      final ProgressLogDialog dialog = new ProgressLogDialog(shell);
      // dialog.setBlockOnOpen(false);

      shell.getDisplay().asyncExec(new Runnable()
      {
        public void run()
        {
          Job job = new Job(jobName)
          {
            @Override
            protected IStatus run(IProgressMonitor monitor)
            {
              long start = System.currentTimeMillis();

              try
              {
                dialog.addLine(jobName);
                runnable.run(dialog);
              }
              catch (Exception ex)
              {
                SetupEditorPlugin.getPlugin().log(ex);
                dialog.addLine("An error occured: " + ex.getMessage());
                dialog.addLine("The Error Log contains more infos...");
              }
              finally
              {
                long seconds = (System.currentTimeMillis() - start) / 1000;
                dialog.addLine("Took " + seconds + " seconds.");
                dialog.addLine("Press OK to close the dialog...");
                dialog.setFinished();
              }

              return Status.OK_STATUS;
            }
          };

          job.schedule();
        }
      });

      dialog.open();
    }
    catch (Exception ex)
    {
      SetupEditorPlugin.getPlugin().log(ex);
    }
  }
}
