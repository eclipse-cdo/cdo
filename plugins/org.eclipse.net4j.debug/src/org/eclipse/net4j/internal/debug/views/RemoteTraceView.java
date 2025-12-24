/*
 * Copyright (c) 2006-2009, 2011, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.debug.views;

import org.eclipse.net4j.internal.debug.RemoteTraceManager;
import org.eclipse.net4j.internal.debug.messages.Messages;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.om.trace.RemoteTraceServer.Event;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Eike Stepper
 */
public class RemoteTraceView extends ViewPart
{
  private static RemoteTraceView instance;

  private TableViewer viewer;

  private Action clearAction;

  private Action doubleClickAction;

  public RemoteTraceView()
  {
  }

  @Override
  public void dispose()
  {
    instance = null;
    super.dispose();
  }

  @Override
  public void createPartControl(Composite parent)
  {
    viewer = new TableViewer(parent, SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
    viewer.getTable().setHeaderVisible(true);
    createColmuns(viewer);
    viewer.setContentProvider(new ViewContentProvider());
    viewer.setLabelProvider(new ViewLabelProvider());
    viewer.setInput(getViewSite());

    // final ToolTipHandler tooltip = new ToolTipHandler(getSite().getShell());
    // tooltip.activateHoverHelp(viewer.getTable());

    makeActions();
    hookContextMenu();
    hookDoubleClickAction();
    contributeToActionBars();
    instance = this;
  }

  protected void createColmuns(TableViewer viewer)
  {
    final String[] columnNames = { Messages.getString("RemoteTraceView.0"), Messages.getString("RemoteTraceView.1"), //$NON-NLS-1$ //$NON-NLS-2$
        Messages.getString("RemoteTraceView.2"), Messages.getString("RemoteTraceView.3"), //$NON-NLS-1$ //$NON-NLS-2$
        Messages.getString("RemoteTraceView.4"), Messages.getString("RemoteTraceView.5"), //$NON-NLS-1$ //$NON-NLS-2$
        Messages.getString("RemoteTraceView.6"), //$NON-NLS-1$
        Messages.getString("RemoteTraceView.7") }; //$NON-NLS-1$
    final int[] columnWidths = { 60, 170, 80, 160, 120, 120, 400, 200 };
    TableColumn[] columns = new TableColumn[columnNames.length];
    for (int i = 0; i < columns.length; i++)
    {
      TableColumn column = new TableColumn(viewer.getTable(), SWT.LEFT, i);
      column.setText(columnNames[i]);
      column.setWidth(columnWidths[i]);
      column.setMoveable(true);
      column.setResizable(true);
    }
  }

  public static void notifyNewTrace()
  {
    if (instance != null)
    {
      instance.refreshViewer();
    }
  }

  public void refreshViewer()
  {
    UIUtil.refreshViewer(viewer);
  }

  private void hookContextMenu()
  {
    MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(new IMenuListener()
    {
      @Override
      public void menuAboutToShow(IMenuManager manager)
      {
        RemoteTraceView.this.fillContextMenu(manager);
      }
    });

    Menu menu = menuMgr.createContextMenu(viewer.getControl());
    viewer.getControl().setMenu(menu);
    getSite().registerContextMenu(menuMgr, viewer);
  }

  private void contributeToActionBars()
  {
    IActionBars bars = getViewSite().getActionBars();
    fillLocalPullDown(bars.getMenuManager());
    fillLocalToolBar(bars.getToolBarManager());
  }

  private void fillLocalPullDown(IMenuManager manager)
  {
    manager.add(clearAction);
  }

  private void fillContextMenu(IMenuManager manager)
  {
    manager.add(clearAction);
    // Other plug-ins can contribute there actions here
    manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
  }

  private void fillLocalToolBar(IToolBarManager manager)
  {
    manager.add(clearAction);
  }

  private void makeActions()
  {
    clearAction = new Action()
    {
      @Override
      public void run()
      {
        RemoteTraceManager.INSTANCE.clearEvents();
        refreshViewer();
      }
    };

    clearAction.setText(Messages.getString("RemoteTraceView.9")); //$NON-NLS-1$
    clearAction.setToolTipText(Messages.getString("RemoteTraceView.10")); //$NON-NLS-1$
    clearAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));

