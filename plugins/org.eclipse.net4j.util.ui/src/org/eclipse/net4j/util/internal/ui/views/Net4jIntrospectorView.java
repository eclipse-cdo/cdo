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
import org.eclipse.net4j.util.ui.actions.ToggleAction;
import org.eclipse.net4j.util.ui.views.IntrospectionProvider;
import org.eclipse.net4j.util.ui.views.IntrospectionProvider.NameAndValue;
import org.eclipse.net4j.util.ui.views.RowIntrospectionProvider;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
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
public class Net4jIntrospectorView extends ViewPart
{
  public static final String VIEW_ID = "org.eclipse.net4j.util.Net4jIntrospectorView"; //$NON-NLS-1$

  private static final Object[] NO_ELEMENTS = {};

  private static final IntrospectionProvider ARRAY_INTROSPECTION_PROVIDER = new ArrayIntrospectionProvider();

  private static final IntrospectionProvider OBJECT_INTROSPECTION_PROVIDER = new ObjectIntrospectionProvider();

  private static final List<IntrospectionProvider> DEFAULT_INTROSPECTION_PROVIDERS = Arrays.asList(ARRAY_INTROSPECTION_PROVIDER, OBJECT_INTROSPECTION_PROVIDER);

  private static Net4jIntrospectorView instance;

  private final IPartListener partListener = new IPartListener()
  {
    @Override
    public void partActivated(IWorkbenchPart part)
    {
      if (part == Net4jIntrospectorView.this)
      {
        return;
      }

      activePart = part;
      if (activePartAction.isChecked())
      {
        history.clear();
        setObject(activePart);
      }
    }

    @Override
    public void partDeactivated(IWorkbenchPart part)
    {
      if (activePartAction.isChecked())
      {
        IWorkbenchPart newPart = part.getSite().getPage().getActivePart();
        if (newPart == Net4jIntrospectorView.this)
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
      // Do nothing.
    }

    @Override
    public void partClosed(IWorkbenchPart part)
    {
      // Do nothing.
    }

    @Override
    public void partBroughtToTop(IWorkbenchPart part)
    {
      // Do nothing.
    }
  };

  private final ISelectionListener pageSelectionListener = new ISelectionListener()
  {
    @Override
    public void selectionChanged(IWorkbenchPart part, ISelection sel)
    {
      if (part == Net4jIntrospectorView.this)
      {
        return;
      }

      if (activePartAction.isChecked())
      {
        return;
      }

      Object pageSelection = null;
      if (sel instanceof IStructuredSelection)
      {
        IStructuredSelection ssel = (IStructuredSelection)sel;
        pageSelection = ssel.getFirstElement();
      }

      if (pageSelection != lastPageSelection)
      {
        lastPageSelection = pageSelection;
        history.clear();

        if (linkSelectionAction.isChecked())
        {
          setObject(lastPageSelection);
        }
      }
    }
  };

  private final IListener elementListener = new IListener()
  {
    @Override
    public void notifyEvent(IEvent event)
    {
      refreshViewer();
    }
  };

  private final History history = new History();

  private final Map<IntrospectionProvider, TableViewer> viewers = new HashMap<>();

  private final List<IntrospectionProvider> providers = new ArrayList<>();

  private IntrospectionProvider currentProvider;

  private Object currentObject;

  private Text classLabel;

  private Text identityLabel;

  private Text objectLabel;

  private IAction backAction = new BackAction();

  private IAction linkSelectionAction = new LinkSelectionAction();

  private IAction logicalStructureAction = new LogicalStructureAction();

  private IAction activePartAction = new ActivePartAction();

  private IAction containerAction = new ContainerAction();

  private IAction refreshAction = new RefreshAction();

  private StackLayout stackLayout;

  private Composite stacked;

  private IWorkbenchPart activePart;

  private Object lastPageSelection;

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
    page.removePartListener(partListener);
    page.removeSelectionListener(pageSelectionListener);
    super.dispose();
  }

  public Object getObject()
  {
    NameAndValue nameAndValue = history.peek();
    if (nameAndValue == null)
    {
      return null;
    }

    return nameAndValue.getValue();
  }

  public void setObject(Object object)
  {
    Object oldObject = currentObject;
    if (oldObject == object)
    {
      return;
    }

    if (oldObject != null)
    {
      EventUtil.removeListener(oldObject, elementListener);
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
      EventUtil.addListener(object, elementListener);

      Class<? extends Object> c = object.getClass();
      String className = c.isArray() ? c.getComponentType().getName() + "[]" : c.getName();
      classLabel.setText(className);

      String identity = HexUtil.identityHashCode(object, true);
      identityLabel.setText(identity);

      String value = StringUtil.safe(object);
      String prefix = c.getName() + "@";
      if (value.startsWith(prefix))
      {
        String text = value.substring(prefix.length());
        if (identity.endsWith(text))
        {
          text = StringUtil.EMPTY;
        }
        else if (text.startsWith(identity))
        {
          text = text.substring(identity.length());
        }

        objectLabel.setText(text.trim());
      }
      else
      {
        objectLabel.setText(value);
      }
    }

    classLabel.getParent().layout();
    backAction.setEnabled(!history.isEmpty());

    currentObject = object;
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
      viewer.setContentProvider(new IntrospectionContentProvider(provider));
      viewer.setLabelProvider(new IntrospectionLabelProvider(provider));
      viewer.setInput(getViewSite());

      viewer.addDoubleClickListener(e -> {
        ISelection sel = e.getSelection();
        if (sel instanceof IStructuredSelection)
        {
          IStructuredSelection ssel = (IStructuredSelection)sel;

          Object element = ssel.getFirstElement();
          drillIn(element);
        }
      });

      viewer.getControl().addKeyListener(KeyListener.keyPressedAdapter(e -> {
        if (e.character == SWT.BS)
        {
          backAction.run();
        }
      }));

      ViewerComparator comparator = provider.getComparator();
      if (comparator != null)
      {
        viewer.setComparator(comparator);
      }

      viewers.put(provider, viewer);
    }

