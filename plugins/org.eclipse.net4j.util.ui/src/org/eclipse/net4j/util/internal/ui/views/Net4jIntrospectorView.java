/*
 * Copyright (c) 2007-2013, 2015, 2016, 2019-2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.internal.ui.views;

import org.eclipse.net4j.ui.shared.SharedIcons;
import org.eclipse.net4j.util.HexUtil;
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.event.EventUtil;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.internal.ui.bundle.OM;
import org.eclipse.net4j.util.internal.ui.messages.Messages;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * @author Eike Stepper
 */
public class Net4jIntrospectorView extends ViewPart implements IPartListener, ISelectionListener, IDoubleClickListener, IListener
{
  public static final String VIEW_ID = "org.eclipse.net4j.util.Net4jIntrospectorView"; //$NON-NLS-1$

  private static final Object[] NO_ELEMENTS = {};

  private static Net4jIntrospectorView instance;

  private TableViewer currentViewer;

  private TableViewer objectViewer;

  private TableViewer iterableViewer;

  private TableViewer arrayViewer;

  private TableViewer mapViewer;

  private Stack<Object> elements = new Stack<>();

  private Text classLabel;

  private Text identityLabel;

  private Text objectLabel;

  private IAction backAction = new BackAction();

  private IAction modeAction = new ModeAction();

  private IAction containerAction = new ContainerAction();

  private IAction refreshAction = new RefreshAction();

  private StackLayout stackLayout;

  private Composite stacked;

  private IWorkbenchPart activePart;

  public Net4jIntrospectorView()
  {
  }

  @Override
  public void dispose()
  {
    IWorkbenchPage page = getSite().getPage();
    page.removePartListener(this);
    page.removeSelectionListener(this);
    super.dispose();
  }

  public static Net4jIntrospectorView getInstance()
  {
    return instance;
  }

  public static synchronized Net4jIntrospectorView getInstance(boolean show)
  {
    if (instance == null)
    {
      try
      {
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        page.showView(VIEW_ID);
      }
      catch (Exception ex)
      {
        throw WrappedException.wrap(ex);
      }
    }

    return instance;
  }

  @Override
  public void createPartControl(Composite parent)
  {
    Color bg = parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
    Color gray = parent.getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE);

    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(UIUtil.createGridLayout(1));

    Composite c = new Composite(composite, SWT.BORDER);
    c.setLayout(UIUtil.createGridLayout(3));
    c.setLayoutData(UIUtil.createGridData(true, false));

    classLabel = new Text(c, SWT.READ_ONLY);
    classLabel.setLayoutData(UIUtil.createGridData(false, false));
    classLabel.setBackground(bg);
    classLabel.setForeground(gray);

    identityLabel = new Text(c, SWT.READ_ONLY);
    identityLabel.setLayoutData(UIUtil.createGridData(false, false));
    identityLabel.setBackground(bg);

    objectLabel = new Text(c, SWT.READ_ONLY);
    objectLabel.setLayoutData(UIUtil.createGridData(true, false));
    objectLabel.setBackground(bg);

    stackLayout = new StackLayout();
    stacked = new Composite(composite, SWT.NONE);
    stacked.setLayoutData(UIUtil.createGridData());
    stacked.setLayout(stackLayout);

    objectViewer = createViewer(stacked);
    createObjectColmuns();
    objectViewer.addDoubleClickListener(this);
    objectViewer.setContentProvider(new ObjectContentProvider());
    objectViewer.setLabelProvider(new ObjectLabelProvider());
    objectViewer.setComparator(new NameComparator());
    objectViewer.setInput(getViewSite());

    iterableViewer = createViewer(stacked);
    createIterableColmuns();
    iterableViewer.addDoubleClickListener(this);
    iterableViewer.setContentProvider(new IterableContentProvider());
    iterableViewer.setLabelProvider(new IterableLabelProvider());
    iterableViewer.setInput(getViewSite());

