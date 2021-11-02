/*
 * Copyright (c) 2009-2012, 2015, 2016, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.views;

import org.eclipse.emf.cdo.CDODeltaNotification;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.internal.ui.actions.RemoveAllContainerItemAction;
import org.eclipse.emf.cdo.internal.ui.actions.RemoveContainerItemAction;
import org.eclipse.emf.cdo.internal.ui.dnd.CDOObjectDropAdapter;
import org.eclipse.emf.cdo.internal.ui.editor.CDOEditor;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.transaction.CDOCommitContext;
import org.eclipse.emf.cdo.transaction.CDODefaultTransactionHandler2;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.container.ContainerEvent;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.INotifier;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.actions.LongRunningAction;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;
import org.eclipse.emf.spi.cdo.FSMUtil;

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
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Victor Roldan Betancort
 */
public class CDOWatchListView extends ViewPart implements ISelectionProvider
{
  private static final String[] columnNames = { Messages.getString("CDOWatchListView.0"), //$NON-NLS-1$
      Messages.getString("CDOWatchListView.1"), Messages.getString("CDOWatchListView.2"), //$NON-NLS-1$ //$NON-NLS-2$
      Messages.getString("CDOWatchListView.3") }; //$NON-NLS-1$

  private static final int[] columnWidths = { 110, 280, 170, 230 };

  public static final String ID = "org.eclipse.emf.cdo.ui.CDOWatchListView"; //$NON-NLS-1$

  private final ComposedAdapterFactory adapterFactory;

  private final CDOObjectContainer container = new CDOObjectContainer();

  private final WatchedObjectsDataRegistry dataRegistry = new WatchedObjectsDataRegistry();

  private TableViewer viewer;

  private IPropertySheetPage propertySheetPage;

  private IAction removeAction;

  private IAction removeAllAction;

  private IAction resetNotificationAction;

  private IAction resetAllNotificationAction;

  public CDOWatchListView()
  {
    adapterFactory = CDOEditor.createAdapterFactory(true);
    container.addListener(getDataRegistry());
  }

  @Override
  public void dispose()
  {
    adapterFactory.dispose();
    super.dispose();
  }

  public WatchedObjectsDataRegistry getDataRegistry()
  {
    return dataRegistry;
  }

  @Override
  public void createPartControl(Composite parent)
  {
    // Create Viewer
    viewer = createViewer(parent);

    // This listener always refreshes viewer upon notification
    IListener refreshListener = new IListener()
    {
      @Override
      public void notifyEvent(IEvent event)
      {
        UIUtil.refreshViewer(viewer);
      }
    };

    container.addListener(refreshListener);
    getDataRegistry().addListener(refreshListener);

    // Configure ViewPart
    createActions();
    createToolbarButtons();
    createContextMenu();
    hookKeyboardActions();
    createViewPulldownMenu();
    getSite().setSelectionProvider(this);
  }

