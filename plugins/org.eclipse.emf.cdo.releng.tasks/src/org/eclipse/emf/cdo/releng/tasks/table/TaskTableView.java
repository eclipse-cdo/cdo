/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.tasks.table;

import org.eclipse.emf.cdo.releng.tasks.Activator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Eike Stepper
 */
public class TaskTableView extends ViewPart
{
  public static final String ID = "org.eclipse.emf.cdo.releng.tasks.table.TaskTableView"; //$NON-NLS-1$

  private static final String SEARCH_LABEL = "search";

  private TaskModel taskModel;

  private GridTableViewer viewer;

  private boolean searching;

  private TaskSearchAction searchAction;

  private TaskFilterAction showEnhancements;

  private TaskFilterAction showAmbiguous;

  private TaskFilterAction showBugs;

  private TaskFilterAction showOpen;

  private TaskFilterAction showFixed;

  public TaskTableView()
  {
  }

  @Override
  public void createPartControl(Composite parent)
  {
    taskModel = new TaskModel();

    Composite container = new Composite(parent, SWT.NONE);
    container.setLayout(new FillLayout());
    {
      viewer = new GridTableViewer(container, SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);

      Grid table = viewer.getGrid();
      table.setCellSelectionEnabled(true);
      table.setHeaderVisible(true);
      table.addMouseListener(new TaskMouseListener());
      table.addKeyListener(new TaskKeyListener());

      {
        GridColumn column = new GridColumn(table, SWT.NONE);
        column.setText("Summary");
        column.setWidth(400);
      }

      for (Version version : taskModel.getVersions())
      {
        GridColumn column = new GridColumn(table, SWT.CENTER);
        column.setText(version.toString());
        column.setWidth(90);
      }

      viewer.setContentProvider(new TaskContentProvider());
      viewer.setLabelProvider(new TaskLabelProvider());
      viewer.setInput(taskModel);

      // ColumnViewerToolTipSupport.enableFor(viewer, ToolTip.RECREATE);
      getViewSite().setSelectionProvider(viewer);
    }

    createActions();
    initializeToolBar();
    initializeMenu();

    viewer.setFilters(new ViewerFilter[] { new TaskViewerFilter() });
  }

  @Override
  public void setFocus()
  {
    viewer.getControl().setFocus();
  }

  @Override
  public void dispose()
  {
    if (taskModel != null)
    {
      taskModel.dispose();
      taskModel = null;
    }

    super.dispose();
  }

  private void createActions()
  {
  }

  private void initializeToolBar()
  {
    IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
    toolbarManager.add(searchAction = new TaskSearchAction());
    toolbarManager.add(new Separator());
    toolbarManager.add(showEnhancements = new TaskFilterAction("Enhancements", "green.gif"));
    toolbarManager.add(showBugs = new TaskFilterAction("Bugs", "red.gif"));
    toolbarManager.add(showAmbiguous = new TaskFilterAction("Ambiguous", "yellow.gif"));
    toolbarManager.add(new Separator());
    toolbarManager.add(showOpen = new TaskFilterAction("Open", "open.gif"));
    toolbarManager.add(showFixed = new TaskFilterAction("Fixed", "fixed.gif", false));
    toolbarManager.add(new Separator());
    toolbarManager.add(new TaskRefreshAction());
  }

  private void initializeMenu()
  {
    // IMenuManager menuManager = getViewSite().getActionBars().getMenuManager();
  }