    linkSelectionAction.setEnabled(!activePartAction.isChecked());

    IActionBars bars = getViewSite().getActionBars();
    fillLocalPullDown(bars.getMenuManager());
    fillLocalToolBar(bars.getToolBarManager());

    IWorkbenchPage page = getSite().getPage();
    page.addPartListener(partListener);
    page.addSelectionListener(pageSelectionListener);

    setCurrentProvider(OBJECT_INTROSPECTION_PROVIDER);
    instance = this;
  }

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

  private void drillIn(Object element)
  {
    try
    {
      NameAndValue nameAndValue = currentProvider.getNameAndValue(element);
      if (nameAndValue != null)
      {
        history.push(nameAndValue.getName(), currentObject);
        setObject(nameAndValue.getValue());
      }
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }
  }

  private void drillOut()
  {
    NameAndValue nameAndValue = history.pop();
    if (nameAndValue != null)
    {
      setObject(nameAndValue.getValue());

      try
      {
        Object element = currentProvider.getElementByName(currentObject, nameAndValue.getName());
        if (element != null)
        {
          TableViewer viewer = getCurrentViewer();
          Display display = viewer.getControl().getDisplay();
          UIUtil.asyncExec(display, () -> viewer.setSelection(new StructuredSelection(element), true));
        }
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }
    }
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
    setFocus();
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
    manager.add(linkSelectionAction);
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
      drillOut();
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class LinkSelectionAction extends ToggleAction
  {
    public LinkSelectionAction()
    {
      super(Messages.getString("Net4jIntrospectorView_31"), //$NON-NLS-1$
          SharedIcons.getDescriptor(SharedIcons.ETOOL_LINK_WITH_EDITOR), //
          OM.PREF_LINK_SELECTION);
    }

    @Override
    protected void run(boolean checked)
    {
      if (checked)
      {
        setObject(lastPageSelection);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class LogicalStructureAction extends ToggleAction
  {
    public LogicalStructureAction()
    {
      super(Messages.getString("Net4jIntrospectorView_30"), //$NON-NLS-1$
          OM.getImageDescriptor("icons/logical_structure.png"), //$NON-NLS-1$
          OM.PREF_LOGICAL_STRUCTURE);
    }

    @Override
    protected void run(boolean checked)
    {
      updateProvider(getObject());
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class ActivePartAction extends ToggleAction
  {
    public ActivePartAction()
    {
      super(Messages.getString("Net4jIntrospectorView_17b"), //$NON-NLS-1$
          SharedIcons.getDescriptor(SharedIcons.ETOOL_PART_MODE), //
          OM.PREF_ACTIVE_PART);
    }

    @Override
    protected void run(boolean checked)
    {
      if (checked)
      {
        history.clear();
        setObject(activePart);
      }
      else
      {
        setObject(lastPageSelection);
      }

      linkSelectionAction.setEnabled(!checked);
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
      if (currentObject != null)
      {
        try
        {
          Object[] result = provider.getElements(currentObject);
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
  private static final class History
  {
    private final Stack<NameAndValue> stack = new Stack<>();

    public History()
    {
    }

    public boolean isEmpty()
    {
      return stack.isEmpty();
    }

    public NameAndValue peek()
    {
      if (stack.isEmpty())
      {
        return null;
      }

      return stack.peek();
    }

    public void push(String name, Object value)
    {
      stack.push(new NameAndValue(name, value));
    }

    public NameAndValue pop()
    {
      if (stack.isEmpty())
      {
        return null;
      }

      return stack.pop();
    }

    public void clear()
    {
      stack.clear();
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
        NameAndValue[] result = new NameAndValue[array.length];

        for (int index = 0; index < array.length; index++)
        {
          result[index] = new NameAndValue(index, array[index]);
        }

        return result;
      }

      return null;
    }

    @Override
    public Object getElementByName(Object parent, String name) throws Exception
    {
      Object[] array = (Object[])parent;
      int index = Integer.parseInt(name);
      return new NameAndValue(index, array[index]);
    }

    @Override
    public NameAndValue getNameAndValue(Object element) throws Exception
    {
      return (NameAndValue)element;
    }

    @Override
    public String getColumnText(Object element, int index) throws Exception
    {
      NameAndValue nameAndValue = (NameAndValue)element;

      switch (index)
      {
      case 0:
        return nameAndValue.getName();
      case 1:
        return formatValue(nameAndValue.getValue());
      case 2:
        return getClassName(nameAndValue.getValue());

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
        rows.add(createRow(pair));
      }
    }

    @Override
    public Row getElementByName(Object parent, String name) throws Exception
    {
      for (Pair<Field, Object> pair : ReflectUtil.dumpToArray(parent))
      {
        if (pair.getElement1().getName().equals(name))
        {
          return createRow(pair);
        }
      }

      return null;
    }

    private static Row createRow(Pair<Field, Object> pair)
    {
      Field field = pair.getElement1();
      Object value = pair.getElement2();

      return new Row(field.getName(), value, getName(field.getType()), getClassName(value));
    }
  }
}
