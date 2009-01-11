/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.internal.ui.views;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.internal.ui.actions.CDOObjectFilterAction;
import org.eclipse.emf.cdo.internal.ui.actions.RemoveAllContainerItemAction;
import org.eclipse.emf.cdo.internal.ui.actions.RemoveContainerItemAction;
import org.eclipse.emf.cdo.internal.ui.dnd.CDOObjectDropAdapter;
import org.eclipse.emf.cdo.internal.ui.filters.CDOStateFilter;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;

import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.actions.LongRunningAction;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.notify.impl.BasicNotifierImpl.EAdapterList;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.EMFEditPlugin;
import org.eclipse.emf.edit.provider.ChangeNotifier;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IChangeNotifier;
import org.eclipse.emf.edit.provider.IDisposable;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheetPage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Victor Roldan Betancort
 */
public class CDOSubscriptionsView extends ViewPart implements ISelectionProvider
{
  private TableViewer viewer;

  private final String[] columnNames = { "Object", "URI", "State", "Changes" };

  private final int[] columnWidths = { 100, 300, 75, 250 };

  private CDOObjectContainer container = new CDOObjectContainer();

  private ComposedAdapterFactory adapterFactory;

  private WatchListLabelProvider labelProvider;

  private PropertySheetPage propertySheetPage;

  private IAction removeAction;

  private IAction removeAllAction;

  private IAction resetNotificationAction;

  private CDOObjectFilterAction filterAction;

  public CDOSubscriptionsView()
  {
    adapterFactory = new ComposedAdapterFactory(EMFEditPlugin.getComposedAdapterFactoryDescriptorRegistry());
    // adapterFactory.addAdapterFactory(new ResourceItemProviderAdapterFactory());
    // adapterFactory.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());
    adapterFactory.addAdapterFactory(new CDOObjectContainerAdapterFactory());
  }

  @Override
  public void createPartControl(Composite parent)
  {
    // Create Viewer
    getSite().setSelectionProvider(this);
    viewer = createViewer(parent);

    // Configure ViewPart
    createActions();
    createToolbarButtons();
    createContextMenu();
    hookKeyboardActions();
    createViewPulldownMenu();
  }

  private void createActions()
  {
    ISharedImages platformImages = PlatformUI.getWorkbench().getSharedImages();
    removeAction = new RemoveContainerItemAction<CDOObject>(container, viewer);
    removeAction.setText("Remove");
    removeAction.setImageDescriptor(platformImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
    removeAction.setDisabledImageDescriptor(platformImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE_DISABLED));
    removeAction.setToolTipText("Remove selected subscriptions");

    removeAllAction = new RemoveAllContainerItemAction<CDOObject>(container);
    removeAllAction.setText("Remove All");
    removeAllAction.setImageDescriptor(platformImages.getImageDescriptor(ISharedImages.IMG_OBJS_ERROR_TSK));
    removeAllAction.setToolTipText("Remove all subscriptions");

    resetNotificationAction = new ResetNotificationAction();
    resetNotificationAction.setText("Reset Changes");
    resetNotificationAction.setImageDescriptor(platformImages.getImageDescriptor(ISharedImages.IMG_TOOL_UNDO));
    resetNotificationAction.setDisabledImageDescriptor(platformImages
        .getImageDescriptor(ISharedImages.IMG_TOOL_UNDO_DISABLED));
    resetNotificationAction.setToolTipText("Reset all change notifications from the selected subscriptions");
  }

  private void createToolbarButtons()
  {
    getViewSite().getActionBars().getToolBarManager().add(removeAction);
    removeAction.setEnabled(false);
    viewer.addSelectionChangedListener(new ISelectionChangedListener()
    {
      public void selectionChanged(SelectionChangedEvent event)
      {
        removeAction.setEnabled(!event.getSelection().isEmpty());
      }
    });

    getViewSite().getActionBars().getToolBarManager().add(resetNotificationAction);
    resetNotificationAction.setEnabled(false);
    viewer.addSelectionChangedListener(new ISelectionChangedListener()
    {
      public void selectionChanged(SelectionChangedEvent event)
      {
        resetNotificationAction.setEnabled(!event.getSelection().isEmpty());
      }
    });

    getViewSite().getActionBars().getToolBarManager().add(removeAllAction);
  }

