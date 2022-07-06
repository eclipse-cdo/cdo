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
import org.eclipse.net4j.util.ui.views.IntrospectionProvider;
import org.eclipse.net4j.util.ui.views.RowIntrospectionProvider;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IColorProvider;
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
import java.util.Arrays;
import java.util.HashMap;
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

  private static final IntrospectionProvider ARRAY_INTROSPECTION_PROVIDER = new ArrayIntrospectionProvider();

  private static final IntrospectionProvider OBJECT_INTROSPECTION_PROVIDER = new ObjectIntrospectionProvider();

  private static final List<IntrospectionProvider> DEFAULT_INTROSPECTION_PROVIDERS = Arrays.asList(ARRAY_INTROSPECTION_PROVIDER, OBJECT_INTROSPECTION_PROVIDER);

  private static Net4jIntrospectorView instance;

  private final Stack<Object> elements = new Stack<>();

  private final Map<IntrospectionProvider, TableViewer> viewers = new HashMap<>();

  private final List<IntrospectionProvider> providers = new ArrayList<>();

  private IntrospectionProvider currentProvider;

  private Text classLabel;

  private Text identityLabel;

  private Text objectLabel;

  private IAction backAction = new BackAction();

  private IAction logicalStructureAction = new LogicalStructureAction();

  private IAction activePartAction = new ActivePartAction();

  private IAction containerAction = new ContainerAction();

  private IAction refreshAction = new RefreshAction();

  private StackLayout stackLayout;

  private Composite stacked;

  private IWorkbenchPart activePart;

  public Net4jIntrospectorView()
  {
    IPluginContainer.INSTANCE.forEachElement(IntrospectionProvider.Factory.PRODUCT_GROUP, IntrospectionProvider.class, providers::add);
    providers.sort(null);
    providers.addAll(DEFAULT_INTROSPECTION_PROVIDERS);
  }

  @Override
  public void dispose()
  {
    instance = null;

    IWorkbenchPage page = getSite().getPage();
    page.removePartListener(this);
    page.removeSelectionListener(this);
    super.dispose();
  }

  public Object getObject()
  {
    if (!elements.isEmpty())
    {
      return elements.peek();
    }

    return null;
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
      currentProvider = OBJECT_INTROSPECTION_PROVIDER;
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

    updateProvider(object);
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

    for (IntrospectionProvider provider : providers)
    {
      TableViewer viewer = provider.createViewer(stacked);
      provider.createColumns(viewer);
      viewer.addDoubleClickListener(this);
      viewer.setContentProvider(new IntrospectionContentProvider(provider));
      viewer.setLabelProvider(new IntrospectionLabelProvider(provider));
      viewer.setInput(getViewSite());

      ViewerComparator comparator = provider.getComparator();
      if (comparator != null)
      {
        viewer.setComparator(comparator);
      }

      viewers.put(provider, viewer);
    }

    IActionBars bars = getViewSite().getActionBars();
    fillLocalPullDown(bars.getMenuManager());
    fillLocalToolBar(bars.getToolBarManager());

    IWorkbenchPage page = getSite().getPage();
    page.addPartListener(this);
    page.addSelectionListener(this);

    setCurrentProvider(OBJECT_INTROSPECTION_PROVIDER);
    instance = this;
  }

  @Override
  public void partActivated(IWorkbenchPart part)
  {
    if (part == this)
    {
      return;
    }

    activePart = part;
    if (activePartAction.isChecked())
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
    if (activePartAction.isChecked())
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

    if (activePartAction.isChecked())
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
      Object object = null;

      try
      {
        object = currentProvider.getObject(element);
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }

      if (object != null)
      {
        setObject(object);
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
      TableViewer currentViewer = getCurrentViewer();
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

  private void refreshViewer()
  {
    UIUtil.refreshViewer(getCurrentViewer());
  }

  private TableViewer getCurrentViewer()
  {
    return viewers.get(currentProvider);
  }

  private void setCurrentProvider(IntrospectionProvider provider)
  {
    currentProvider = provider;
    stackLayout.topControl = getCurrentViewer().getControl();
    stacked.layout();
  }

  private void updateProvider(Object object)
  {
    if (object != null)
    {
      List<IntrospectionProvider> providersToUse = logicalStructureAction.isChecked() ? providers : DEFAULT_INTROSPECTION_PROVIDERS;

      for (IntrospectionProvider provider : providersToUse)
      {
        if (provider.canHandle(object))
        {
          setCurrentProvider(provider);
          break;
        }
      }
    }

    refreshViewer();
  }

  private void fillLocalPullDown(IMenuManager manager)
  {
    manager.add(containerAction);
    manager.add(refreshAction);
  }

  private void fillLocalToolBar(IToolBarManager manager)
  {
    manager.add(backAction);
    manager.add(new Separator());
    manager.add(logicalStructureAction);
    manager.add(activePartAction);
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

  /**
   * @author Eike Stepper
   */
  private final class BackAction extends Action
  {
    public BackAction()
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
  private final class LogicalStructureAction extends Action
  {
    public LogicalStructureAction()
    {
      super(Messages.getString("Net4jIntrospectorView_30"), AS_CHECK_BOX); //$NON-NLS-1$
      setImageDescriptor(OM.getImageDescriptor("icons/logical_structure.png"));
      setChecked(OM.PREF_LOGICAL_STRUCTURE.getValue());
    }

    @Override
    public void run()
    {
      OM.PREF_LOGICAL_STRUCTURE.setValue(isChecked());
      updateProvider(getObject());
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class ActivePartAction extends Action
  {
    public ActivePartAction()
    {
      super(Messages.getString("Net4jIntrospectorView_17b"), AS_CHECK_BOX); //$NON-NLS-1$
      setImageDescriptor(SharedIcons.getDescriptor(SharedIcons.ETOOL_PART_MODE));
      setChecked(OM.PREF_ACTIVE_PART.getValue());
    }
  
    @Override
    public void run()
    {
      boolean checked = isChecked();
      OM.PREF_ACTIVE_PART.setValue(checked);
  
      if (checked)
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
  private final class ContainerAction extends Action
  {
    public ContainerAction()
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
  private final class RefreshAction extends Action
  {
    public RefreshAction()
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
  private final class IntrospectionContentProvider implements IStructuredContentProvider
  {
    private final IntrospectionProvider provider;

    public IntrospectionContentProvider(IntrospectionProvider provider)
    {
      this.provider = provider;
    }

    @Override
    public Object[] getElements(Object parent)
    {
      if (!elements.isEmpty())
      {
        Object element = elements.peek();

        try
        {
          Object[] result = provider.getElements(element);
          if (result != null)
          {
            return result;
          }
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
        }
      }

      return NO_ELEMENTS;
    }

    @Override
    public void inputChanged(Viewer v, Object oldInput, Object newInput)
    {
      // Do nothing.
    }

    @Override
    public void dispose()
    {
      // Do nothing.
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class IntrospectionLabelProvider extends LabelProvider implements ITableLabelProvider, IColorProvider
  {
    private final IntrospectionProvider provider;

    public IntrospectionLabelProvider(IntrospectionProvider provider)
    {
      this.provider = provider;
    }

    @Override
    public String getColumnText(Object element, int index)
    {
      try
      {
        String result = provider.getColumnText(element, index);
        if (result != null)
        {
          return result;
        }
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }

      return ""; //$NON-NLS-1$
    }

    @Override
    public Image getColumnImage(Object element, int index)
    {
      try
      {
        return provider.getColumnImage(element, index);
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }

      return null;
    }

    @Override
    public String getText(Object element)
    {
      return getColumnText(element, 0);
    }

    @Override
    public Image getImage(Object element)
    {
      return getColumnImage(element, 0);
    }

    @Override
    public Color getForeground(Object element)
    {
      return provider.getForeground(element);
    }

    @Override
    public Color getBackground(Object element)
    {
      return provider.getBackground(element);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class ArrayIntrospectionProvider extends IntrospectionProvider
  {
    public ArrayIntrospectionProvider()
    {
      super("java.lang.Array", "Array");
    }
  
    @Override
    public int getPriority()
    {
      return Integer.MAX_VALUE;
    }
  
    @Override
    public boolean canHandle(Object object)
    {
      return object.getClass().isArray();
    }
  
    @Override
    public void createColumns(TableViewer viewer)
    {
      createColumn(viewer, Messages.getString("Net4jIntrospectorView_13"), 50); //$NON-NLS-1$
      createColumn(viewer, Messages.getString("Net4jIntrospectorView_14"), 400); //$NON-NLS-1$
      createColumn(viewer, Messages.getString("Net4jIntrospectorView_15"), 300); //$NON-NLS-1$
    }
  
    @Override
    public Object[] getElements(Object parent) throws Exception
    {
      if (parent instanceof Object[])
      {
        Object[] array = (Object[])parent;
  
        @SuppressWarnings("unchecked")
        Pair<Integer, Object>[] result = new Pair[array.length];
  
        for (int i = 0; i < array.length; i++)
        {
          result[i] = Pair.create(i, array[i]);
        }
  
        return result;
      }
  
      return null;
    }
  
    @Override
    public Object getObject(Object element) throws Exception
    {
      @SuppressWarnings("unchecked")
      Pair<Integer, Object> pair = (Pair<Integer, Object>)element;
      return pair.getElement2();
    }
  
    @Override
    public String getColumnText(Object element, int index) throws Exception
    {
      @SuppressWarnings("unchecked")
      Pair<Integer, Object> pair = (Pair<Integer, Object>)element;
  
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
  
      default:
        return null;
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class ObjectIntrospectionProvider extends RowIntrospectionProvider
  {
    public ObjectIntrospectionProvider()
    {
      super("java.lang.Object", "Object");
    }

    @Override
    public int getPriority()
    {
      return Integer.MAX_VALUE;
    }

    @Override
    public boolean canHandle(Object object)
    {
      return true;
    }

    @Override
    protected void fillRows(Object parent, List<Row> rows) throws Exception
    {
      for (Pair<Field, Object> pair : ReflectUtil.dumpToArray(parent))
      {
        Field field = pair.getElement1();
        Object value = pair.getElement2();

        rows.add(new Row(field.getName(), value, field.getType().getName(),
            value == null ? Messages.getString("Net4jIntrospectorView_1") : value.getClass().getName()));
      }
    }
  }
}
