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
package org.eclipse.net4j.internal.debug.views;

import org.eclipse.net4j.internal.debug.bundle.OM;
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.collection.Pair;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
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
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import java.lang.reflect.Field;
import java.util.Stack;

/**
 * @author Eike Stepper
 */
public class IntrospectorView extends ViewPart implements ISelectionListener
{
  private static final Object[] NO_ELEMENTS = {};

  private TableViewer viewer;

  // private Object object;

  private Stack<Element> elements = new Stack();

  private Text classLabel;

  private Text objectLabel;

  private IAction backAction = new BackAction();

  public IntrospectorView()
  {
  }

  @Override
  public void dispose()
  {
    getSite().getPage().removeSelectionListener(this);
    super.dispose();
  }

  @Override
  public void createPartControl(Composite parent)
  {
    Color bg = parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
    Color gray = parent.getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE);

    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(newGrid(1));

    Composite c = new Composite(composite, SWT.BORDER);
    c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    c.setLayout(newGrid(2));

    classLabel = new Text(c, SWT.READ_ONLY);
    classLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
    classLabel.setBackground(bg);
    classLabel.setForeground(gray);

    objectLabel = new Text(c, SWT.READ_ONLY);
    objectLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    objectLabel.setBackground(bg);

    viewer = new TableViewer(composite, SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
    viewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    viewer.getTable().setHeaderVisible(true);
    viewer.getTable().setLinesVisible(true);
    createColmuns(viewer);
    viewer.setContentProvider(new ViewContentProvider());
    viewer.setLabelProvider(new ViewLabelProvider());
    viewer.setInput(getViewSite());

    hookContextMenu();
    hookDoubleClickAction();
    contributeToActionBars();

    getSite().getPage().addSelectionListener(this);
  }

  public void refreshViewer()
  {
    if (isViewerAlive())
    {
      getSite().getShell().getDisplay().asyncExec(new Runnable()
      {
        public void run()
        {
          if (isViewerAlive())
          {
            viewer.refresh();
            // viewer.setSelection(StructuredSelection.EMPTY);
            // if (!elements.isEmpty())
            // {
            // Element element = elements.peek();
            // if (element.field != null)
            // {
            // setField(element.field);
            // }
            // }
          }
        }
      });
    }
  }

  public void selectionChanged(IWorkbenchPart part, ISelection sel)
  {
    if (part == this)
    {
      return;
    }

    if (sel instanceof IStructuredSelection)
    {
      IStructuredSelection ssel = (IStructuredSelection)sel;
      elements.clear();
      setObject(ssel.getFirstElement());
    }
    else
    {
      setObject(null);
    }
  }

  /**
   * Passing the focus request to the viewer's control.
   */
  @Override
  public void setFocus()
  {
    viewer.getControl().setFocus();
  }

  private void doubleClicked(Pair<Field, Object> pair)
  {
    Field field = pair.getElement1();
    if (!field.getType().isPrimitive())
    {
      Element element = elements.peek();
      if (element != null)
      {
        element.field = field;
      }

      setObject(pair.getElement2());
    }
  }

  private void setObject(Object object)
  {
    if (object != null)
    {
      if (!elements.isEmpty())
      {
        Element element = elements.peek();
        if (element.object != object)
        {
          pushObject(object);
        }
      }
      else
      {
        pushObject(object);
      }
    }

    if (object == null)
    {
      classLabel.setText("");
      objectLabel.setText("");
    }
    else
    {
      String className = object.getClass().getName();
      classLabel.setText(className);

      String value = object.toString();
      if (value.startsWith(className + "@"))
      {
        objectLabel.setText(value.substring(className.length()));
      }
      else
      {
        objectLabel.setText(value);
      }
    }

    classLabel.getParent().layout();
    backAction.setEnabled(elements.size() >= 2);
    refreshViewer();
  }

  private void pushObject(Object object)
  {
    Element e = new Element();
    e.object = object;
    elements.push(e);
  }

  @SuppressWarnings("unused")
  private void setField(Field field)
  {
    TableItem[] items = viewer.getTable().getItems();
    for (TableItem item : items)
    {
      Object data = item.getData();
      if (data instanceof Pair)
      {
        Pair<Field, Object> pair = (Pair)data;
        if (pair.getElement1() == field)
        {
          viewer.setSelection(new StructuredSelection(pair.getElement2()), true);
          break;
        }
      }
    }
  }

