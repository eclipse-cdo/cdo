/*
 * Copyright (c) 2007-2013, 2015, 2016, 2019-2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.internal.ui.views;

import org.eclipse.net4j.ui.shared.SharedIcons;
import org.eclipse.net4j.util.HexUtil;
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.ReflectUtil.Setting;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.internal.ui.bundle.OM;
import org.eclipse.net4j.util.internal.ui.messages.Messages;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.actions.ToggleAction;
import org.eclipse.net4j.util.ui.views.IntrospectionProvider;
import org.eclipse.net4j.util.ui.views.IntrospectionProvider.NameAndValue;
import org.eclipse.net4j.util.ui.views.RowIntrospectionProvider;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.ViewPart;

import java.lang.reflect.Array;
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
        setValue(activePart);
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
          setValue(null);
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

      Object pageSelection = getPageSelection(sel);
      if (pageSelection != lastPageSelection)
      {
        lastPageSelection = pageSelection;
        history.clear();

        if (linkSelectionAction.isChecked())
        {
          setValue(lastPageSelection);
        }
      }
    }

    private Object getPageSelection(ISelection sel)
    {
      if (sel.isEmpty())
      {
        return null;
      }

      if (sel instanceof IStructuredSelection)
      {
        IStructuredSelection ssel = (IStructuredSelection)sel;
        if (ssel.size() == 1)
        {
          return ssel.getFirstElement();
        }

        return ssel.toList();
      }

      return sel;
    }
  };

  private final History history = new History();

  private final Map<IntrospectionProvider, TableViewer> viewers = new HashMap<>();

  private final Map<IntrospectionProvider, MenuManager> menuManagers = new HashMap<>();

  private final List<IntrospectionProvider> providers = new ArrayList<>();

  private IntrospectionProvider currentProvider;

  private Object currentValue;

  private String currentName;

  private Text classLabel;

  private Text identityLabel;

  private Text objectLabel;

  private final IAction copyAction = new CopyAction();

  private final IAction backwardAction = new BackwardAction();

  private final IAction forwardAction = new ForwardAction();

  private final IAction logicalStructureAction = new LogicalStructureAction();

  private final IAction activePartAction = new ActivePartAction();

  private final IAction linkSelectionAction = new LinkSelectionAction();

  private final IAction containerAction = new ContainerAction();

  private final IAction refreshAction = new RefreshAction();

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

    detachListener();

    IWorkbenchPage page = getSite().getPage();
    page.removePartListener(partListener);
    page.removeSelectionListener(pageSelectionListener);
    super.dispose();
  }

  public Object getValue()
  {
    return currentValue;
  }

  public void setValue(Object value)
  {
    if (currentValue == value)
    {
      return;
    }

    detachListener();

    if (value == null)
    {
      classLabel.setText(""); //$NON-NLS-1$
      identityLabel.setText(""); //$NON-NLS-1$
      objectLabel.setText(""); //$NON-NLS-1$
    }
    else
    {
      Class<? extends Object> c = value.getClass();
      String className = c.isArray() ? c.getComponentType().getName() + "[]" : c.getName();
      classLabel.setText(className);

      String identity = HexUtil.identityHashCode(value, true);
      identityLabel.setText(identity);

      String label = getLabel(value, identity);
      objectLabel.setText(label);
    }

    classLabel.getParent().layout();
    currentValue = value;

    updateEnablements();
    updateProvider();

    attachListener();
  }

  @Override
  public void createPartControl(Composite parent)
  {
    Display display = parent.getDisplay();
    Color bg = display.getSystemColor(SWT.COLOR_LIST_BACKGROUND);
    Color gray = display.getSystemColor(SWT.COLOR_DARK_BLUE);

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

      viewer.addSelectionChangedListener(e -> {
        Object element = viewer.getStructuredSelection().getFirstElement();

        try
        {
          NameAndValue nameAndValue = currentProvider.getNameAndValue(element);
          if (nameAndValue != null)
          {
            currentName = nameAndValue.getName();
          }
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
        }
      });

      Control control = viewer.getControl();

      control.addListener(SWT.DefaultSelection, e -> {
        Object element = viewer.getStructuredSelection().getFirstElement();
        provider.open(e, currentValue, element, elem -> open(elem));
      });

      control.addKeyListener(KeyListener.keyPressedAdapter(e -> {
        if ((e.keyCode == SWT.ARROW_LEFT || e.character == SWT.BS) && backwardAction.isEnabled())
        {
          backwardAction.run();
        }
        else if (e.keyCode == SWT.ARROW_RIGHT && forwardAction.isEnabled())
        {
          forwardAction.run();
        }
      }));

      ViewerComparator comparator = provider.getComparator();
      if (comparator != null)
      {
        viewer.setComparator(comparator);
      }

      viewers.put(provider, viewer);
      menuManagers.put(provider, hookContextMenu(viewer));
    }

    linkSelectionAction.setEnabled(!activePartAction.isChecked());

    IActionBars bars = getViewSite().getActionBars();
    bars.setGlobalActionHandler(ActionFactory.COPY.getId(), copyAction);

    fillLocalPullDown(bars.getMenuManager());
    fillLocalToolBar(bars.getToolBarManager());

    IWorkbenchPage page = getSite().getPage();
    page.addPartListener(partListener);
    page.addSelectionListener(pageSelectionListener);

    setCurrentProvider(OBJECT_INTROSPECTION_PROVIDER);
    updateEnablements();
    instance = this;
  }

  @Override
  public void setFocus()
  {
    try
    {
      TableViewer currentViewer = getCurrentViewer();
      if (currentViewer != null)
      {
        currentViewer.getControl().setFocus();
      }
    }
    catch (RuntimeException ignore)
    {
    }
  }

  private void open(Object element)
  {
    try
    {
      NameAndValue nameAndValue = currentProvider.getNameAndValue(element);
      if (nameAndValue != null)
      {
        history.open(nameAndValue.getName(), currentValue);
        setValue(nameAndValue.getValue());
      }
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }
  }

  private void goBackward()
  {
    NameAndValue nameAndValue = history.goBackward(currentName, currentValue);
    if (nameAndValue != null)
    {
      setValue(nameAndValue.getValue());
      selectElement(currentValue, nameAndValue.getName());
    }
  }

  private void goForward()
  {
    NameAndValue nameAndValue = history.goForward(currentName, currentValue);
    if (nameAndValue != null)
    {
      setValue(nameAndValue.getValue());
      selectElement(currentValue, nameAndValue.getName());
    }
  }

  private void selectElement(Object parent, String name)
  {
    try
    {
      Object element = currentProvider.getElementByName(parent, name);
      if (element != null)
      {
        currentName = name;

        TableViewer viewer = getCurrentViewer();
        Display display = viewer.getControl().getDisplay();
        UIUtil.asyncExec(display, () -> viewer.setSelection(new StructuredSelection(element), true));
      }
      else
      {
        currentName = null;
      }
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }
  }

  private void refreshViewer()
  {
    UIUtil.refreshViewer(getCurrentViewer());
  }

  private String getLabel(Object value, String identity)
  {
    String str = StringUtil.safe(value);
    String prefix = value.getClass().getName() + "@";
    if (str.startsWith(prefix))
    {
      String text = str.substring(prefix.length());
      if (identity.endsWith(text))
      {
        text = StringUtil.EMPTY;
      }
      else if (text.startsWith(identity))
      {
        text = text.substring(identity.length());
      }

      return text.trim();
    }

    return str;
  }

  private TableViewer getCurrentViewer()
  {
    return viewers.get(currentProvider);
  }

  private void setCurrentProvider(IntrospectionProvider provider)
  {
    if (currentProvider != provider)
    {
      TableViewer currentViewer = getCurrentViewer();
      boolean hadFocus = currentViewer == null ? false : currentViewer.getControl().isFocusControl();

      currentProvider = provider;
      currentViewer = getCurrentViewer();

      MenuManager menuManager = menuManagers.get(currentProvider);
      getSite().registerContextMenu(menuManager, currentViewer);

      stackLayout.topControl = currentViewer.getControl();
      stacked.layout();

      if (hadFocus)
      {
        setFocus();
      }
    }
  }

  private void updateProvider()
  {
    IntrospectionProvider result = OBJECT_INTROSPECTION_PROVIDER;

    if (currentValue != null)
    {
      List<IntrospectionProvider> providersToUse = logicalStructureAction.isChecked() ? providers : DEFAULT_INTROSPECTION_PROVIDERS;

      for (IntrospectionProvider provider : providersToUse)
      {
        if (provider.canHandle(currentValue))
        {
          result = provider;
          break;
        }
      }
    }

    setCurrentProvider(result);
    refreshViewer();
  }

  private void updateEnablements()
  {
    backwardAction.setEnabled(history.canGoBackward());
    forwardAction.setEnabled(history.canGoForward());
  }

  private void attachListener()
  {
    if (currentValue != null)
    {
      TableViewer viewer = getCurrentViewer();
      if (viewer != null)
      {
        currentProvider.attachListener(viewer, currentValue);
      }
    }
  }

  private void detachListener()
  {
    if (currentValue != null)
    {
      TableViewer viewer = getCurrentViewer();
      if (viewer != null)
      {
        currentProvider.detachListener(viewer, currentValue);
      }
    }
  }

  private MenuManager hookContextMenu(TableViewer viewer)
  {
    MenuManager menuManager = new MenuManager("#PopupMenu");
    menuManager.setRemoveAllWhenShown(true);
    menuManager.addMenuListener(new IMenuListener()
    {
      @Override
      public void menuAboutToShow(IMenuManager manager)
      {
        fillContextMenu(manager);
      }
    });

    Control control = viewer.getControl();
    Menu menu = menuManager.createContextMenu(control);
    control.setMenu(menu);
    return menuManager;
  }

  private void fillContextMenu(IMenuManager manager)
  {
    manager.add(copyAction);
  }

  private void fillLocalPullDown(IMenuManager manager)
  {
    manager.add(containerAction);
    manager.add(refreshAction);
  }

  private void fillLocalToolBar(IToolBarManager manager)
  {
    manager.add(backwardAction);
    manager.add(forwardAction);
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
  private final class CopyAction extends Action
  {
    public CopyAction()
    {
      super(Messages.getString("Net4jIntrospectorView_32")); //$NON-NLS-1$
      setImageDescriptor(SharedIcons.getDescriptor(SharedIcons.ETOOL_COPY));
      setActionDefinitionId(IWorkbenchCommandConstants.EDIT_COPY);
    }

    @Override
    public void run()
    {
      TableViewer viewer = getCurrentViewer();
      StringBuilder builder = new StringBuilder();
      for (Object element : viewer.getStructuredSelection())
      {
        try
        {
          NameAndValue nameAndValue = currentProvider.getNameAndValue(element);
          if (nameAndValue != null)
          {
            StringUtil.appendSeparator(builder, StringUtil.NL);
            builder.append(nameAndValue);
          }
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
        }
      }

      Display display = viewer.getControl().getDisplay();
      UIUtil.copyToClipboard(display, builder.toString());
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class BackwardAction extends Action
  {
    public BackwardAction()
    {
      super(Messages.getString("Net4jIntrospectorView_16")); //$NON-NLS-1$
      ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
      setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_BACK));
      setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_BACK_DISABLED));
    }

    @Override
    public void run()
    {
      goBackward();
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class ForwardAction extends Action
  {
    public ForwardAction()
    {
      super(Messages.getString("Net4jIntrospectorView_16b")); //$NON-NLS-1$
      ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
      setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_BACK));
      setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD));
      setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD_DISABLED));
    }

    @Override
    public void run()
    {
      goForward();
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
      updateProvider();
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
        setValue(activePart);
      }
      else
      {
        setValue(lastPageSelection);
      }

      linkSelectionAction.setEnabled(!checked);
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
        setValue(lastPageSelection);
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
      setValue(IPluginContainer.INSTANCE);
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
      if (currentValue != null && provider.canHandle(currentValue))
      {
        try
        {
          Object[] result = provider.getElements(currentValue);
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
    private final Stack<NameAndValue> backward = new Stack<>();

    private final Stack<NameAndValue> forward = new Stack<>();

    public History()
    {
    }

    public void open(String name, Object value)
    {
      NameAndValue toPush = new NameAndValue(name, value);

      if (!forward.isEmpty())
      {
        NameAndValue peeked = forward.peek();
        if (peeked.equals(toPush))
        {
          forward.pop();
        }
        else
        {
          forward.clear();
        }
      }

      backward.push(toPush);
    }

    public boolean canGoBackward()
    {
      return !backward.isEmpty();
    }

    public boolean canGoForward()
    {
      return !forward.isEmpty();
    }

    public NameAndValue goBackward(String currentName, Object currentObject)
    {
      if (backward.isEmpty())
      {
        return null;
      }

      forward.push(new NameAndValue(currentName, currentObject));
      return backward.pop();
    }

    public NameAndValue goForward(String currentName, Object currentObject)
    {
      if (forward.isEmpty())
      {
        return null;
      }

      backward.push(new NameAndValue(currentName, currentObject));
      return forward.pop();
    }

    public void clear()
    {
      backward.clear();
      forward.clear();
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
      int length = Array.getLength(parent);
      NameAndValue[] result = new NameAndValue[length];

      for (int index = 0; index < length; index++)
      {
        Object value = Array.get(parent, index);
        result[index] = new NameAndValue(index, value);
      }

      return result;
    }

    @Override
    public Object getElementByName(Object parent, String name) throws Exception
    {
      int index;

      try
      {
        index = Integer.parseInt(name);
      }
      catch (NumberFormatException ex)
      {
        return null;
      }

      Object element = Array.get(parent, index);
      return new NameAndValue(index, element);
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
      ReflectUtil.dump(parent, setting -> rows.add(createRow(setting)));
    }

    @Override
    public Row getElementByName(Object parent, String name) throws Exception
    {
      Row[] result = { null };

      ReflectUtil.dump(parent, setting -> {
        if (setting.getName().equals(name))
        {
          result[0] = createRow(setting);
          return false;
        }

        return true;
      });

      return result[0];
    }

    private static Row createRow(Setting setting)
    {
      Object value = setting.getValue();
      return new Row(setting.getName(), value, getName(setting.getType()), getClassName(value));
    }
  }
}
