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
import org.eclipse.emf.cdo.releng.setup.Branch;
import org.eclipse.emf.cdo.releng.setup.ContextVariableTask;
import org.eclipse.emf.cdo.releng.setup.Setup;
import org.eclipse.emf.cdo.releng.setup.SetupConstants;
import org.eclipse.emf.cdo.releng.setup.SetupTask;
import org.eclipse.emf.cdo.releng.setup.util.EMFUtil;
import org.eclipse.emf.cdo.releng.setup.util.UIUtil;
import org.eclipse.emf.cdo.releng.setup.util.log.ProgressLog;
import org.eclipse.emf.cdo.releng.setup.util.log.ProgressLogFilter;
import org.eclipse.emf.cdo.releng.setup.util.log.ProgressLogProvider;
import org.eclipse.emf.cdo.releng.setup.util.log.ProgressLogRunnable;

import org.eclipse.net4j.util.ObjectUtil;

import org.eclipse.emf.common.ui.viewer.ColumnViewerInformationControlToolTipSupport;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.IItemFontProvider;
import org.eclipse.emf.edit.provider.ItemProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.ExtendedFontRegistry;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.progress.ProgressManager;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Eike Stepper
 */
public class ProgressDialog extends AbstractSetupDialog implements ProgressLog
{
  public static final SimpleDateFormat TIME = new SimpleDateFormat("HH:mm:ss");

  public static final SimpleDateFormat DATE_TIME = new SimpleDateFormat("yyyy-MM-dd " + TIME.toPattern());

  private static final String JOB_NAME = "Setting up IDE";

  private Text text;

  private ScrollBarListener scrollBarListener = new ScrollBarListener();

  private Button okButton;

  private Button cancelButton;

  private boolean cancelled;

  private ProgressLogFilter logFilter = new ProgressLogFilter();

  private List<SetupTaskPerformer> setupTaskPerformers;

  private SetupTask currentTask;

  private SetupTaskPerformer currentPerformer;

  private Map<SetupTask, Point> setupTaskSelections = new HashMap<SetupTask, Point>();

  private TreeViewer treeViewer;

  private ISelectionChangedListener treeViewerSelectionChangedListener = new ISelectionChangedListener()
  {
    public void selectionChanged(SelectionChangedEvent event)
    {
      IStructuredSelection selection = (IStructuredSelection)event.getSelection();
      Object element = selection.getFirstElement();
      if (element instanceof EObject)
      {
        for (EObject eObject = (EObject)element; eObject != null; eObject = eObject.eContainer())
        {
          if (eObject != currentTask && eObject instanceof SetupTask)
          {
            Point textSelection = setupTaskSelections.get(eObject);
            if (textSelection != null)
            {
              // Force the first line to be scrolled into view
              text.setSelection(textSelection.x, textSelection.x);

              // Treat -1 so that at selects to the very end.
              //
              int end = textSelection.y;
              if (end == -1)
              {
                end = text.getCharCount();
              }

              // Determine the number of lines of text to be selected
              String selectedText = text.getText(textSelection.x, end);
              int lineFeedCount = 0;
              int carriageReturnCount = 0;
              for (int i = 0, length = selectedText.length(); i < length; ++i)
              {
                char c = selectedText.charAt(i);
                if (c == '\n')
                {
                  ++lineFeedCount;
                }
                else if (c == '\r')
                {
                  ++carriageReturnCount;
                }
              }

              // If the number of visible lines is greater than the number of lines in the selection, invert the
              // selection range to scroll the top line into view.
              int visibleLineCount = text.getClientArea().height / text.getLineHeight();
              if (lineFeedCount > visibleLineCount || carriageReturnCount > visibleLineCount)
              {
                text.setSelection(end, textSelection.x);
              }
              else
              {
                text.setSelection(textSelection.x, end);
              }
            }
          }
        }
      }
    }
  };

  private ProgressDialog(Shell parentShell, List<SetupTaskPerformer> setupTaskPerformers)
  {
    super(parentShell, getTitle(setupTaskPerformers), 1000, 600);
    this.setupTaskPerformers = setupTaskPerformers;
  }

  @Override
  protected String getDefaultMessage()
  {
    return "Please wait until the " + (SetupConstants.SETUP_IDE ? "setup" : "install")
        + " process is finished and the OK button is enabled...";
  }

