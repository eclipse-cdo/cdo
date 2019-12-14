/*
 * Copyright (c) 2015, 2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.checkouts;

import org.eclipse.emf.cdo.CDOElement;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.explorer.CDOExplorerManager;
import org.eclipse.emf.cdo.explorer.CDOExplorerManager.ElementsChangedEvent;
import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckoutManager;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckoutManager.CheckoutStateEvent;
import org.eclipse.emf.cdo.explorer.ui.ViewerUtil;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.explorer.ui.checkouts.actions.OpenWithActionProvider;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionCache;
import org.eclipse.emf.cdo.ui.CDOItemProvider;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.LifecycleException;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.views.ItemProvider;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.ICommonContentProvider;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Eike Stepper
 */
public class CDOCheckoutContentProvider implements ICommonContentProvider, IPropertySourceProvider, IOpenListener
{
  private static final Map<String, CDOCheckoutContentProvider> INSTANCES = new HashMap<>();

  private static final Set<Object> LOADING_OBJECTS = new HashSet<>();

  private static final Method GET_CHILDREN_FEATURES_METHOD = getMethod(ItemProviderAdapter.class, "getChildrenFeatures", Object.class);

  private static final Method FIND_ITEM_METHOD = getMethod(StructuredViewer.class, "findItem", Object.class);

  private static final CDOCheckoutManager CHECKOUT_MANAGER = CDOExplorerUtil.getCheckoutManager();

  private final IListener checkoutManagerListener = new IListener()
  {
    @Override
    public void notifyEvent(IEvent event)
    {
      CDOCheckoutViewerRefresh viewerRefresh = stateManager.getViewerRefresh();

      if (event instanceof IContainerEvent)
      {
        viewerRefresh.addNotification(null, true, true);
      }
      else if (event instanceof CheckoutStateEvent)
      {
        CheckoutStateEvent e = (CheckoutStateEvent)event;
        CDOCheckout checkout = e.getCheckout();
        CDOCheckout.State state = e.getNewState();

        if (state == CDOCheckout.State.Opening)
        {
          // Trigger hasChildren().
          ViewerUtil.refresh(viewer, checkout);

          // Trigger getChildren().
          ViewerUtil.expand(viewer, checkout, true);
        }
        else
        {
          if (state == CDOCheckout.State.Closed)
          {
            ViewerUtil.expand(viewer, checkout, false);
          }

          viewerRefresh.addNotification(checkout, true, true);

          if (state == CDOCheckout.State.Open)
          {
            ViewerUtil.expand(viewer, checkout, true);
          }

          updatePropertySheetPage(checkout);
        }
      }
      else if (event instanceof CDOExplorerManager.ElementsChangedEvent)
      {
        CDOExplorerManager.ElementsChangedEvent e = (CDOExplorerManager.ElementsChangedEvent)event;
        ElementsChangedEvent.StructuralImpact structuralImpact = e.getStructuralImpact();
        Object[] changedElements = e.getChangedElements();

        if (structuralImpact != ElementsChangedEvent.StructuralImpact.NONE && changedElements.length == 1)
        {
          Object changedElement = changedElements[0];
          if (changedElement instanceof CDOElement)
          {
            changedElement = ((CDOElement)changedElement).getParent();
          }

          if (structuralImpact == ElementsChangedEvent.StructuralImpact.PARENT)
          {
            changedElement = CDOExplorerUtil.getParent(changedElement);
          }

          viewerRefresh.addNotification(changedElement, true, true);
        }
        else
        {
          for (Object changedElement : changedElements)
          {
            viewerRefresh.addNotification(changedElement, false, true);
          }
        }

        updatePropertySheetPage(changedElements);
      }
    }

    private void updatePropertySheetPage(final Object element)
    {
      IWorkbenchPage workbenchPage = getWorkbenchPage();

      PropertySheet propertySheet = getPropertySheet(workbenchPage);
      if (propertySheet != null)
      {
        final IPage currentPage = propertySheet.getCurrentPage();
        if (currentPage instanceof PropertySheetPage || currentPage instanceof TabbedPropertySheetPage)
        {
          final Control control = viewer.getControl();
          if (!control.isDisposed())
          {
            control.getDisplay().asyncExec(new Runnable()
            {
              @Override
              public void run()
              {
                if (!control.isDisposed())
                {
                  IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
                  if (selection.size() == 1)
                  {
                    for (Object object : selection.toArray())
                    {
                      if (object == element)
                      {
                        if (currentPage instanceof PropertySheetPage)
                        {
                          ((PropertySheetPage)currentPage).refresh();
                        }
                        else if (currentPage instanceof TabbedPropertySheetPage)
                        {
                          TabbedPropertySheetPage page = (TabbedPropertySheetPage)currentPage;
                          if (page.getCurrentTab() != null)
                          {
                            page.refresh();
                          }
                        }

                        return;
                      }
                    }
                  }
                }
              }
            });
          }
        }
      }
    }

    private PropertySheet getPropertySheet(IWorkbenchPage workbenchPage)
    {
      for (IViewReference viewReference : workbenchPage.getViewReferences())
      {
        if ("org.eclipse.ui.views.PropertySheet".equals(viewReference.getId()))
        {
          IViewPart view = viewReference.getView(false);
          if (view instanceof PropertySheet)
          {
            return (PropertySheet)view;

          }
        }
      }

      return null;
    }
  };

