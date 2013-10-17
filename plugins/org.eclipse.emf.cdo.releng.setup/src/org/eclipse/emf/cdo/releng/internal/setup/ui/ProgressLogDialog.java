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
package org.eclipse.emf.cdo.releng.internal.setup.ui;

import org.eclipse.emf.cdo.releng.internal.setup.Activator;
import org.eclipse.emf.cdo.releng.internal.setup.SetupTaskPerformer;
import org.eclipse.emf.cdo.releng.setup.util.log.ProgressLog;
import org.eclipse.emf.cdo.releng.setup.util.log.ProgressLogProvider;
import org.eclipse.emf.cdo.releng.setup.util.log.ProgressLogRunnable;

import org.eclipse.net4j.util.io.IOUtil;

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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.progress.ProgressManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProgressLogDialog extends TitleAreaDialog implements ProgressLog
{
  public static final String TITLE = "Setup Development Environment";

  private static final SimpleDateFormat TIME = new SimpleDateFormat("HH:mm:ss");

  private static final SimpleDateFormat DATE_TIME = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  private PrintStream logStream;

  private Text text;

  private Button okButton;

  private Button cancelButton;

  private boolean cancelled;

  private String lastLine;

  private ProgressLogDialog(Shell parentShell, File logFile)
  {
    super(parentShell);
    if (logFile != null)
    {
      try
      {
        logFile.getParentFile().mkdirs();
        logStream = new PrintStream(new FileOutputStream(logFile, true));
      }
      catch (FileNotFoundException ex)
      {
        throw new RuntimeException(ex);
      }
    }

    setHelpAvailable(false);
    setShellStyle(SWT.BORDER | SWT.MAX | SWT.RESIZE | SWT.TITLE | SWT.APPLICATION_MODAL);
  }

  @Override
  protected Control createDialogArea(Composite parent)
  {
    setMessage("Please wait until the setup process is finished and the OK button is enabled...");
    setTitleImage(ResourceManager.getPluginImage("org.eclipse.emf.cdo.releng.setup", "icons/install_wiz.gif"));
    getShell().setText(TITLE);
    setTitle(TITLE);

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
    SetupTaskPerformer.setProgress(this);
    ProgressManager oldProgressProvider = ProgressManager.getInstance();
    ProgressLogProvider newProgressLogProvider = new ProgressLogProvider(this, oldProgressProvider);
    Job.getJobManager().setProgressProvider(newProgressLogProvider);
  }

  @Override
  public boolean close()
  {
    SetupTaskPerformer.setProgress(null);

    if (logStream != null)
    {
      logStream.println();
      logStream.println();
      logStream.println();
      logStream.println();
      IOUtil.closeSilent(logStream);
    }

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

  public void log(String line)
  {
    if (isCancelled())
    {
      throw new OperationCanceledException();
    }

    if (line == null || line.length() == 0 || Character.isLowerCase(line.charAt(0)) || line.equals("Updating")
        || line.startsWith("Scanning Git") || line.startsWith("Re-indexing") || line.endsWith(" remaining.")
        || line.startsWith("Calculating Decorations") || line.startsWith("Decorating") || line.startsWith("http://")
        || line.startsWith("The user operation is waiting") || line.startsWith("Git repository changed")
        || line.startsWith("Refreshing ") || line.startsWith("Opening ") || line.startsWith("Connecting project ")
        || line.startsWith("Searching for associated repositories.") || line.startsWith("Preparing type ")
        || line.startsWith("Loading project description") || line.startsWith("Generating cspec from PDE artifacts")
        || line.startsWith("Reporting encoding changes") || line.startsWith("Saving")
        || line.startsWith("Downloading software") || line.startsWith("Java indexing..."))
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

    lastLine = line;

    final String message = line + "\n";
    final Date date = new Date();

    if (logStream != null)
    {
      try
      {
        logStream.print("[" + DATE_TIME.format(date) + "] " + message);
        logStream.flush();
      }
      catch (Exception ex)
      {
        Activator.log(ex);
      }
    }

    asyncExec(new Runnable()
    {
      public void run()
      {
        try
        {
          text.append("[" + TIME.format(date) + "] " + message);
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
    Job.getJobManager().setProgressProvider(ProgressManager.getInstance());

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

  public static void run(Shell shell, File logFile, final String jobName, final ProgressLogRunnable runnable)
  {
    try
    {
      final boolean[] restart = { false };
      final ProgressLogDialog dialog = new ProgressLogDialog(shell, logFile);
      Runnable jobRunnable = new Runnable()
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
                dialog.log(jobName);
                restart[0] = runnable.run(dialog);
              }
              catch (Exception ex)
              {
                Activator.log(ex);
                dialog.log("An error occured: " + ex.getMessage());
                dialog.log("The Error Log contains more infos...");
              }
              finally
              {
                long seconds = (System.currentTimeMillis() - start) / 1000;
                dialog.log("Took " + seconds + " seconds.");
                dialog.log("Press OK to close the dialog" + (restart[0] ? " and restart Eclipse" : "") + "...");
                dialog.setFinished();
              }

              return Status.OK_STATUS;
            }
          };

          job.schedule();

          if (dialog.open() == ProgressLogDialog.OK && restart[0])
          {
            PlatformUI.getWorkbench().restart();
          }
        }
      };

      if (Display.getCurrent() == shell.getDisplay())
      {
        jobRunnable.run();
      }
      else
      {
        shell.getDisplay().asyncExec(jobRunnable);
      }
    }
    catch (Exception ex)
    {
      Activator.log(ex);
    }
  }
}