    doubleClickAction = new Action()
    {
      @Override
      public void run()
      {
        ISelection selection = viewer.getSelection();
        Object obj = ((IStructuredSelection)selection).getFirstElement();
        showMessage(Messages.getString("RemoteTraceView.11") + obj.toString()); //$NON-NLS-1$
      }
    };
  }

  private void hookDoubleClickAction()
  {
    viewer.addDoubleClickListener(new IDoubleClickListener()
    {
      @Override
      public void doubleClick(DoubleClickEvent event)
      {
        doubleClickAction.run();
      }
    });
  }

  private void showMessage(String message)
  {
    MessageDialog.openInformation(viewer.getControl().getShell(), Messages.getString("RemoteTraceView.12"), message); //$NON-NLS-1$
  }

  /**
   * Passing the focus request to the viewer's control.
   */
  @Override
  public void setFocus()
  {
    viewer.getControl().setFocus();
  }

  /**
   * @author Eike Stepper
   */
  class ViewContentProvider implements IStructuredContentProvider
  {
    @Override
    public void inputChanged(Viewer v, Object oldInput, Object newInput)
    {
    }

    @Override
    public void dispose()
    {
    }

    @Override
    public Object[] getElements(Object parent)
    {
      return RemoteTraceManager.INSTANCE.getEvents();
    }
  }

  /**
   * @author Eike Stepper
   */
  class ViewLabelProvider extends LabelProvider implements ITableLabelProvider
  {
    private Image info;

    private Image error;

    private Image text;

    public ViewLabelProvider()
    {
      ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
      info = sharedImages.getImage(ISharedImages.IMG_OBJS_INFO_TSK);
      error = sharedImages.getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
      text = sharedImages.getImage(ISharedImages.IMG_OBJ_FILE);
    }

    @Override
    public String getColumnText(Object obj, int index)
    {
      if (obj instanceof Event)
      {
        Event event = (Event)obj;
        String text = event.getText(index);
        switch (index)
        {
        case 5:
          return ReflectUtil.getSimpleClassName(text);
        case 6:
          int at = text.indexOf('@');
          if (at == -1)
          {
            return text;
          }

          String context = ReflectUtil.getSimpleClassName(event.getText(5));
          String id = text.substring(at + 1);
          String className = text.substring(0, at);
          if (ObjectUtil.equals(context, className))
          {
            return id;
          }

          return id + " (" + className + ")"; //$NON-NLS-1$ //$NON-NLS-2$

        case 7:
          return getFirstLine(text);
        }

        return text;
      }

      return getText(obj);
    }

    @Override
    public Image getColumnImage(Object obj, int index)
    {
      if (obj instanceof Event)
      {
        Event event = (Event)obj;
        switch (index)
        {
        case 0:
          return event.hasError() ? error : info;
        case 7:
          return hasNL(event.getMessage()) ? text : null;
        case 8:
          return event.hasError() ? error : null;
        }
      }

      return null;
    }

    @Override
    public Image getImage(Object obj)
    {
      return null;
    }

    private boolean hasNL(String str)
    {
      return str.indexOf('\n') != -1;
    }

    private String getFirstLine(String str)
    {
      int nl = str.indexOf('\n');
      if (nl != -1)
      {
        str = str.substring(0, nl);
      }

      return str.replaceAll("\r", ""); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  // static class ToolTipHandler
  // {
  // private Shell tipShell;
  //
  // private Label tipLabel;
  //
  // private TableItem tipItem;
  //
  // private Point tipPosition;
  //
  // public ToolTipHandler(Shell parent)
  // {
  // final Display display = parent.getDisplay();
  //
  // tipShell = new Shell(parent, SWT.ON_TOP | SWT.TOOL);
  // GridLayout gridLayout = new GridLayout();
  // gridLayout.numColumns = 2;
  // gridLayout.marginWidth = 2;
  // gridLayout.marginHeight = 2;
  // tipShell.setLayout(gridLayout);
  //
  // tipShell.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
  //
  // tipLabel = new Label(tipShell, SWT.NONE);
  // tipLabel.setForeground(display.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
  // tipLabel.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
  // tipLabel
  // .setLayoutData(new GridData(GridData.FILL_HORIZONTAL |
  // GridData.VERTICAL_ALIGN_CENTER));
  // }
  //
  // public void activateHoverHelp(final Control control)
  // {
  // control.addMouseListener(new MouseAdapter()
  // {
  // @Override
  // public void mouseDown(MouseEvent e)
  // {
  // if (tipShell.isVisible())
  // {
  // tipShell.setVisible(false);
  // }
  // }
  // });
  //
  // control.addMouseTrackListener(new MouseTrackAdapter()
  // {
  // @Override
  // public void mouseExit(MouseEvent e)
  // {
  // if (tipShell.isVisible())
  // {
  // tipShell.setVisible(false);
  // }
  //
  // tipItem = null;
  // }
  //
  // @Override
  // public void mouseHover(MouseEvent event)
  // {
  // Point pt = new Point(event.x, event.y);
  // Widget widget = event.widget;
  // if (widget instanceof Table)
  // {
  // Table table = (Table)widget;
  // TableItem item = table.getItem(pt);
  // if (widget == null)
  // {
  // tipShell.setVisible(false);
  // tipItem = null;
  // return;
  // }
  //
  // if (item == tipItem)
  // {
  // return;
  // }
  //
  // Object data = item.getData();
  // if (data instanceof Event)
  // {
  // Event e = (Event)data;
  // }
  //
  // tipItem = item;
  // tipPosition = control.toDisplay(pt);
  // String text = (String)item.getData("TIP_TEXT");
  // tipLabel.setText(text != null ? text : "");
  // tipShell.pack();
  // setHoverLocation(tipShell, tipPosition);
  // tipShell.setVisible(true);
  // }
  // }
  // });
  // }
  //
  // private void setHoverLocation(Shell shell, Point position)
  // {
  // Rectangle displayBounds = shell.getDisplay().getBounds();
  // Rectangle shellBounds = shell.getBounds();
  // shellBounds.x = Math.max(Math.min(position.x, displayBounds.width -
  // shellBounds.width), 0);
  // shellBounds.y = Math.max(
  // Math.min(position.y + 16, displayBounds.height - shellBounds.height), 0);
  // shell.setBounds(shellBounds);
  // }
  // }
}