  private final CDOCheckoutStateManager stateManager = new CDOCheckoutStateManager(this);

  private final Map<Object, Object[]> childrenCache = new ConcurrentHashMap<>();

  private String viewerID;

  private TreeViewer viewer;

  private Object input;

  public static final String PROJECT_EXPLORER_ID = "org.eclipse.ui.navigator.ProjectExplorer";

  public CDOCheckoutContentProvider()
  {
  }

  @Override
  public void init(ICommonContentExtensionSite config)
  {
    viewerID = config.getService().getViewerId();
    INSTANCES.put(viewerID, this);
    CHECKOUT_MANAGER.addListener(checkoutManagerListener);
  }

  @Override
  public void saveState(IMemento aMemento)
  {
    // Do nothing.
  }

  @Override
  public void restoreState(IMemento aMemento)
  {
    // Do nothing.
  }

  @Override
  public void dispose()
  {
    CHECKOUT_MANAGER.removeListener(checkoutManagerListener);
    INSTANCES.remove(viewerID);
  }

  public void disposeWith(Control control)
  {
    control.addDisposeListener(new DisposeListener()
    {
      @Override
      public void widgetDisposed(DisposeEvent e)
      {
        dispose();
      }
    });
  }

  public final CDOCheckoutStateManager getStateManager()
  {
    return stateManager;
  }

  public final TreeViewer getViewer()
  {
    return viewer;
  }

  @Override
  public void inputChanged(Viewer newViewer, Object oldInput, Object newInput)
  {
    TreeViewer newTreeViewer = null;
    if (newViewer instanceof TreeViewer)
    {
      newTreeViewer = (TreeViewer)newViewer;
    }

    if (newTreeViewer != viewer)
    {
      if (viewer != null)
      {
        viewer.removeOpenListener(this);
      }

      viewer = newTreeViewer;

      if (viewer != null)
      {
        viewer.addOpenListener(this);
      }
    }

    input = newInput;
    stateManager.inputChanged(newTreeViewer, oldInput, newInput);
  }

  public Object getInput()
  {
    return input;
  }

  @Override
  public Object[] getElements(Object object)
  {
    return getChildren(object);
  }

  @Override
  public boolean hasChildren(Object object)
  {
    try
    {
      if (object instanceof IResource)
      {
        if (object instanceof IWorkspaceRoot)
        {
          return !CHECKOUT_MANAGER.isEmpty();
        }

        return false;
      }

      if (object instanceof ViewerUtil.Pending)
      {
        return false;
      }

      if (object instanceof CDOCheckout)
      {
        CDOCheckout checkout = (CDOCheckout)object;
        switch (checkout.getState())
        {
        case Closing:
        case Closed:
          return false;

        case Opening:
          // This must be the ViewerUtil.Pending element.
          return true;

        case Open:
          object = checkout.getRootObject();
          break;
        }
      }

      if (object instanceof CDOElement)
      {
        CDOElement checkoutElement = (CDOElement)object;
        return checkoutElement.hasChildren();
      }

      if (GET_CHILDREN_FEATURES_METHOD != null && object instanceof EObject)
      {
        EObject eObject = (EObject)object;

        InternalCDOObject cdoObject = getCDOObject(eObject);
        if (cdoObject != null)
        {
          InternalCDORevision revision = cdoObject.cdoRevision(false);
          if (revision != null)
          {
            try
            {
              ITreeItemContentProvider provider = (ITreeItemContentProvider)stateManager.adapt(object, ITreeItemContentProvider.class);
              if (provider instanceof ItemProviderAdapter)
              {
                return hasChildren(cdoObject, revision, (ItemProviderAdapter)provider);
              }
            }
            catch (Exception ex)
            {
              //$FALL-THROUGH$
            }
          }
        }
      }

      ITreeContentProvider contentProvider = stateManager.getContentProvider(object);
      if (contentProvider != null)
      {
        return contentProvider.hasChildren(object);
      }
    }
    catch (LifecycleException ex)
    {
      //$FALL-THROUGH$
    }
    catch (RuntimeException ex)
    {
      if (LifecycleUtil.isActive(object))
      {
        throw ex;
      }

      //$FALL-THROUGH$
    }

    return false;
  }