  @Override
  protected void createUI(Composite parent)
  {
    SashForm sashForm = new SashForm(parent, SWT.VERTICAL);
    sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));

    treeViewer = new TreeViewer(sashForm, SWT.NONE);
    Tree tree = treeViewer.getTree();

    ILabelProvider labelProvider = createLabelProvider();
    treeViewer.setLabelProvider(labelProvider);

    ItemProvider input = new ItemProvider();
    final EList<Object> children = input.getChildren();
    final AdapterFactoryContentProvider contentProvider = new AdapterFactoryContentProvider(EMFUtil.ADAPTER_FACTORY)
    {
      @Override
      public Object getParent(Object object)
      {
        if (object instanceof SetupTask)
        {
          for (Object child : children)
          {
            for (Object grandChild : ((ItemProvider)child).getChildren())
            {
              if (grandChild == object)
              {
                return child;
              }
            }
          }
        }

        return super.getParent(object);
      }
    };

    treeViewer.setContentProvider(contentProvider);
    treeViewer.addSelectionChangedListener(treeViewerSelectionChangedListener);

    new ColumnViewerInformationControlToolTipSupport(treeViewer, new LocationListener()
    {
      public void changing(LocationEvent event)
      {
      }

      public void changed(LocationEvent event)
      {
      }
    });

    for (SetupTaskPerformer setupTaskPerformer : setupTaskPerformers)
    {
      final EList<SetupTask> triggeredSetupTasks = setupTaskPerformer.getTriggeredSetupTasks();
      Setup setup = setupTaskPerformer.getSetup();
      Branch branch = setup.getBranch();
      ItemProvider branchPresentation = new ItemProvider(branch.getProject().getName() + " "
          + labelProvider.getText(branch) + " (Eclipse " + setup.getEclipseVersion().getVersion() + ")",
          labelProvider.getImage(branch));
      branchPresentation.getChildren().addAll(triggeredSetupTasks);
      children.add(branchPresentation);
    }

    treeViewer.setInput(input);
    tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
    tree.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

    text = new Text(sashForm, SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
    text.setBackground(text.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
    text.setFont(SWTResourceManager.getFont("Courier New", 9, SWT.NORMAL));
    text.setEditable(false);
    text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
    text.getVerticalBar().addSelectionListener(scrollBarListener);
  }

  public ILabelProvider createLabelProvider()
  {
    return new ToolTipLabelProvider()
    {
      @Override
      public Font getFont(Object element)
      {
        if (element == currentTask)
        {
          return ExtendedFontRegistry.INSTANCE.getFont(treeViewer.getControl().getFont(), IItemFontProvider.BOLD_FONT);
        }

        return super.getFont(element);
      }

      @Override
      public Color getForeground(Object element)
      {
        if (currentPerformer != null)
        {
          if (element instanceof EObject)
          {
            for (EObject eObject = (EObject)element; eObject != null; eObject = eObject.eContainer())
            {
              if (eObject instanceof SetupTask)
              {
                if (!currentPerformer.getNeededTasks().contains(eObject))
                {
                  return treeViewer.getControl().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);
                }

                break;
              }
            }
          }
        }

        return super.getForeground(element);
      }
    };
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
    return super.close();
  }

  public boolean isCancelled()
  {
    return cancelled;
  }

  public void log(String line)
  {
    log(line, true);
  }

  private void log(String line, boolean filter)
  {
    if (isCancelled())
    {
      throw new OperationCanceledException();
    }

    if (filter)
    {
      line = logFilter.filter(line);
    }

    if (line == null)
    {
      return;
    }

    final String message = line + "\n";
    final Date date = new Date();

    asyncExec(new Runnable()
    {
      public void run()
      {
        if (okButton != null && !okButton.isDisposed() && !okButton.isEnabled())
        {
          String string = "[" + TIME.format(date) + "] " + message;

          if (scrollBarListener.isDragging())
          {
            scrollBarListener.appendText(string);
          }
          else
          {
            appendText(string);
          }
        }
      }
    });
  }

  public void log(IStatus status)
  {
    log(toString(status), false);
  }

  public void log(Throwable t)
  {
    log(toString(t), false);
  }

  public void task(final SetupTask setupTask)
  {
    UIUtil.getDisplay().asyncExec(new Runnable()
    {
      public void run()
      {
        SetupTask previousCurrentTask = currentTask;
        currentTask = setupTask;

        if (currentPerformer == null || !currentPerformer.getTriggeredSetupTasks().contains(setupTask))
        {
          for (SetupTaskPerformer performer : setupTaskPerformers)
          {
            if (performer.getTriggeredSetupTasks().contains(setupTask))
            {
              currentPerformer = performer;
              treeViewer.refresh(true);
              break;
            }
          }
        }

        int offset = 0;
        if (previousCurrentTask != null)
        {
          Point previousTextSelection = setupTaskSelections.get(previousCurrentTask);
          offset = text.getCharCount();
          int start = previousTextSelection.x;
          setupTaskSelections.put(previousCurrentTask, new Point(start, offset));
          treeViewer.refresh(previousCurrentTask, true);
        }

        if (setupTask == null)
        {
          currentPerformer = null;
        }
        else
        {
          setupTaskSelections.put(setupTask, new Point(offset, -1));
          treeViewer.refresh(setupTask, true);
          treeViewer.removeSelectionChangedListener(treeViewerSelectionChangedListener);
          treeViewer.setSelection(new StructuredSelection(setupTask), true);
          treeViewer.addSelectionChangedListener(treeViewerSelectionChangedListener);
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
          task(null);

          okButton.setEnabled(true);
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
      UIUtil.getDisplay().asyncExec(runnable);
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

  private void appendText(String string)
  {
    Rectangle clientArea = text.getClientArea();
    int visibleLines = clientArea.height / text.getLineHeight();
    int topVisibleLine = text.getLineCount() - visibleLines;
    int topIndex = text.getTopIndex();
    int delta = topVisibleLine - topIndex;
    if (delta <= 2)
    {
      text.append(string);
    }
    else
    {
      text.setRedraw(false);
      text.setText(text.getText().trim() + text.getLineDelimiter() + string);
      text.setTopIndex(topIndex);
      text.setRedraw(true);
    }
  }

  private static boolean promptUnresolvedVariables(Shell shell, List<SetupTaskPerformer> setupTaskPerformers)
  {
    final PromptDialog promptDialog = new PromptDialog(shell, setupTaskPerformers);

    final AtomicInteger result = new AtomicInteger();
    if (shell != null && shell.getDisplay() == Display.getCurrent())
    {
      result.set(promptDialog.open());
    }
    else
    {
      UIUtil.getDisplay().syncExec(new Runnable()
      {
        public void run()
        {
          result.set(promptDialog.open());
        }
      });
    }

    return result.get() == PromptDialog.OK;
  }

  public static void run(final Shell shell, final ProgressLogRunnable runnable,
      final List<SetupTaskPerformer> setupTaskPerformers)
  {
    try
    {
      final AtomicReference<Set<String>> result = new AtomicReference<Set<String>>();
      final ProgressDialog dialog = new ProgressDialog(shell, setupTaskPerformers);
      Runnable jobRunnable = new Runnable()
      {
        public void run()
        {
          final Job job = new Job(JOB_NAME)
          {
            @Override
            protected IStatus run(IProgressMonitor monitor)
            {
              for (SetupTaskPerformer taskPerformer : setupTaskPerformers)
              {
                List<ContextVariableTask> unresolvedVariables = taskPerformer.getUnresolvedVariables();
                if (!unresolvedVariables.isEmpty())
                {
                  if (!promptUnresolvedVariables(shell, setupTaskPerformers))
                  {
                    return Status.CANCEL_STATUS;
                  }

                  for (SetupTaskPerformer setupTaskPerformer : setupTaskPerformers)
                  {
                    setupTaskPerformer.resolveSettings();
                  }

                  break;
                }
              }

              long start = System.currentTimeMillis();

              try
              {
                dialog.log(JOB_NAME);

                Set<String> restartReasons = runnable.run(dialog);
                result.set(restartReasons);
              }
              catch (Throwable ex)
              {
                Activator.log(ex);
                dialog.log(ex);

                // IStatus status = Activator.getStatus(ex);
                // dialog.log(status);
              }
              finally
              {
                long seconds = (System.currentTimeMillis() - start) / 1000;
                dialog.log("Took " + seconds + " seconds.");
                Set<String> restartReasons = result.get();
                if (!ObjectUtil.isEmpty(restartReasons) && SetupConstants.SETUP_IDE)
                {
                  dialog.log("A restart is needed for the following reasons:");
                  for (String reason : restartReasons)
                  {
                    dialog.log("  - " + reason);
                  }

                  dialog.log("Press OK to restart now or Cancel to restart later...");
                }
                else
                {
                  dialog.log("Press OK to close the dialog...");
                }

                dialog.setFinished();
              }

              return Status.OK_STATUS;
            }
          };

          UIUtil.getDisplay().asyncExec(new Runnable()
          {
            public void run()
            {
              job.schedule();
            }
          });

          if (dialog.open() == ProgressDialog.OK && !ObjectUtil.isEmpty(result.get()) && SetupConstants.SETUP_IDE)
          {
            PlatformUI.getWorkbench().restart();
          }
        }
      };

      if (shell != null && shell.getDisplay() == Display.getCurrent())
      {
        jobRunnable.run();
      }
      else
      {
        UIUtil.getDisplay().asyncExec(jobRunnable);
      }
    }
    catch (Throwable ex)
    {
      Activator.log(ex);
      ErrorDialog.open(ex);
    }
  }

  public static String toString(Throwable t)
  {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PrintStream printStream;

    try
    {
      printStream = new PrintStream(out, false, "UTF-8");
      t.printStackTrace(printStream);
      printStream.close();
      return new String(out.toByteArray(), "UTF-8");
    }
    catch (UnsupportedEncodingException ex)
    {
      return t.toString();
    }
  }

  public static String toString(IStatus status)
  {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PrintStream printStream;

    try
    {
      printStream = new PrintStream(out, false, "UTF-8");
      deeplyPrint(status, printStream, 0);
      printStream.close();
      return new String(out.toByteArray(), "UTF-8");
    }
    catch (UnsupportedEncodingException ex)
    {
      return status.getMessage();
    }
  }

  private static void deeplyPrint(IStatus status, PrintStream strm, int level)
  {
    appendLevelPrefix(strm, level);
    String msg = status.getMessage();
    strm.println(msg);
    Throwable cause = status.getException();
    if (cause != null)
    {
      strm.print("Caused by: ");
      if (!(msg.equals(cause.getMessage()) || msg.equals(cause.toString())))
      {
        deeplyPrint(cause, strm, level);
      }
    }

    if (status.isMultiStatus())
    {
      IStatus[] children = status.getChildren();
      for (int i = 0; i < children.length; i++)
      {
        deeplyPrint(children[i], strm, level + 1);
      }
    }
  }

  private static void deeplyPrint(Throwable t, PrintStream strm, int level)
  {
    if (t instanceof CoreException)
    {
      deeplyPrint(t, strm, level);
    }
    else
    {
      appendLevelPrefix(strm, level);
      strm.println(t.toString());
      Throwable cause = t.getCause();
      if (cause != null)
      {
        strm.print("Caused by: "); //$NON-NLS-1$
        deeplyPrint(cause, strm, level);
      }
    }
  }

  private static void appendLevelPrefix(PrintStream strm, int level)
  {
    for (int idx = 0; idx < level; ++idx)
    {
      strm.print(' ');
    }
  }

  private static String getTitle(List<SetupTaskPerformer> setupTaskPerformers)
  {
    return (SetupConstants.SETUP_IDE ? "Setting up" : "Installing") + " Development Environment"
        + (setupTaskPerformers.size() > 1 ? "s" : "");
  }

  /**
   * @author Eike Stepper
   */
  protected class ScrollBarListener extends SelectionAdapter
  {
    private boolean isDragging;

    private String pendingLines;

    @Override
    public void widgetSelected(SelectionEvent event)
    {
      if (event.detail == SWT.NONE)
      {
        isDragging = false;
        if (pendingLines != null)
        {
          ProgressDialog.this.appendText(pendingLines);
          pendingLines = null;
        }
      }
      else if (event.detail == SWT.DRAG)
      {
        isDragging = true;
      }
    }

    public boolean isDragging()
    {
      return isDragging;
    }

    public void appendText(String line)
    {
      if (pendingLines == null)
      {
        pendingLines = line;
      }
      else
      {
        pendingLines = pendingLines.trim() + text.getLineDelimiter() + line;
      }
    }
  }
}