  private GridLayout newGrid(int numColumns)
  {
    GridLayout grid = new GridLayout(numColumns, false);
    grid.marginTop = 0;
    grid.marginLeft = 0;
    grid.marginRight = 0;
    grid.marginBottom = 0;
    grid.marginWidth = 0;
    grid.marginHeight = 0;
    grid.horizontalSpacing = 0;
    grid.verticalSpacing = 0;
    return grid;
  }

  private void createColmuns(TableViewer viewer)
  {
    final String[] columnNames = { "Field", "Value", "Declared Type", "Concrete Type" };
    final int[] columnWidths = { 200, 400, 300, 300 };
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

  private boolean isViewerAlive()
  {
    return viewer != null && viewer.getControl() != null && !viewer.getControl().isDisposed();
  }

  private void hookContextMenu()
  {
    MenuManager menuMgr = new MenuManager("#PopupMenu");
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(new IMenuListener()
    {
      public void menuAboutToShow(IMenuManager manager)
      {
        IntrospectorView.this.fillContextMenu(manager);
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
  }

  private void fillContextMenu(IMenuManager manager)
  {
    manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
  }

  private void fillLocalToolBar(IToolBarManager manager)
  {
    manager.add(backAction);
  }

  private void hookDoubleClickAction()
  {
    viewer.addDoubleClickListener(new IDoubleClickListener()
    {
      public void doubleClick(DoubleClickEvent event)
      {
        ISelection sel = event.getSelection();
        if (sel instanceof IStructuredSelection)
        {
          IStructuredSelection ssel = (IStructuredSelection)sel;
          Object element = ssel.getFirstElement();
          if (element instanceof Pair)
          {
            doubleClicked((Pair)element);
          }
        }
      }
    });
  }

  @SuppressWarnings("unused")
  private void showMessage(String message)
  {
    MessageDialog.openInformation(viewer.getControl().getShell(), "Remote Traces", message);
  }

  static class Element
  {
    public Object object;

    public Field field;
  }

  /**
   * @author Eike Stepper
   */
  class BackAction extends Action
  {
    private BackAction()
    {
      super("Back");
      ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
      setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_BACK));
      setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_BACK_DISABLED));
    }

    @Override
    public void run()
    {
      if (!elements.isEmpty())
      {
        elements.pop();
        if (!elements.isEmpty())
        {
          Element element = elements.peek();
          setObject(element.object);
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  class ViewContentProvider implements IStructuredContentProvider
  {
    public void inputChanged(Viewer v, Object oldInput, Object newInput)
    {
    }

    public void dispose()
    {
    }

    public Object[] getElements(Object parent)
    {
      if (!elements.isEmpty())
      {
        try
        {
          Element element = elements.peek();
          return ReflectUtil.dumpToArray(element.object);
        }
        catch (RuntimeException ex)
        {
          OM.LOG.error(ex);
        }
      }

      return NO_ELEMENTS;
    }
  }

  /**
   * @author Eike Stepper
   */
  class ViewLabelProvider extends LabelProvider implements ITableLabelProvider
  {
    public ViewLabelProvider()
    {
    }

    public String getColumnText(Object obj, int index)
    {
      if (obj instanceof Pair)
      {
        try
        {
          Pair<Field, Object> pair = (Pair)obj;
          Field field = pair.getElement1();
          Object value = pair.getElement2();
          switch (index)
          {
          case 0:
            return field.getName();
          case 1:
            return value == null ? "null" : value.toString();
          case 2:
            return field.getType().getName();
          case 3:
            return value == null ? "" : value.getClass().getName();
          }
        }
        catch (RuntimeException ex)
        {
          OM.LOG.error(ex);
        }
      }

      return getText(obj);
    }

    public Image getColumnImage(Object obj, int index)
    {
      return null;
    }

    @Override
    public Image getImage(Object obj)
    {
      return null;
    }
  }

  /**
   * @author Eike Stepper
   */
  class NameSorter extends ViewerSorter
  {
  }
}