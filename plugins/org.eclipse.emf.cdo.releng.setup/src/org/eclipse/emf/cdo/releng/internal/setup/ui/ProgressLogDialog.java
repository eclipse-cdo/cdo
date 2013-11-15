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
import org.eclipse.emf.cdo.releng.setup.Setup;
import org.eclipse.emf.cdo.releng.setup.SetupTask;
import org.eclipse.emf.cdo.releng.setup.util.log.ProgressLog;
import org.eclipse.emf.cdo.releng.setup.util.log.ProgressLogFilter;
import org.eclipse.emf.cdo.releng.setup.util.log.ProgressLogProvider;
import org.eclipse.emf.cdo.releng.setup.util.log.ProgressLogRunnable;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;

import org.eclipse.emf.common.ui.ImageURIRegistry;
import org.eclipse.emf.common.ui.viewer.ColumnViewerInformationControlToolTipSupport;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.edit.provider.AdapterFactoryItemDelegator;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.emf.edit.ui.provider.DecoratingColumLabelProvider;
import org.eclipse.emf.edit.ui.provider.DiagnosticDecorator;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
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
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class ProgressLogDialog extends AbstractSetupDialog implements ProgressLog
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

  private TreeViewer treeViewer;

  private ProgressLogDialog(Shell parentShell, List<SetupTaskPerformer> setupTaskPerformers)
  {
    super(parentShell);
    this.setupTaskPerformers = setupTaskPerformers;
    setHelpAvailable(false);
  }

  /**
   * Return the initial size of the dialog.
   */
  @Override
  protected Point getInitialSize()
  {
    return new Point(1000, 600);
  }

  @Override
  protected int getContainerMargin()
  {
    return 10;
  }

  @Override
  protected String getDefaultMessage()
  {
    return "Please wait until the setup process is finished and the OK button is enabled...";
  }

  @Override
  protected void createUI(Composite parent)
  {
    SashForm sashForm = new SashForm(parent, SWT.VERTICAL);
    sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));

    treeViewer = new TreeViewer(sashForm);
    Tree tree = treeViewer.getTree();

    ComposedAdapterFactory adapterFactory = new ComposedAdapterFactory(
        ComposedAdapterFactory.Descriptor.Registry.INSTANCE);
    final AdapterFactoryItemDelegator itemDelegator = new AdapterFactoryItemDelegator(adapterFactory);
    ILabelProvider labelProvider = new DecoratingColumLabelProvider(new AdapterFactoryLabelProvider(adapterFactory),
        new ILabelDecorator()
        {
          public void removeListener(ILabelProviderListener listener)
          {
          }

          public boolean isLabelProperty(Object element, String property)
          {
            return true;
          }

          public void dispose()
          {
          }

          public void addListener(ILabelProviderListener listener)
          {
          }

          public String decorateText(String text, Object element)
          {
            return text;
          }

          public Image decorateImage(Image image, Object element)
          {
            return image;
          }
        })
    {
      @Override
      public String getToolTipText(Object element)
      {
        StringBuilder result = new StringBuilder();
        List<IItemPropertyDescriptor> propertyDescriptors = itemDelegator.getPropertyDescriptors(element);
        if (propertyDescriptors != null)
        {
          result.append("<table border='1'>");
          for (IItemPropertyDescriptor propertyDescriptor : propertyDescriptors)
          {
            result.append("<tr>");

            String displayName = propertyDescriptor.getDisplayName(element);
            result.append("<td>").append(DiagnosticDecorator.escapeContent(displayName)).append("</td>");

            result.append("<td>");
            IItemLabelProvider propertyLabelProvider = propertyDescriptor.getLabelProvider(element);
            Object propertyValue = propertyDescriptor.getPropertyValue(element);
            Object image = propertyLabelProvider.getImage(propertyValue);
            if (image != null)
            {
              result.append(DiagnosticDecorator.enquote("<img src='"
                  + ImageURIRegistry.INSTANCE.getImageURI(ExtendedImageRegistry.INSTANCE.getImage(image)) + "'/> "));
            }

            String valueText = propertyLabelProvider.getText(propertyValue);
            if (!StringUtil.isEmpty(valueText))
            {
              result.append(DiagnosticDecorator.escapeContent(valueText));
            }
            else
            {
              result.append("&nbsp;");
            }

            if (valueText == null && image == null)
            {
              result.append("&nbsp;");
            }

            result.append("</td>");
            result.append("</tr>");
          }

          result.append("</table>");
        }

        return result.length() == 0 ? null : result.toString();
      }
    };

    treeViewer.setLabelProvider(labelProvider);

    ItemProvider input = new ItemProvider();
    final EList<Object> children = input.getChildren();
    final AdapterFactoryContentProvider contentProvider = new AdapterFactoryContentProvider(adapterFactory)
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

    text = new Text(sashForm, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
    text.setFont(SWTResourceManager.getFont("Courier New", 10, SWT.NORMAL));
    text.setEditable(false);
    text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
    text.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
    text.getVerticalBar().addSelectionListener(scrollBarListener);
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
    if (isCancelled())
    {
      throw new OperationCanceledException();
    }

    line = logFilter.filter(line);
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
        if (!okButton.isEnabled())
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
    log(toString(status));
  }

  public void task(final SetupTask setupTask)
  {
    getShell().getDisplay().asyncExec(new Runnable()
    {
      public void run()
      {
        treeViewer.setSelection(new StructuredSelection(setupTask), true);
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

  public static void run(final Shell shell, final ProgressLogRunnable runnable,
      List<SetupTaskPerformer> setupTaskPerformers)
  {
    try
    {
      final AtomicReference<Set<String>> result = new AtomicReference<Set<String>>();
      final ProgressLogDialog dialog = new ProgressLogDialog(shell, setupTaskPerformers);
      Runnable jobRunnable = new Runnable()
      {
        public void run()
        {
          final Job job = new Job(JOB_NAME)
          {
            @Override
            protected IStatus run(IProgressMonitor monitor)
            {
              long start = System.currentTimeMillis();

              try
              {
                dialog.log(JOB_NAME);
                result.set(runnable.run(dialog));
              }
              catch (Throwable ex)
              {
                Activator.log(ex);
                dialog.log("An error occured: " + ex.getMessage());
                dialog.log("The Error Log contains more infos...");
              }
              finally
              {
                long seconds = (System.currentTimeMillis() - start) / 1000;
                dialog.log("Took " + seconds + " seconds.");
                Set<String> restartReasons = result.get();
                if (!ObjectUtil.isEmpty(restartReasons) && Activator.SETUP_IDE)
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

          shell.getDisplay().asyncExec(new Runnable()
          {
            public void run()
            {
              job.schedule();
            }
          });

          if (dialog.open() == ProgressLogDialog.OK && !ObjectUtil.isEmpty(result.get()) && Activator.SETUP_IDE)
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
    catch (Throwable ex)
    {
      Activator.log(ex);
      ErrorDialog.open(ex);
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
          ProgressLogDialog.this.appendText(pendingLines);
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