  @Override
  public Object[] getChildren(Object object)
  {
    try
    {
      if (object instanceof IResource)
      {
        if (object instanceof IWorkspaceRoot)
        {
          return CHECKOUT_MANAGER.getCheckouts();
        }

        return ViewerUtil.NO_CHILDREN;
      }

      if (object instanceof ViewerUtil.Pending)
      {
        return ViewerUtil.NO_CHILDREN;
      }

      if (object instanceof CDOElement)
      {
        CDOElement checkoutElement = (CDOElement)object;
        return checkoutElement.getChildren();
      }

      final Object originalObject = object;
      Object[] children = childrenCache.remove(originalObject);
      if (children != null)
      {
        return children;
      }

      CDOCheckout openingCheckout = null;
      CDOCheckout checkout = null;

      if (object instanceof CDOCheckout)
      {
        checkout = (CDOCheckout)object;

        switch (checkout.getState())
        {
        case Closing:
        case Closed:
          return ViewerUtil.NO_CHILDREN;

        case Opening:
          openingCheckout = checkout;
          break;

        case Open:
          object = checkout.getRootObject();
          break;
        }
      }

      final Object finalObject = object;
      final CDOCheckout finalOpeningCheckout = openingCheckout;

      final ITreeContentProvider contentProvider = stateManager.getContentProvider(finalObject);
      if (contentProvider == null)
      {
        return ItemProvider.NO_ELEMENTS;
      }

      final List<CDORevision> loadedRevisions = new ArrayList<>();
      final List<CDOID> missingIDs = new ArrayList<>();

      if (openingCheckout == null)
      {
        children = determineChildRevisions(object, loadedRevisions, missingIDs);
        if (children != null)
        {
          return CDOCheckoutContentModifier.Registry.INSTANCE.modifyChildren(object, children);
        }
      }

      boolean firstLoad;
      synchronized (LOADING_OBJECTS)
      {
        firstLoad = LOADING_OBJECTS.add(originalObject);
      }

      if (firstLoad || finalOpeningCheckout == null)
      {
        Job job = new Job("Load " + finalObject)
        {
          @Override
          protected IStatus run(IProgressMonitor monitor)
          {
            try
            {
              if (finalOpeningCheckout != null)
              {
                finalOpeningCheckout.open();
                determineChildRevisions(finalObject, loadedRevisions, missingIDs);
              }

              if (!missingIDs.isEmpty())
              {
                CDOObject cdoObject = getCDOObject((EObject)finalObject);
                CDOView view = cdoObject.cdoView();
                CDORevisionManager revisionManager = view.getSession().getRevisionManager();

                List<CDORevision> revisions = revisionManager.getRevisions(missingIDs, view, CDORevision.UNCHUNKED, CDORevision.DEPTH_NONE, true);
                loadedRevisions.addAll(revisions);
              }

              Object[] children = contentProvider.getChildren(finalObject);

              // Adjust possible legacy adapters.
              for (int i = 0; i < children.length; i++)
              {
                Object child = children[i];
                if (child instanceof InternalCDOObject)
                {
                  InternalCDOObject cdoObject = (InternalCDOObject)child;
                  InternalEObject instance = cdoObject.cdoInternalInstance();
                  if (instance != cdoObject)
                  {
                    children[i] = instance;
                  }
                }
              }

              children = CDOCheckoutContentModifier.Registry.INSTANCE.modifyChildren(finalObject, children);
              childrenCache.put(originalObject, children);
            }
            catch (final Exception ex)
            {
              childrenCache.remove(originalObject);

              if (finalOpeningCheckout != null)
              {
                finalOpeningCheckout.close();
              }

              OM.LOG.error(ex);

              final Control control = viewer.getControl();
              if (!control.isDisposed())
              {
                UIUtil.getDisplay().asyncExec(new Runnable()
                {
                  @Override
                  public void run()
                  {
                    try
                    {
                      if (!control.isDisposed())
                      {
                        Shell shell = control.getShell();
                        String title = (finalOpeningCheckout != null ? "Open" : "Load") + " Error";
                        MessageDialog.openError(shell, title, ex.getMessage());
                      }
                    }
                    catch (Exception ex)
                    {
                      OM.LOG.error(ex);
                    }
                  }
                });
              }
            }

            CDOCheckoutViewerRefresh viewerRefresh = stateManager.getViewerRefresh();
            viewerRefresh.addNotification(originalObject, true, true, new Runnable()
            {
              @Override
              public void run()
              {
                synchronized (LOADING_OBJECTS)
                {
                  LOADING_OBJECTS.remove(originalObject);
                }
              }
            });

            return Status.OK_STATUS;
          }
        };

        job.schedule();
      }

      if (FIND_ITEM_METHOD != null)
      {
        try
        {
          Object widget = FIND_ITEM_METHOD.invoke(viewer, originalObject);
          if (widget instanceof TreeItem)
          {
            TreeItem item = (TreeItem)widget;
            TreeItem[] childItems = item.getItems();

            int childCount = childItems.length;
            if (childCount != 0)
            {
              List<Object> result = new ArrayList<>();
              for (int i = 0; i < childCount; i++)
              {
                TreeItem childItem = childItems[i];
                Object child = childItem.getData();
                if (child != null)
                {
                  result.add(child);
                }
              }

              int size = result.size();
              if (size != 0)
              {
                return result.toArray(new Object[size]);
              }
            }
          }
        }
        catch (Exception ex)
        {
          //$FALL-THROUGH$
        }
      }

      String text = "Loading...";
      if (finalOpeningCheckout != null)
      {
        text = "Opening...";
      }

      return new Object[] { new ViewerUtil.Pending(originalObject, text) };
    }
    catch (LifecycleException ex)
    {
      //$FALL-THROUGH$
    }
    catch (RuntimeException ex)
    {
      if (LifecycleUtil.isActive(object))
      {
        throw ex;
      }

      //$FALL-THROUGH$
    }

    return ItemProvider.NO_ELEMENTS;
  }