  /**
   * @author Eike Stepper
   */
  private final class TaskViewerFilter extends ViewerFilter
  {
    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element)
    {
      LogicalTask task = (LogicalTask)element;
      if (searching)
      {
        String filter = searchAction.getText().toLowerCase();
        String summary = task.getSummary().toLowerCase();
        if (summary.indexOf(filter) == -1)
        {
          return false;
        }
      }

      int severityType = task.getSeverityType();
      if (!showEnhancements.isChecked() && severityType == 1)
      {
        return false;
      }

      if (!showBugs.isChecked() && severityType == 2)
      {
        return false;
      }

      if (!showAmbiguous.isChecked() && severityType == 3)
      {
        return false;
      }

      boolean allFixed = task.isAllFixed();
      if (!showOpen.isChecked() && !allFixed)
      {
        return false;
      }

      if (!showFixed.isChecked() && allFixed)
      {
        return false;
      }

      return true;
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class TaskSearchAction extends Action
  {
    public TaskSearchAction()
    {
      super(SEARCH_LABEL);
    }

    @Override
    public void run()
    {
      MessageDialog.openInformation(getSite().getShell(), "Search Tasks",
          "To search for tasks just start typing in the table.\nPress ESC to reset the search filter.");
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class TaskFilterAction extends Action
  {
    public TaskFilterAction(String text, String image, boolean checked)
    {
      super(text, IAction.AS_CHECK_BOX);
      setImageDescriptor(Activator.getImageDescriptor("icons/" + image));
      setChecked(checked);
    }

    public TaskFilterAction(String text, String image)
    {
      this(text, image, true);
    }

    @Override
    public void run()
    {
      viewer.refresh(true);
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class TaskRefreshAction extends Action
  {
    public TaskRefreshAction()
    {
      super("Refresh", Activator.getImageDescriptor("icons/refresh.gif"));
    }

    @Override
    public void run()
    {
      new Job("Refreshing")
      {
        @Override
        protected IStatus run(IProgressMonitor monitor)
        {
          taskModel.refresh();
          viewer.getControl().getDisplay().syncExec(new Runnable()
          {
            public void run()
            {
              viewer.refresh(true);
            }
          });

          return Status.OK_STATUS;
        }
      }.schedule();
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class TaskKeyListener extends KeyAdapter
  {
    @Override
    public void keyPressed(KeyEvent e)
    {
      char c = e.character;
      if (Character.isLetter(c) || Character.isDigit(c) || Character.isSpaceChar(c))
      {
        String filter = searchAction.getText().toLowerCase();
        if (!searching)
        {
          filter = "";
          searching = true;
        }

        filter += new Character(c);
        searchAction.setText(filter.toLowerCase());
        viewer.refresh(true);
      }
      else if (e.keyCode == SWT.BS)
      {
        if (searching)
        {
          String filter = searchAction.getText().toLowerCase();
          searchAction.setText(filter.substring(0, filter.length() - 1));
          viewer.refresh(true);
        }
      }
      else if (e.keyCode == SWT.ESC)
      {
        searching = false;
        searchAction.setText(SEARCH_LABEL);
        viewer.refresh(true);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class TaskMouseListener extends MouseAdapter
  {
    @Override
    @SuppressWarnings("restriction")
    public void mouseDoubleClick(MouseEvent event)
    {
      Object element = ((IStructuredSelection)viewer.getSelection()).getFirstElement();
      if (element instanceof LogicalTask)
      {
        LogicalTask logicalTask = (LogicalTask)element;
        ViewerCell cell = viewer.getCell(new Point(event.x, event.y));

        int columnIndex = cell.getColumnIndex();
        if (columnIndex != 0)
        {
          Version[] versions = taskModel.getVersions();
          Version version = versions[columnIndex - 1];

          for (VersionTask versionTask : logicalTask.getVersionTasks())
          {
            if (versionTask.getVersion().equals(version))
            {
              ITask task = versionTask.getTask();
              org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal.refreshAndOpenTaskListElement(task);
            }
          }
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class TaskContentProvider implements IStructuredContentProvider
  {
    public Object[] getElements(Object inputElement)
    {
      return taskModel.getLogicalTasks();
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
    }

    public void dispose()
    {
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class TaskLabelProvider extends CellLabelProvider
  {
    private Color gray = viewer.getControl().getDisplay().getSystemColor(SWT.COLOR_GRAY);

    private Color red = viewer.getControl().getDisplay().getSystemColor(SWT.COLOR_RED);

    private Font bold = getBoldFont();

    private Image[] severityImages = { null, loadImage("green.gif"), loadImage("red.gif"), loadImage("yellow.gif") };

    @Override
    public void update(ViewerCell cell)
    {
      LogicalTask logicalTask = (LogicalTask)cell.getElement();
      int columnIndex = cell.getColumnIndex();
      if (columnIndex == 0)
      {
        cell.setImage(severityImages[logicalTask.getSeverityType()]);
        cell.setText(logicalTask.getSummary());
        cell.setFont(logicalTask.getSeverityType() > 1 ? bold : null);
        cell.setForeground(logicalTask.isAllFixed() ? gray : null);
      }
      else
      {
        Version version = taskModel.getVersions()[columnIndex - 1];
        VersionTask versionTask = logicalTask.getVersionTask(version);
        if (versionTask != null)
        {
          cell.setText(versionTask.getStatus());
          cell.setFont(!"enhancement".equalsIgnoreCase(versionTask.getSeverity()) ? bold : null);
          cell.setForeground("FIXED".equalsIgnoreCase(versionTask.getResolution()) ? gray : null);
          cell.setBackground(logicalTask.getVersionTaskCount(version) > 1 ? red : null);
        }
        else
        {
          cell.setText("");
          cell.setFont(null);
          cell.setForeground(null);
          cell.setBackground(null);
        }
      }
    }

    @Override
    public String getToolTipText(Object element)
    {
      return super.getToolTipText(element);
    }

    @Override
    public Point getToolTipShift(Object object)
    {
      return new Point(5, 5);
    }

    @Override
    public int getToolTipDisplayDelayTime(Object object)
    {
      return 500;
    }

    @Override
    public int getToolTipTimeDisplayed(Object object)
    {
      return 4000;
    }

    @Override
    public void dispose()
    {
      if (bold != null)
      {
        bold.dispose();
        bold = null;
      }

      for (int i = 0; i < severityImages.length; i++)
      {
        if (severityImages[i] != null)
        {
          severityImages[i].dispose();
          severityImages[i] = null;
        }
      }

      super.dispose();
    }

    private Image loadImage(String file)
    {
      ImageDescriptor descriptor = Activator.getImageDescriptor("icons/" + file);
      return descriptor.createImage(viewer.getControl().getDisplay());
    }

    public Font getBoldFont()
    {
      Control control = viewer.getControl();
      FontData[] datas = control.getFont().getFontData().clone();
      datas[0].setStyle(SWT.BOLD);
      Display display = control.getShell().getDisplay();
      Font font = new Font(display, datas);
      return font;
    }
  }
}