  private void createActions()
  {
    ISharedImages platformImages = PlatformUI.getWorkbench().getSharedImages();
    removeAction = new RemoveContainerItemAction<>(container, viewer);
    removeAction.setText(Messages.getString("CDOWatchListView.4")); //$NON-NLS-1$
    removeAction.setImageDescriptor(platformImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
    removeAction.setDisabledImageDescriptor(platformImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE_DISABLED));
    removeAction.setToolTipText(Messages.getString("CDOWatchListView.5")); //$NON-NLS-1$

    removeAllAction = new RemoveAllContainerItemAction<>(container);
    removeAllAction.setText(Messages.getString("CDOWatchListView.6")); //$NON-NLS-1$
    removeAllAction.setImageDescriptor(platformImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
    removeAllAction.setToolTipText(Messages.getString("CDOWatchListView.7")); //$NON-NLS-1$

    resetNotificationAction = new ResetNotificationAction();
    resetNotificationAction.setText(Messages.getString("CDOWatchListView.8")); //$NON-NLS-1$
    resetNotificationAction.setImageDescriptor(platformImages.getImageDescriptor(ISharedImages.IMG_TOOL_UNDO));
    resetNotificationAction.setDisabledImageDescriptor(platformImages.getImageDescriptor(ISharedImages.IMG_TOOL_UNDO_DISABLED));
    resetNotificationAction.setToolTipText(Messages.getString("CDOWatchListView.9")); //$NON-NLS-1$

    resetAllNotificationAction = new ResetAllNotificationAction();
    resetAllNotificationAction.setText(Messages.getString("CDOWatchListView.10")); //$NON-NLS-1$
    resetAllNotificationAction.setImageDescriptor(platformImages.getImageDescriptor(ISharedImages.IMG_TOOL_UNDO));
    resetAllNotificationAction.setDisabledImageDescriptor(platformImages.getImageDescriptor(ISharedImages.IMG_TOOL_UNDO_DISABLED));
    resetAllNotificationAction.setToolTipText(Messages.getString("CDOWatchListView.11")); //$NON-NLS-1$
  }

  private void createToolbarButtons()
  {
    getViewSite().getActionBars().getToolBarManager().add(resetAllNotificationAction);
  }

  private void createViewPulldownMenu()
  {
    IMenuManager menu = getViewSite().getActionBars().getMenuManager();
    menu.add(removeAllAction);
  }

  private void createContextMenu()
  {
    MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(new IMenuListener()
    {
      @Override
      public void menuAboutToShow(IMenuManager m)
      {
        CDOWatchListView.this.fillContextMenu(m);
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
    viewer.getControl().setFocus();
  }

  @Override
  public ISelection getSelection()
  {
    if (viewer != null)
    {
      return viewer.getSelection();
    }

    return StructuredSelection.EMPTY;
  }

  @Override
  public void setSelection(ISelection selection)
  {
    // Doesn't need to set viewer.setSelection(). Already done with user event.
  }

  @Override
  public void addSelectionChangedListener(ISelectionChangedListener listener)
  {
    if (viewer != null)
    {
      viewer.addSelectionChangedListener(listener);
    }
  }

  @Override
  public void removeSelectionChangedListener(ISelectionChangedListener listener)
  {
    if (viewer != null)
    {
      viewer.removeSelectionChangedListener(listener);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getAdapter(Class<T> adapter)
  {
    if (adapter.equals(IPropertySheetPage.class))
    {
      return (T)getPropertySheetPage();
    }

    return super.getAdapter(adapter);
  }

  public IPropertySheetPage getPropertySheetPage()
  {
    if (propertySheetPage == null)
    {
      PropertySheetPage page = new PropertySheetPage();
      page.setPropertySourceProvider(new AdapterFactoryContentProvider(adapterFactory));
      propertySheetPage = page;
    }

    return propertySheetPage;
  }

  private TableViewer createViewer(Composite parent)
  {
    TableViewer viewer = new TableViewer(parent, SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
    viewer.getTable().setLayoutData(UIUtil.createGridData());
    viewer.getTable().setHeaderVisible(true);
    viewer.getTable().setLinesVisible(true);
    createColumns(viewer.getTable());

    viewer.setContentProvider(new CDOObjectContainerContentProvider());
    viewer.setLabelProvider(new CDOSubscriptionViewLabelProvider(adapterFactory));
    viewer.setInput(container);

    CDOObjectDropAdapter.support(viewer);
    return viewer;
  }

  private void createColumns(Table table)
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
   * @author Victor Roldan Betancort
   */
  private final class TransactionHandler extends CDODefaultTransactionHandler2
  {
    public TransactionHandler()
    {
    }

    @Override
    public void committedTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
    {
      container.removeAllElements(commitContext.getDetachedObjects().values());
      UIUtil.refreshViewer(viewer);
    }
  }

  /**
   * @author Victor Roldan Betancort
   */
  private final class CDOSubscriptionAdapter extends AdapterImpl implements CDOAdapterPolicy
  {
    public CDOSubscriptionAdapter()
    {
    }

    @Override
    public void notifyChanged(Notification msg)
    {
      if (msg instanceof CDODeltaNotification)
      {
        checkDetached(msg);
        // TODO how to retrieve remote commit timestamp?
        getDataRegistry().addNotification(msg);
      }
    }

    private void checkDetached(Notification msg)
    {
      // TODO Remote detach won't be shown in the UI, the object will be just removed from the viewer
      if (((CDODeltaNotification)msg).getEventType() == CDODeltaNotification.DETACH_OBJECT)
      {
        Object obj = msg.getNotifier();
        container.removeElement(CDOUtil.getCDOObject((EObject)obj));
        getDataRegistry().removeData(obj);
      }
    }

    @Override
    public boolean isValid(EObject object, Adapter adapter)
    {
      return adapter instanceof CDOSubscriptionAdapter;
    }
  }

  /**
   * @author Victor Roldan Betancort
   */
  private final class CDOObjectContainer extends Container<CDOObject> implements IContainer.Modifiable<CDOObject>
  {
    private Set<CDOObject> watchedObjects = new HashSet<>();

    private CDOSubscriptionAdapter subscriptionAdapter = new CDOSubscriptionAdapter();

    private ViewDeactivationListener viewDeactivationListener = new ViewDeactivationListener();

    private TransactionHandler transactionHandler = new TransactionHandler();

    private CDOViewReferenceCounterManager counterManager = new CDOViewReferenceCounterManager();

    public CDOObjectContainer()
    {
    }

    @Override
    public CDOObject[] getElements()
    {
      return watchedObjects.toArray(new CDOObject[watchedObjects.size()]);
    }

    @Override
    public boolean isEmpty()
    {
      return watchedObjects.isEmpty();
    }

    /**
     * @returns true if the element was added successfully to the container
     */
    @Override
    public boolean addElement(CDOObject element)
    {
      if (watchedObjects.add(element))
      {
        postAdd(element);
        fireElementAddedEvent(element);
        return true;
      }

      return false;
    }

    /**
     * @returns true if the element was removed successfully from the container
     */
    @Override
    public boolean removeElement(CDOObject element)
    {
      if (watchedObjects.remove(element))
      {
        postRemove(element);
        fireElementRemovedEvent(element);
        return true;
      }

      return false;
    }

    /**
     * @returns true if at least one element was added. False otherwise.
     */
    @Override
    public boolean addAllElements(Collection<CDOObject> elements)
    {
      ArrayList<CDOObject> addedElements = new ArrayList<>();
      for (CDOObject cdoObject : elements)
      {
        if (watchedObjects.add(cdoObject))
        {
          postAdd(cdoObject);
          addedElements.add(cdoObject);
        }
      }

      if (!addedElements.isEmpty())
      {
        fireElementsAddedEvent(addedElements.toArray(new CDOObject[addedElements.size()]));
        return true;
      }

      return false;
    }

    /**
     * @returns true if at least one element was removed. False otherwise.
     */
    @Override
    public boolean removeAllElements(Collection<CDOObject> elements)
    {
      ArrayList<CDOObject> removedElements = new ArrayList<>();
      for (CDOObject cdoObject : elements)
      {
        if (watchedObjects.remove(cdoObject))
        {
          postRemove(cdoObject);
          removedElements.add(cdoObject);
        }
      }

      if (!removedElements.isEmpty())
      {
        fireElementsRemovedEvent(removedElements.toArray(new CDOObject[removedElements.size()]));
        return true;
      }

      return false;
    }

    private void postAdd(CDOObject element)
    {
      element.cdoView().options().addChangeSubscriptionPolicy(subscriptionAdapter);
      element.eAdapters().add(subscriptionAdapter);
      increaseViewReference(element);
    }

    private void postRemove(CDOObject element)
    {
      decreaseViewReference(element);
      CDOView view = element.cdoView();
      if (view != null && !view.isClosed())
      {
        element.eAdapters().remove(subscriptionAdapter);
      }
    }

    private void increaseViewReference(CDOObject referrer)
    {
      CDOViewReferenceCounterManager.CDOViewReferenceCounter counter = counterManager.getCounter(referrer);
      if (counter.increase() == 1)
      {
        CDOView view = referrer.cdoView();
        view.addListener(viewDeactivationListener);
        if (view instanceof CDOTransaction)
        {
          ((CDOTransaction)view).addTransactionHandler(transactionHandler);
        }
      }
    }

    private void decreaseViewReference(CDOObject referrer)
    {
      CDOViewReferenceCounterManager.CDOViewReferenceCounter counter = counterManager.getCounter(referrer);

      // CDOObject might be detached, and so CDOView will be null
      if (counter.decrease() == 0 && referrer.cdoView() != null)
      {
        CDOView view = referrer.cdoView();
        view.removeListener(viewDeactivationListener);
        if (view instanceof CDOTransaction)
        {
          ((CDOTransaction)view).removeTransactionHandler(transactionHandler);
        }
      }
    }

    /**
     * @author Victor Roldan Betancort
     */
    private final class ViewDeactivationListener implements IListener
    {
      @Override
      public void notifyEvent(IEvent event)
      {
        if (event instanceof ILifecycleEvent)
        {
          if (((ILifecycleEvent)event).getKind() == ILifecycleEvent.Kind.ABOUT_TO_DEACTIVATE)
          {
            List<CDOObject> aboutToRemove = new ArrayList<>();
            for (CDOObject object : getElements())
            {
              if (object.cdoView().equals(event.getSource()))
              {
                aboutToRemove.add(object);
              }
            }

            removeAllElements(aboutToRemove);
          }
        }
      }
    }
  }

  /**
   * @author Victor Roldan Betancort
   */
  private final class CDOViewReferenceCounterManager
  {
    private List<CDOViewReferenceCounter> viewReferences = new ArrayList<>();

    public CDOViewReferenceCounterManager()
    {
    }

    private class CDOViewReferenceCounter
    {
      private final CDOView view;

      private AtomicLong referenceCount = new AtomicLong();

      public CDOViewReferenceCounter(CDOObject cdoObject)
      {
        view = cdoObject.cdoView();
        referenceCount.set(0);
      }

      public long increase()
      {
        return referenceCount.incrementAndGet();
      }

      public synchronized long decrease()
      {
        Long value = referenceCount.decrementAndGet();
        if (value == 0)
        {
          viewReferences.remove(this);
        }

        return value;
      }

      public CDOView getReferencedView()
      {
        return view;
      }
    }

    public CDOViewReferenceCounter getCounter(CDOObject cdoObject)
    {
      synchronized (viewReferences)
      {
        for (CDOViewReferenceCounter counter : viewReferences)
        {
          if (counter.getReferencedView().equals(cdoObject.cdoView()))
          {
            return counter;
          }
        }
      }

      CDOViewReferenceCounter counter = new CDOViewReferenceCounter(cdoObject);
      viewReferences.add(counter);
      return counter;
    }
  }

  /**
   * @author Victor Roldan Betancort
   */
  private final class WatchedObjectsDataRegistry extends org.eclipse.net4j.util.event.Notifier implements IListener
  {
    private Map<Object, WatchedObjectData> registry = new HashMap<>();

    private final IEvent dataRegistryEvent = new IEvent()
    {
      @Override
      public INotifier getSource()
      {
        return WatchedObjectsDataRegistry.this;
      }
    };

    public WatchedObjectsDataRegistry()
    {
    }

    public WatchedObjectData getData(Object object)
    {
      WatchedObjectData data = registry.get(object);
      if (data == null)
      {
        data = new WatchedObjectData();
        registry.put(object, data);
      }

      return data;
    }

    public void addNotification(Notification msg)
    {
      getData(msg.getNotifier()).setNotification(msg);
      IListener[] listeners = getListeners();
      if (listeners.length != 0)
      {
        fireEvent(dataRegistryEvent, listeners);
      }
    }

    public void removeNotification(Object object)
    {
      getData(object).deleteNotification();
      IListener[] listeners = getListeners();
      if (listeners.length != 0)
      {
        fireEvent(dataRegistryEvent, listeners);
      }
    }

    public void removeData(Object object)
    {
      registry.remove(object);
    }

    public void removeAllNotification(Collection<Object> objects)
    {
      for (WatchedObjectData data : registry.values())
      {
        data.deleteNotification();
      }

      IListener[] listeners = getListeners();
      if (listeners.length != 0)
      {
        fireEvent(dataRegistryEvent, listeners);
      }
    }

    public Notification getNotification(Object object)
    {
      return getData(object).getNotification();
    }

    @Override
    public void notifyEvent(IEvent event)
    {
      if (event instanceof ContainerEvent<?>)
      {
        ContainerEvent<?> containerEvent = (ContainerEvent<?>)event;
        for (IContainerDelta<?> delta : containerEvent.getDeltas())
        {
          if (delta.getKind().equals(IContainerDelta.Kind.REMOVED))
          {
            removeData(delta.getElement());
          }
        }
      }
    }

    /**
     * @author Victor Roldan Betancort
     */
    private final class WatchedObjectData
    {
      private Notification notification;

      public WatchedObjectData()
      {
      }

      public void setNotification(Notification notification)
      {
        this.notification = notification;
      }

      public void deleteNotification()
      {
        notification = null;
      }

      public Notification getNotification()
      {
        return notification;
      }
    }
  }

  /**
   * @author Victor Roldan Betancort
   */
  private final class CDOSubscriptionViewLabelProvider extends LabelProvider implements ITableLabelProvider, IColorProvider
  {
    private final Color YELLOW = UIUtil.getDisplay().getSystemColor(SWT.COLOR_YELLOW);

    private final String[] eventTypes = { Messages.getString("CDOWatchListView.12"), //$NON-NLS-1$
        Messages.getString("CDOWatchListView.25"), Messages.getString("CDOWatchListView.26"), //$NON-NLS-1$ //$NON-NLS-2$
        Messages.getString("CDOWatchListView.27"), Messages.getString("CDOWatchListView.28"), //$NON-NLS-1$ //$NON-NLS-2$
        Messages.getString("CDOWatchListView.29"), Messages.getString("CDOWatchListView.30"), //$NON-NLS-1$ //$NON-NLS-2$
        Messages.getString("CDOWatchListView.31"), //$NON-NLS-1$
        Messages.getString("CDOWatchListView.32"), Messages.getString("CDOWatchListView.33") }; //$NON-NLS-1$ //$NON-NLS-2$

    private AdapterFactory adapterFactory;

    public CDOSubscriptionViewLabelProvider(AdapterFactory adapterFactory)
    {
      this.adapterFactory = adapterFactory;
    }

    @Override
    public Image getColumnImage(Object object, int columnIndex)
    {
      // In case an invalid object arrives (i.e., detached), return CDOState
      if (FSMUtil.isInvalid(CDOUtil.getCDOObject((EObject)object)))
      {
        return null;
      }

      switch (columnIndex)
      {
      case 0:
        IItemLabelProvider labelProvider = (IItemLabelProvider)adapterFactory.adapt(object, IItemLabelProvider.class);
        if (labelProvider != null)
        {
          return ExtendedImageRegistry.getInstance().getImage(labelProvider.getImage(object));
        }
      }

      return null;
    }

    @Override
    public String getColumnText(Object element, int columnIndex)
    {
      CDOObject object = CDOUtil.getCDOObject((EObject)element);

      // In case an invalid object arrives (i.e., detached), return CDOState
      if (FSMUtil.isInvalid(object))
      {
        return object.cdoState().toString();
      }

      switch (columnIndex)
      {
      case 0:
        IItemLabelProvider labelProvider = (IItemLabelProvider)adapterFactory.adapt(object, IItemLabelProvider.class);
        if (labelProvider != null)
        {
          return labelProvider.getText(object);
        }

        return null;

      case 1:
        return object.cdoResource().getURI().toString();

      case 2:
        // XXX CDOView view = object.cdoView();
        // if (view instanceof CDOTransaction)
        // {
        // long time = ((CDOTransaction)view).getLastCommitTime();
        // if (time != CDORevision.UNSPECIFIED_DATE)
        // {
        // return new Date(time).toString();
        // }
        // }

        return "?"; //$NON-NLS-1$

      case 3:
        Notification notification = getDataRegistry().getNotification(element);
        return createEventLabel(notification);
      }

      return element.toString();
    }

    @Override
    public Color getBackground(Object element)
    {
      if (element instanceof CDOObject)
      {
        if (getDataRegistry().getNotification(element) != null)
        {
          return YELLOW;
        }
      }

      return null;
    }

    @Override
    public Color getForeground(Object element)
    {
      return null;
    }

    private String createEventLabel(Notification notification)
    {
      if (notification == null)
      {
        return null;
      }

      StringBuilder builder = new StringBuilder();
      int event = notification.getEventType();
      if (event < Notification.EVENT_TYPE_COUNT)
      {
        builder.append(eventTypes[event]);
      }

      builder.append(": "); //$NON-NLS-1$
      builder.append(((EStructuralFeature)notification.getFeature()).getName());
      builder.append(" = "); //$NON-NLS-1$
      builder.append(notification.getNewValue().toString());
      return builder.toString();
    }
  }

  /**
   * @author Victor Roldan Betancort
   */
  private final class CDOObjectContainerContentProvider implements IStructuredContentProvider
  {
    public CDOObjectContainerContentProvider()
    {
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object[] getElements(Object object)
    {
      return ((IContainer<Object>)object).getElements();
    }

    @Override
    public void dispose()
    {
      // do nothing
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
      // do nothing
    }
  }

  /**
   * @author Victor Roldan Betancort
   */
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
            getDataRegistry().removeNotification(object);
          }
        }
      }
    }
  }

  /**
   * @author Victor Roldan Betancort
   */
  private final class ResetAllNotificationAction extends LongRunningAction
  {
    public ResetAllNotificationAction()
    {
    }

    @Override
    protected void doRun(IProgressMonitor progressMonitor) throws Exception
    {
      ArrayList<Object> aboutToReset = new ArrayList<>();
      for (CDOObject cdoObject : container.getElements())
      {
        aboutToReset.add(cdoObject);
      }

      getDataRegistry().removeAllNotification(aboutToReset);
    }
  }
}