  private Object[] determineChildRevisions(Object object, List<CDORevision> loadedRevisions, List<CDOID> missingIDs)
  {
    if (GET_CHILDREN_FEATURES_METHOD != null && object instanceof EObject)
    {
      EObject eObject = (EObject)object;

      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (cdoObject != null)
      {
        InternalCDORevision revision = cdoObject.cdoRevision(false);
        if (revision != null)
        {
          try
          {
            ITreeItemContentProvider provider = (ITreeItemContentProvider)stateManager.adapt(object, ITreeItemContentProvider.class);
            if (provider instanceof ItemProviderAdapter)
            {
              determineChildRevisions(cdoObject, revision, (ItemProviderAdapter)provider, loadedRevisions, missingIDs);

              if (missingIDs.isEmpty())
              {
                // All revisions are cached. Just return the objects without server round-trips.
                ITreeContentProvider contentProvider = stateManager.getContentProvider(object);
                if (contentProvider != null)
                {
                  return contentProvider.getChildren(object);
                }
              }
            }
          }
          catch (Exception ex)
          {
            //$FALL-THROUGH$
          }
        }
      }
    }

    return null;
  }

  @Override
  public Object getParent(Object object)
  {
    try
    {
      if (object instanceof CDOCheckout)
      {
        return ResourcesPlugin.getWorkspace().getRoot();
      }

      if (object instanceof ViewerUtil.Pending)
      {
        return ((ViewerUtil.Pending)object).getParent();
      }

      if (object instanceof CDOElement)
      {
        CDOElement checkoutElement = (CDOElement)object;
        return checkoutElement.getParent();
      }

      if (object instanceof EObject)
      {
        EObject eObject = CDOUtil.getEObject((EObject)object);

        CDOElement element = CDOElement.getFor(eObject);
        if (element != null)
        {
          return element;
        }

        ITreeContentProvider contentProvider = stateManager.getContentProvider(object);
        if (contentProvider != null)
        {
          Object parent = contentProvider.getParent(object);
          if (parent instanceof EObject)
          {
            EObject eParent = (EObject)parent;
            Adapter adapter = EcoreUtil.getAdapter(eParent.eAdapters(), CDOCheckout.class);
            if (adapter != null)
            {
              return adapter;
            }
          }

          return parent;
        }
      }
    }
    catch (LifecycleException ex)
    {
      //$FALL-THROUGH$
    }
    catch (RuntimeException ex)
    {
      if (LifecycleUtil.isActive(object))
      {
        throw ex;
      }

      //$FALL-THROUGH$
    }

    return null;
  }