    arrayViewer = createViewer(stacked);
    createArrayColmuns();
    arrayViewer.addDoubleClickListener(this);
    arrayViewer.setContentProvider(new ArrayContentProvider());
    arrayViewer.setLabelProvider(new ArrayLabelProvider());
    arrayViewer.setInput(getViewSite());

    mapViewer = createViewer(stacked);
    createMapColmuns();
    mapViewer.addDoubleClickListener(this);
    mapViewer.setContentProvider(new MapContentProvider());
    mapViewer.setLabelProvider(new MapLabelProvider());
    mapViewer.setComparator(new NameComparator());
    mapViewer.setInput(getViewSite());

    IActionBars bars = getViewSite().getActionBars();
    fillLocalPullDown(bars.getMenuManager());
    fillLocalToolBar(bars.getToolBarManager());

    IWorkbenchPage page = getSite().getPage();
    page.addPartListener(this);
    page.addSelectionListener(this);

    setCurrentViewer(objectViewer);
    instance = this;
  }

  private void setCurrentViewer(TableViewer viewer)
  {
    currentViewer = viewer;
    stackLayout.topControl = currentViewer.getControl();
    stacked.layout();
  }

  private TableViewer createViewer(Composite parent)
  {
    TableViewer viewer = new TableViewer(parent, SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
    viewer.getTable().setLayoutData(UIUtil.createGridData());
    viewer.getTable().setHeaderVisible(true);
    viewer.getTable().setLinesVisible(true);
    return viewer;
  }

  public void refreshViewer()
  {
    UIUtil.refreshViewer(currentViewer);
  }

  @Override
  public void partActivated(IWorkbenchPart part)
  {
    if (part == this)
    {
      return;
    }

    activePart = part;
    if (modeAction.isChecked())
    {
      elements.clear();
      setObject(activePart);
    }
  }

  @Override
  public void partBroughtToTop(IWorkbenchPart part)
  {
  }

  @Override
  public void partClosed(IWorkbenchPart part)
  {
  }

  @Override
  public void partDeactivated(IWorkbenchPart part)
  {
    if (modeAction.isChecked())
    {
      IWorkbenchPart newPart = part.getSite().getPage().getActivePart();
      if (newPart == this)
      {
        return;
      }

      if (part == activePart)
      {
        activePart = null;
        setObject(null);
      }
    }
  }

  @Override
  public void partOpened(IWorkbenchPart part)
  {
  }

  @Override
  public void selectionChanged(IWorkbenchPart part, ISelection sel)
  {
    if (part == this)
    {
      return;
    }

    if (modeAction.isChecked())
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

  @Override
  public void doubleClick(DoubleClickEvent event)
  {
    ISelection sel = event.getSelection();
    if (sel instanceof IStructuredSelection)
    {
      IStructuredSelection ssel = (IStructuredSelection)sel;
      Object element = ssel.getFirstElement();
      if (currentViewer == objectViewer && element instanceof Pair<?, ?>)
      {
        @SuppressWarnings("unchecked")
        Pair<Field, Object> pair = (Pair<Field, Object>)element;

        Field field = pair.getElement1();
        if (!field.getType().isPrimitive())
        {
          setObject(pair.getElement2());
        }
      }
      else if (currentViewer == mapViewer && element instanceof Map.Entry<?, ?>)
      {
        Map.Entry<?, ?> entry = (Map.Entry<?, ?>)element;
        setObject(entry.getValue());
      }
      else if (currentViewer == iterableViewer)
      {
        setObject(element);
      }
      else if (currentViewer == arrayViewer && element instanceof Pair<?, ?>)
      {
        @SuppressWarnings("unchecked")
        Pair<Integer, Object> pair = (Pair<Integer, Object>)element;
        setObject(pair.getElement2());
      }
    }
  }

  /**
   * Passing the focus request to the viewer's control.
   */
  @Override
  public void setFocus()
  {
    try
    {
      currentViewer.getControl().setFocus();
    }
    catch (RuntimeException ignore)
    {
    }
  }

  @Override
  public void notifyEvent(IEvent event)
  {
    refreshViewer();
  }

  public void setObject(Object object)
  {
    EventUtil.removeListener(object, this);
    if (object != null)
    {
      if (!elements.isEmpty())
      {
        Object element = elements.peek();
        if (element != object)
        {
          EventUtil.removeListener(element, this);
          elements.push(object);
        }
      }
      else
      {
        elements.push(object);
      }
    }

    if (object == null)
    {
      classLabel.setText(""); //$NON-NLS-1$
      identityLabel.setText(""); //$NON-NLS-1$
      objectLabel.setText(""); //$NON-NLS-1$
      currentViewer = objectViewer;
    }
    else
    {
      EventUtil.addListener(object, this);

      Class<? extends Object> c = object.getClass();
      String className = c.isArray() ? c.getComponentType().getName() + "[]" : c.getName();
      classLabel.setText(className);

      String identity = HexUtil.identityHashCode(object);
      identityLabel.setText(identity);

      String value = object.toString();
      String prefix = c.getName() + "@";
      if (value.startsWith(prefix))
      {
        String text = value.substring(prefix.length());
        if (identity.endsWith(text))
        {
          text = StringUtil.EMPTY;
        }

        objectLabel.setText(text);
      }
      else
      {
        objectLabel.setText(value);
      }
    }

    classLabel.getParent().layout();
    backAction.setEnabled(elements.size() >= 2);

    if (object instanceof Map<?, ?>)
    {
      setCurrentViewer(mapViewer);
    }
    else if (object instanceof Iterable<?>)
    {
      setCurrentViewer(iterableViewer);
    }
    else if (object != null && object.getClass().isArray())
    {
      setCurrentViewer(arrayViewer);
    }
    else
    {
      setCurrentViewer(objectViewer);
    }

    refreshViewer();
  }

  private void createObjectColmuns()
  {
    Table table = objectViewer.getTable();
    String[] columnNames = { Messages.getString("Net4jIntrospectorView_4"), //$NON-NLS-1$
        Messages.getString("Net4jIntrospectorView_5"), Messages.getString("Net4jIntrospectorView_6"), //$NON-NLS-1$ //$NON-NLS-2$
        Messages.getString("Net4jIntrospectorView_7") }; //$NON-NLS-1$
    int[] columnWidths = { 200, 400, 300, 300 };
    createColumns(table, columnNames, columnWidths);
  }

  private void createMapColmuns()
  {
    Table table = mapViewer.getTable();
    String[] columnNames = { Messages.getString("Net4jIntrospectorView_8"), //$NON-NLS-1$
        Messages.getString("Net4jIntrospectorView_9"), Messages.getString("Net4jIntrospectorView_10") }; //$NON-NLS-1$ //$NON-NLS-2$
    int[] columnWidths = { 200, 400, 300 };
    createColumns(table, columnNames, columnWidths);
  }

  private void createIterableColmuns()
  {
    Table table = iterableViewer.getTable();
    String[] columnNames = { Messages.getString("Net4jIntrospectorView_11"), //$NON-NLS-1$
        Messages.getString("Net4jIntrospectorView_12") }; //$NON-NLS-1$
    int[] columnWidths = { 400, 300 };
    createColumns(table, columnNames, columnWidths);
  }

  private void createArrayColmuns()
  {
    Table table = arrayViewer.getTable();
    String[] columnNames = { Messages.getString("Net4jIntrospectorView_13"), //$NON-NLS-1$
        Messages.getString("Net4jIntrospectorView_14"), Messages.getString("Net4jIntrospectorView_15") }; //$NON-NLS-1$ //$NON-NLS-2$
    int[] columnWidths = { 50, 400, 300 };
    createColumns(table, columnNames, columnWidths);
  }

  private void createColumns(Table table, String[] columnNames, int[] columnWidths)
  {
    TableColumn[] columns = new TableColumn[columnNames.length];
    for (int i = 0; i < columns.length; i++)
    {
      TableColumn column = new TableColumn(table, SWT.LEFT, i);
      column.setText(columnNames[i]);
      column.setWidth(columnWidths[i]);
      column.setMoveable(true);
      column.setResizable(true);
    }
  }

  private void fillLocalPullDown(IMenuManager manager)
  {
  }

  private void fillLocalToolBar(IToolBarManager manager)
  {
    manager.add(backAction);
    manager.add(containerAction);
    manager.add(new Separator());
    manager.add(modeAction);
    manager.add(new Separator());
    manager.add(refreshAction);
  }

  /**
   * @author Eike Stepper
   */
  class BackAction extends Action
  {
    private BackAction()
    {
      super(Messages.getString("Net4jIntrospectorView_16")); //$NON-NLS-1$
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
          setObject(elements.peek());
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  class ModeAction extends Action
  {
    private ModeAction()
    {
      super(Messages.getString("Net4jIntrospectorView_17b"), AS_CHECK_BOX); //$NON-NLS-1$
      setImageDescriptor(SharedIcons.getDescriptor(SharedIcons.ETOOL_PART_MODE));
    }

    @Override
    public void run()
    {
      if (isChecked())
      {
        elements.clear();
        setObject(activePart);
      }
      else
      {
        setObject(null);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  class ContainerAction extends Action
  {
    private ContainerAction()
    {
      super(Messages.getString("Net4jIntrospectorView_17")); //$NON-NLS-1$
      setImageDescriptor(SharedIcons.getDescriptor(SharedIcons.VIEW_CONTAINER));
    }

    @Override
    public void run()
    {
      setObject(IPluginContainer.INSTANCE);
    }
  }

  /**
   * @author Eike Stepper
   */
  class RefreshAction extends Action
  {
    private RefreshAction()
    {
      super("Refresh"); //$NON-NLS-1$
      setImageDescriptor(SharedIcons.getDescriptor(SharedIcons.ETOOL_REFRESH));
    }

    @Override
    public void run()
    {
      refreshViewer();
    }
  }

  /**
   * @author Eike Stepper
   */
  abstract class AbstractContentProvider implements IStructuredContentProvider
  {
    @Override
    public void inputChanged(Viewer v, Object oldInput, Object newInput)
    {
    }

    @Override
    public void dispose()
    {
    }
  }

  /**
   * @author Eike Stepper
   */
  abstract class AbstractLabelProvider extends LabelProvider implements ITableLabelProvider
  {
    @Override
    public String getText(Object element)
    {
      return getColumnText(element, 0);
    }

    @Override
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
  class ObjectContentProvider extends AbstractContentProvider
  {
    @Override
    public Object[] getElements(Object parent)
    {
      if (!elements.isEmpty())
      {
        try
        {
          return ReflectUtil.dumpToArray(elements.peek());
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
  class ObjectLabelProvider extends AbstractLabelProvider
  {
    @Override
    public String getColumnText(Object obj, int index)
    {
      if (obj instanceof Pair<?, ?>)
      {
        try
        {
          @SuppressWarnings("unchecked")
          Pair<Field, Object> pair = (Pair<Field, Object>)obj;

          Field field = pair.getElement1();
          Object value = pair.getElement2();

          switch (index)
          {
          case 0:
            return field.getName();
          case 1:
            return value == null ? Messages.getString("Net4jIntrospectorView_18") : value.toString(); //$NON-NLS-1$
          case 2:
            return field.getType().getName();
          case 3:
            return value == null ? Messages.getString("Net4jIntrospectorView_1") : value.getClass().getName(); //$NON-NLS-1$
          }
        }
        catch (RuntimeException ex)
        {
          OM.LOG.error(ex);
        }
      }

      return ""; //$NON-NLS-1$
    }
  }

  /**
   * @author Eike Stepper
   */
  class IterableContentProvider extends AbstractContentProvider
  {
    @Override
    public Object[] getElements(Object parent)
    {
      if (!elements.isEmpty())
      {
        Object element = elements.peek();
        if (element instanceof Iterable<?>)
        {
          List<Object> result = new ArrayList<>();
          for (Object object : (Iterable<?>)element)
          {
            result.add(object);
          }

          return result.toArray();
        }
      }

      return NO_ELEMENTS;
    }
  }

  /**
   * @author Eike Stepper
   */
  class IterableLabelProvider extends AbstractLabelProvider
  {
    @Override
    public String getColumnText(Object obj, int index)
    {
      switch (index)
      {
      case 0:
        return obj == null ? Messages.getString("Net4jIntrospectorView_21") : obj.toString(); //$NON-NLS-1$
      case 1:
        return obj == null ? Messages.getString("Net4jIntrospectorView_22") : obj.getClass().getName(); //$NON-NLS-1$
      }

      return ""; //$NON-NLS-1$
    }
  }

  /**
   * @author Eike Stepper
   */
  class ArrayContentProvider extends AbstractContentProvider
  {
    @Override
    @SuppressWarnings("unchecked")
    public Object[] getElements(Object parent)
    {
      if (!elements.isEmpty())
      {
        Object element = elements.peek();
        if (element.getClass().isArray())
        {
          Object[] array = (Object[])element;
          Pair<Integer, Object>[] result = new Pair[array.length];
          for (int i = 0; i < array.length; i++)
          {
            result[i] = Pair.create(i, array[i]);
          }

          return result;
        }
      }

      return NO_ELEMENTS;
    }
  }

  /**
   * @author Eike Stepper
   */
  class ArrayLabelProvider extends AbstractLabelProvider
  {
    @Override
    public String getColumnText(Object obj, int index)
    {
      if (obj instanceof Pair<?, ?>)
      {
        try
        {
          @SuppressWarnings("unchecked")
          Pair<Integer, Object> pair = (Pair<Integer, Object>)obj;

          int i = pair.getElement1();
          Object value = pair.getElement2();
          switch (index)
          {
          case 0:
            return String.valueOf(i);
          case 1:
            return value == null ? Messages.getString("Net4jIntrospectorView_24") : value.toString(); //$NON-NLS-1$
          case 2:
            return value == null ? Messages.getString("Net4jIntrospectorView_25") : value.getClass().getName(); //$NON-NLS-1$
          }
        }
        catch (RuntimeException ex)
        {
          OM.LOG.error(ex);
        }
      }

      return ""; //$NON-NLS-1$
    }
  }

  /**
   * @author Eike Stepper
   */
  class MapContentProvider extends AbstractContentProvider
  {
    @Override
    public Object[] getElements(Object parent)
    {
      if (!elements.isEmpty())
      {
        Object element = elements.peek();
        if (element instanceof Map<?, ?>)
        {
          return ((Map<?, ?>)element).entrySet().toArray();
        }
      }

      return NO_ELEMENTS;
    }
  }

  /**
   * @author Eike Stepper
   */
  class MapLabelProvider extends AbstractLabelProvider
  {
    @Override
    public String getColumnText(Object obj, int index)
    {
      if (obj instanceof Map.Entry<?, ?>)
      {
        Map.Entry<?, ?> entry = (Map.Entry<?, ?>)obj;
        Object key = entry.getKey();
        Object value = entry.getValue();
        switch (index)
        {
        case 0:
          return key == null ? Messages.getString("Net4jIntrospectorView_27") : key.toString(); //$NON-NLS-1$
        case 1:
          return value == null ? Messages.getString("Net4jIntrospectorView_28") : value.toString(); //$NON-NLS-1$
        case 2:
          return value == null ? Messages.getString("Net4jIntrospectorView_29") : value.getClass().getName(); //$NON-NLS-1$
        }
      }

      return ""; //$NON-NLS-1$
    }
  }

  /**
   * @author Eike Stepper
   */
  class NameComparator extends ViewerComparator
  {
  }
}