  private void createViewPulldownMenu()
  {
    ISharedImages platformImages = PlatformUI.getWorkbench().getSharedImages();
    IMenuManager menu = getViewSite().getActionBars().getMenuManager();
    filterAction = new CDOObjectFilterAction(viewer, new CDOStateFilter(viewer));
    filterAction.setText("Filter...");
    filterAction.setImageDescriptor(platformImages.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
    menu.add(filterAction);
  }

  private void createContextMenu()
  {
    MenuManager menuMgr = new MenuManager("#PopupMenu");
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(new IMenuListener()
    {
      public void menuAboutToShow(IMenuManager m)
      {
        CDOSubscriptionsView.this.fillContextMenu(m);
      }
    });

    Menu menu = menuMgr.createContextMenu(viewer.getControl());
    viewer.getControl().setMenu(menu);
    getSite().registerContextMenu(menuMgr, viewer);
  }

  private void fillContextMenu(IMenuManager menuMgr)
  {
    boolean isEmpty = viewer.getSelection().isEmpty();
    removeAction.setEnabled(!isEmpty);
    menuMgr.add(removeAction);
    resetNotificationAction.setEnabled(!isEmpty);
    menuMgr.add(resetNotificationAction);
    menuMgr.add(removeAllAction);
    menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
  }

  private void hookKeyboardActions()
  {
    viewer.getControl().addKeyListener(new KeyAdapter()
    {
      @Override
      public void keyReleased(KeyEvent event)
      {
        handleKeyReleased(event);
      }
    });
  }

  protected void handleKeyReleased(KeyEvent event)
  {
    if (event.character == SWT.DEL && event.stateMask == 0)
    {
      removeAction.run();
    }
  }

  @Override
  public void setFocus()
  {
  }

  public ISelection getSelection()
  {
    if (viewer != null)
    {
      return viewer.getSelection();
    }

    return StructuredSelection.EMPTY;
  }

  public void setSelection(ISelection selection)
  {
    // Doesn't need to set viewer.setSelection(). Already done with user event.
  }

  public void addSelectionChangedListener(ISelectionChangedListener listener)
  {
    if (viewer != null)
    {
      viewer.addSelectionChangedListener(listener);
    }
  }

  public void removeSelectionChangedListener(ISelectionChangedListener listener)
  {
    if (viewer != null)
    {
      viewer.removeSelectionChangedListener(listener);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public Object getAdapter(Class adapter)
  {
    if (adapter.equals(IPropertySheetPage.class))
    {
      return getPropertySheetPage();
    }

    return super.getAdapter(adapter);
  }

  public IPropertySheetPage getPropertySheetPage()
  {
    if (propertySheetPage == null)
    {
      propertySheetPage = new PropertySheetPage();
      propertySheetPage.setPropertySourceProvider(new AdapterFactoryContentProvider(adapterFactory));
    }

    return propertySheetPage;
  }

  private TableViewer createViewer(Composite parent)
  {
    TableViewer viewer = new TableViewer(parent, SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
    viewer.getTable().setLayoutData(UIUtil.createGridData());
    viewer.getTable().setHeaderVisible(true);
    viewer.getTable().setLinesVisible(true);
    createColumns(viewer.getTable(), columnNames, columnWidths);
    viewer.setContentProvider(new AdapterFactoryContentProvider(adapterFactory));
    labelProvider = new WatchListLabelProvider(adapterFactory);
    viewer.setLabelProvider(labelProvider);
    viewer.setInput(container);
    CDOObjectDropAdapter.support(viewer);
    return viewer;
  }

  private void refreshViewer()
  {
    try
    {
      viewer.getControl().getDisplay().asyncExec(new Runnable()
      {
        public void run()
        {
          try
          {
            viewer.refresh();
          }
          catch (RuntimeException ignore)
          {
            // Do nothing
          }
        }
      });
    }
    catch (RuntimeException ignore)
    {
      // Do nothing
    }
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

  /**
   * @author Eike Stepper
   */
  private final class CDOSubscriptionAdapter extends AdapterImpl implements CDOAdapterPolicy
  {
    public CDOSubscriptionAdapter()
    {
    }

    @Override
    public void notifyChanged(Notification msg)
    {
      labelProvider.addNotification(msg);
      refreshViewer();
    }

    public boolean isValid(EObject object, Adapter adapter)
    {
      return adapter instanceof CDOSubscriptionAdapter;
    }
  }

  /**
   * Since we want to get the benefits of the usage of AdapterFactoryLabelProvider and AdapterFactoryContentProvider,
   * this class extends Notifier so it can be adapted by <code>ComposedAdapterFactory</code>. See
   * ComposedAdapterFactory.adapt(Object target, Object type).
   * 
   * @author Victor Roldan Betancort
   */
  private final class CDOObjectContainer extends Container<CDOObject> implements Notifier,
      IContainer.Modifiable<CDOObject>
  {
    private BasicEList<Adapter> eAdapters;

    private Set<CDOObject> watchedObjects = new HashSet<CDOObject>();

    private CDOSubscriptionAdapter subscriptionAdapter = new CDOSubscriptionAdapter();

    public CDOObjectContainer()
    {
    }

    public CDOObject[] getElements()
    {
      return watchedObjects.toArray(new CDOObject[watchedObjects.size()]);
    }

    @Override
    public boolean isEmpty()
    {
      return watchedObjects.isEmpty();
    }

    public EList<Adapter> eAdapters()
    {
      if (eAdapters == null)
      {
        eAdapters = new EAdapterList<Adapter>(this);
      }

      return eAdapters;
    }

    public boolean eDeliver()
    {
      return false;
    }

    public void eNotify(Notification notification)
    {
      // Do nothing
    }

    public void eSetDeliver(boolean deliver)
    {
      // Do nothing
    }

    public void addElement(CDOObject element)
    {
      if (watchedObjects.add(element))
      {
        element.cdoView().options().addChangeSubscriptionPolicy(subscriptionAdapter);
        element.eAdapters().add(subscriptionAdapter);
        refreshViewer();
      }
    }

    public void removeElement(CDOObject element)
    {
      if (watchedObjects.remove(element))
      {
        element.eAdapters().remove(subscriptionAdapter);
        refreshViewer();
      }
    }
  }

  /**
   * @author Victor Roldan Betancort
   */
  private static final class CDOObjectContainerAdapterFactory extends AdapterFactoryImpl implements
      ComposeableAdapterFactory, IChangeNotifier, IDisposable
  {
    private static final Package viewsPackage = CDOSubscriptionsView.class.getPackage();

    private ComposedAdapterFactory parentAdapterFactory;

    private IChangeNotifier changeNotifier = new ChangeNotifier();

    private Collection<Object> supportedTypes = new ArrayList<Object>();

    public CDOObjectContainerAdapterFactory()
    {
      supportedTypes.add(IStructuredItemContentProvider.class);
    }

    public ComposeableAdapterFactory getRootAdapterFactory()
    {
      return parentAdapterFactory;
    }

    @Override
    public boolean isFactoryForType(Object type)
    {
      return type == viewsPackage || type instanceof CDOObjectContainer || supportedTypes.contains(type);
    }

    /**
     * This implementation substitutes the factory itself as the key for the adapter.
     */
    @Override
    public Adapter adapt(Notifier notifier, Object type)
    {
      if (notifier instanceof CDOObjectContainer && IStructuredItemContentProvider.class.equals(type))
      {
        return new CDOObjectContainerContentProvider();
      }

      return null;
    }

    public void setParentAdapterFactory(ComposedAdapterFactory parentAdapterFactory)
    {
      this.parentAdapterFactory = parentAdapterFactory;
    }

    public void addListener(INotifyChangedListener notifyChangedListener)
    {
      changeNotifier.addListener(notifyChangedListener);
    }

    public void removeListener(INotifyChangedListener notifyChangedListener)
    {
      changeNotifier.removeListener(notifyChangedListener);
    }

    public void fireNotifyChanged(Notification notification)
    {
      changeNotifier.fireNotifyChanged(notification);
      if (parentAdapterFactory != null)
      {
        parentAdapterFactory.fireNotifyChanged(notification);
      }
    }

    public void dispose()
    {
    }
  }

  /**
   * @author Victor Roldan Betancort
   */
  private static final class WatchListLabelProvider extends AdapterFactoryLabelProvider implements IColorProvider
  {
    private static final Color YELLOW = UIUtil.getDisplay().getSystemColor(SWT.COLOR_YELLOW);

    private static final String[] eventTypes = { "CREATE", "SET", "UNSET", "ADD", "REMOVE", "ADD MANY", "REMOVE MANY",
        "MOVE", "REMOVING ADAPTER", "RESOLVE" };

    private HashMap<CDOObject, Notification> notificationMap = new HashMap<CDOObject, Notification>();

    public WatchListLabelProvider(AdapterFactory adapterFactory)
    {
      super(adapterFactory);
    }

    @Override
    public Image getColumnImage(Object object, int columnIndex)
    {
      switch (columnIndex)
      {
      case 0:
        return super.getColumnImage(object, columnIndex);

      default:
        return null;
      }
    }

    @Override
    public String getColumnText(Object element, int columnIndex)
    {
      CDOObject cdoobject = (CDOObject)element;
      switch (columnIndex)
      {
      case 0:
        return super.getText(cdoobject);

      case 1:
        return cdoobject.cdoResource().getURI().toString();

      case 2:
        return cdoobject.cdoState().toString();

      case 3:
        Notification notification = notificationMap.get(element);
        return createEventLabel(notification);

      default:
        return element.toString();
      }
    }

    public void addNotification(Notification msg)
    {
      if (msg.getNotifier() instanceof CDOObject)
      {
        notificationMap.put((CDOObject)msg.getNotifier(), msg);
      }
    }

    public void removeNotification(CDOObject cdoobject)
    {
      notificationMap.remove(cdoobject);
    }

    @Override
    public Color getBackground(Object element)
    {
      if (element instanceof CDOObject)
      {
        if (notificationMap.containsKey(element))
        {
          return YELLOW;
        }
      }

      return super.getBackground(element);
    }

    private String createEventLabel(Notification notification)
    {
      if (notification == null)
      {
        return null;
      }

      StringBuffer eventString = new StringBuffer();
      int event = notification.getEventType();
      if (event < Notification.EVENT_TYPE_COUNT)
      {
        eventString.append(eventTypes[event]);
      }

      eventString.append(": ");
      eventString.append(((EStructuralFeature)notification.getFeature()).getName());
      eventString.append(" = ");
      eventString.append(notification.getNewValue().toString());
      return eventString.toString();
    }
  }

  /**
   * @author Victor Roldan Betancort
   */
  private static final class CDOObjectContainerContentProvider extends AdapterImpl implements
      IStructuredItemContentProvider
  {
    public CDOObjectContainerContentProvider()
    {
    }

    public Collection<?> getElements(Object object)
    {
      Collection<Object> cdoObjects = new ArrayList<Object>();
      if (object instanceof IContainer<?>)
      {
        for (Object cdoObject : ((IContainer<?>)object).getElements())
        {
          cdoObjects.add(cdoObject);
        }
      }

      return cdoObjects;
    }
  }

  private final class ResetNotificationAction extends LongRunningAction
  {
    private transient List<?> targets;

    public ResetNotificationAction()
    {
    }

    @Override
    protected void preRun() throws Exception
    {
      ISelection selection = getSelection();
      if (selection instanceof IStructuredSelection)
      {
        IStructuredSelection ssel = (IStructuredSelection)selection;
        if (!ssel.isEmpty())
        {
          targets = ssel.toList();
          return;
        }
      }

      cancel();
    }

    @Override
    protected void doRun(IProgressMonitor progressMonitor) throws Exception
    {
      if (targets != null)
      {
        List<?> useTargets = targets;
        targets = null;

        for (Object object : useTargets)
        {
          if (object instanceof CDOObject)
          {
            labelProvider.removeNotification((CDOObject)object);
            refreshViewer();
          }
        }
      }
    }
  }
}