  @Override
  public IPropertySource getPropertySource(Object object)
  {
    IPropertySourceProvider contentProvider = stateManager.getContentProvider(object);
    if (contentProvider != null)
    {
      return contentProvider.getPropertySource(object);
    }

    return null;
  }

  public void selectObjects(final Object... objects)
  {
    final Control control = viewer.getControl();
    if (!control.isDisposed())
    {
      final long end = System.currentTimeMillis() + 5000L;
      final Display display = control.getDisplay();
      display.asyncExec(new Runnable()
      {
        @Override
        public void run()
        {
          if (control.isDisposed())
          {
            return;
          }

          for (Object object : objects)
          {
            LinkedList<Object> path = new LinkedList<>();
            CDOCheckout checkout = CDOExplorerUtil.walkUp(object, path);
            if (checkout != null)
            {
              viewer.setExpandedState(checkout, true);

              if (!path.isEmpty())
              {
                path.removeFirst();
              }

              if (!path.isEmpty())
              {
                path.removeLast();
              }

              for (Object parent : path)
              {
                viewer.setExpandedState(parent, true);
              }
            }
          }

          viewer.setSelection(new StructuredSelection(objects), true);
          IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();

          Set<Object> actual = new HashSet<>(Arrays.asList(selection.toArray()));
          Set<Object> expected = new HashSet<>(Arrays.asList(objects));

          if (!actual.equals(expected))
          {
            if (isObjectLoading(objects) || System.currentTimeMillis() < end)
            {
              display.timerExec(50, this);
            }
          }
        }
      });
    }
  }

  @Override
  public void open(OpenEvent event)
  {
    ISelection selection = event.getSelection();
    if (selection instanceof IStructuredSelection)
    {
      IStructuredSelection ssel = (IStructuredSelection)selection;
      if (ssel.size() == 1)
      {
        Object element = ssel.getFirstElement();
        if (element instanceof CDOCheckout)
        {
          final CDOCheckout checkout = (CDOCheckout)element;
          if (checkout.getState() == CDOCheckout.State.Closed)
          {
            // Simulate CheckoutOpenHandler.
            new Job("Open " + checkout.getLabel())
            {
              @Override
              protected IStatus run(IProgressMonitor monitor)
              {
                try
                {
                  checkout.open();
                }
                catch (Exception ex)
                {
                  OM.LOG.error(ex);
                }

                return Status.OK_STATUS;
              }
            }.schedule();
          }
        }
        else if (element instanceof CDOResourceNode)
        {
          // Do nothing special.
        }
        else if (element instanceof EObject)
        {
          EObject eObject = (EObject)element;

          Shell shell = getWorkbenchPage().getWorkbenchWindow().getShell();
          ComposedAdapterFactory adapterFactory = stateManager.getAdapterFactory(eObject);
          OpenWithActionProvider.editObject(shell, adapterFactory, eObject);
        }
      }
    }
  }

  private IWorkbenchPage getWorkbenchPage()
  {
    if (viewer instanceof CommonViewer)
    {
      CommonViewer commonViewer = (CommonViewer)viewer;
      return commonViewer.getCommonNavigator().getSite().getPage();
    }

    return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
  }

  private static InternalCDOObject getCDOObject(EObject eObject)
  {
    return (InternalCDOObject)CDOUtil.getCDOObject(eObject, false);
  }

  private static boolean hasChildren(InternalCDOObject cdoObject, InternalCDORevision revision, ItemProviderAdapter provider) throws Exception
  {
    for (EStructuralFeature feature : getChildrenFeatures(cdoObject, provider))
    {
      if (feature.isMany())
      {
        if (!revision.isEmpty(feature))
        {
          return true;
        }
      }
      else if (revision.getValue(feature) != null)
      {
        return true;
      }
    }

    return false;
  }

  private static void determineChildRevisions(InternalCDOObject cdoObject, InternalCDORevision revision, ItemProviderAdapter provider,
      List<CDORevision> loadedRevisions, List<CDOID> missingIDs) throws Exception
  {
    InternalCDOView view = cdoObject.cdoView();
    InternalCDORevisionCache revisionCache = view.getSession().getRevisionManager().getCache();

    for (EStructuralFeature feature : getChildrenFeatures(cdoObject, provider))
    {
      if (feature.isMany())
      {
        CDOList list = revision.getListOrNull(feature);
        if (list != null)
        {
          for (Object object : list)
          {
            determineChildRevision(loadedRevisions, missingIDs, view, revisionCache, object);
          }
        }
      }
      else
      {
        Object value = revision.getValue(feature);
        determineChildRevision(loadedRevisions, missingIDs, view, revisionCache, value);
      }
    }
  }

  private static void determineChildRevision(List<CDORevision> loadedRevisions, List<CDOID> missingIDs, InternalCDOView view, InternalCDORevisionCache cache,
      Object object)
  {
    if (object instanceof CDOID)
    {
      CDOID id = (CDOID)object;
      CDORevision childRevision = cache.getRevision(id, view);
      if (childRevision != null)
      {
        loadedRevisions.add(childRevision);
      }
      else
      {
        missingIDs.add(id);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static Collection<? extends EStructuralFeature> getChildrenFeatures(InternalCDOObject cdoObject, ItemProviderAdapter provider) throws Exception
  {
    return (Collection<? extends EStructuralFeature>)GET_CHILDREN_FEATURES_METHOD.invoke(provider, cdoObject);
  }

  private static Method getMethod(Class<?> c, String methodName, Class<?>... parameterTypes)
  {
    try
    {
      Method method = c.getDeclaredMethod(methodName, parameterTypes);
      method.setAccessible(true);
      return method;
    }
    catch (Throwable ex)
    {
      return null;
    }
  }

  private static boolean isObjectLoading(Object... objects)
  {
    synchronized (LOADING_OBJECTS)
    {
      for (Object object : objects)
      {
        if (LOADING_OBJECTS.contains(object))
        {
          return true;
        }
      }

      return false;
    }
  }

  public static TreeViewer createTreeViewer(Composite container)
  {
    // TODO This is not lazy, async:
    CDOItemProvider parentItemProvider = new CDOItemProvider(null)
    {
      @Override
      public boolean hasChildren(Object element)
      {
        return getChildren(element).length != 0;
      }

      @Override
      public Object[] getChildren(Object element)
      {
        List<Object> children = new ArrayList<>();
        for (Object child : doGetChildren(element))
        {
          if (child instanceof CDOCheckout || child instanceof CDOResourceFolder)
          {
            children.add(child);
          }
        }

        return children.toArray();
      }

      private Object[] doGetChildren(Object element)
      {
        if (element instanceof CDOCheckout)
        {
          CDOCheckout checkout = (CDOCheckout)element;
          if (checkout.isOpen())
          {
            return checkout.getRootObject().eContents().toArray();
          }
        }

        return super.getChildren(element);
      }

      @Override
      public void fillContextMenu(IMenuManager manager, ITreeSelection selection)
      {
        // Do nothing.
      }
    };

    CDOCheckoutContentProvider contentProvider = new CDOCheckoutContentProvider();
    contentProvider.disposeWith(container);

    CDOCheckoutLabelProvider labelProvider = new CDOCheckoutLabelProvider(contentProvider);

    TreeViewer parentViewer = new TreeViewer(container, SWT.BORDER);
    parentViewer.setContentProvider(parentItemProvider);
    parentViewer.setLabelProvider(labelProvider);
    parentViewer.setInput(CDOExplorerUtil.getCheckoutManager());
    return parentViewer;
  }

  public static final CDOCheckoutContentProvider getInstance(String viewerID)
  {
    return INSTANCES.get(viewerID);
  }
}
