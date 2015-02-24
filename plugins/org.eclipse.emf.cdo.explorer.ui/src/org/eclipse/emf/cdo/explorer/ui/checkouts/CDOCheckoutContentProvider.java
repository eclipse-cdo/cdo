/*
 * Copyright (c) 2009-2013 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout.State;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckoutManager;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckoutManager.CheckoutOpenEvent;
import org.eclipse.emf.cdo.explorer.ui.ViewerUtil;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.explorer.ui.checkouts.actions.OpenWithActionProvider;
import org.eclipse.emf.cdo.internal.ui.dialogs.EditObjectDialog;
import org.eclipse.emf.cdo.internal.ui.editor.CDOEditor;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionCache;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.edit.provider.ViewerNotification;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.spi.cdo.FSMUtil;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Eike Stepper
 */
public class CDOCheckoutContentProvider extends AdapterFactoryContentProvider implements IOpenListener
{
  private static final Set<Object> LOADING_OBJECTS = new HashSet<Object>();

  private static final Method GET_CHILDREN_FEATURES_METHOD = getMethod(ItemProviderAdapter.class,
      "getChildrenFeatures", Object.class);

  private static final Method FIND_ITEM_METHOD = getMethod(StructuredViewer.class, "findItem", Object.class);

  private static final CDOCheckoutManager CHECKOUT_MANAGER = CDOExplorerUtil.getCheckoutManager();

  private static CDOCheckoutContentProvider instance;

  private final IListener checkoutManagerListener = new IListener()
  {
    public void notifyEvent(IEvent event)
    {
      if (event instanceof IContainerEvent)
      {
        ViewerUtil.refresh(viewer, null);
      }
      else if (event instanceof CheckoutOpenEvent)
      {
        CheckoutOpenEvent e = (CheckoutOpenEvent)event;
        CDOCheckout checkout = e.getCheckout();

        if (!e.isOpen())
        {
          ViewerUtil.expand(viewer, checkout, false);
        }

        ViewerUtil.refresh(viewer, checkout);

        if (e.isOpen())
        {
          ViewerUtil.expand(viewer, checkout, true);
        }

        updatePropertySheetPage(checkout);
      }
      else if (event instanceof CDOExplorerManager.ElementsChangedEvent)
      {
        CDOExplorerManager.ElementsChangedEvent e = (CDOExplorerManager.ElementsChangedEvent)event;
        ElementsChangedEvent.StructuralImpact structuralImpact = e.getStructuralImpact();
        Collection<Object> changedElements = e.getChangedElements();

        if (structuralImpact != ElementsChangedEvent.StructuralImpact.NONE && changedElements.size() == 1)
        {
          Object changedElement = changedElements.iterator().next();
          if (changedElement instanceof CDOElement)
          {
            changedElement = ((CDOElement)changedElement).getParent();
          }

          if (structuralImpact == ElementsChangedEvent.StructuralImpact.PARENT)
          {
            changedElement = CDOExplorerUtil.getParent(changedElement);
          }

          ViewerUtil.refresh(viewer, changedElement);
        }
        else
        {
          ViewerUtil.update(viewer, changedElements);
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
                          ((TabbedPropertySheetPage)currentPage).refresh();
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

  private final Map<CDOCheckout, CDOCheckout> openingCheckouts = new ConcurrentHashMap<CDOCheckout, CDOCheckout>();

  private final Map<Object, Object[]> childrenCache = new ConcurrentHashMap<Object, Object[]>();

  private final ComposedAdapterFactory adapterFactory;

  private TreeViewer viewer;

  private Object input;

  public CDOCheckoutContentProvider()
  {
    super(null);

    adapterFactory = CDOEditor.createAdapterFactory(true);
    setAdapterFactory(adapterFactory);

    CHECKOUT_MANAGER.addListener(checkoutManagerListener);
    instance = this;
  }

  @Override
  public void dispose()
  {
    instance = null;
    super.dispose();

    CHECKOUT_MANAGER.removeListener(checkoutManagerListener);
    adapterFactory.dispose();
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

    super.inputChanged(newTreeViewer, oldInput, newInput);
  }

  @Override
  public boolean hasChildren(Object object)
  {
    if (object == input)
    {
      return !CHECKOUT_MANAGER.isEmpty();
    }

    if (object instanceof ViewerUtil.Pending)
    {
      return false;
    }

    if (object instanceof CDOCheckout)
    {
      CDOCheckout checkout = (CDOCheckout)object;
      if (!checkout.isOpen())
      {
        if (openingCheckouts.get(checkout) != null)
        {
          // This must be the ViewerUtil.Pending element.
          return true;
        }

        return false;
      }

      object = checkout.getRootObject();
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
          ITreeItemContentProvider provider = (ITreeItemContentProvider)adapterFactory.adapt(object,
              ITreeItemContentProvider.class);
          if (provider instanceof ItemProviderAdapter)
          {
            try
            {
              return hasChildren(cdoObject, revision, (ItemProviderAdapter)provider);
            }
            catch (Exception ex)
            {
              //$FALL-THROUGH$
            }
          }
        }
      }
    }

    return super.hasChildren(object);
  }

  @Override
  public Object[] getChildren(Object object)
  {
    if (object == input)
    {
      return CHECKOUT_MANAGER.getCheckouts();
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

    if (object instanceof CDOCheckout)
    {
      CDOCheckout checkout = (CDOCheckout)object;
      if (!checkout.isOpen())
      {
        openingCheckout = openingCheckouts.remove(checkout);
        if (openingCheckout == null)
        {
          return ViewerUtil.NO_CHILDREN;
        }
      }

      if (openingCheckout == null)
      {
        object = checkout.getRootObject();
      }
    }

    final List<CDORevision> loadedRevisions = new ArrayList<CDORevision>();
    final List<CDOID> missingIDs = new ArrayList<CDOID>();

    if (openingCheckout == null)
    {
      children = determineChildRevisions(object, loadedRevisions, missingIDs);
      if (children != null)
      {
        return CDOCheckoutContentModifier.Registry.INSTANCE.modifyChildren(object, children);
      }
    }

    final Object finalObject = object;
    final CDOCheckout finalOpeningCheckout = openingCheckout;

    synchronized (LOADING_OBJECTS)
    {
      LOADING_OBJECTS.add(originalObject);
    }

    new Job("Load " + finalObject)
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
            CDOObject cdoObject = CDOUtil.getCDOObject((EObject)finalObject);
            CDOView view = cdoObject.cdoView();
            CDORevisionManager revisionManager = view.getSession().getRevisionManager();

            List<CDORevision> revisions = revisionManager.getRevisions(missingIDs, view, CDORevision.UNCHUNKED,
                CDORevision.DEPTH_NONE, true);
            loadedRevisions.addAll(revisions);
          }

          Object[] children = CDOCheckoutContentProvider.super.getChildren(finalObject);
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

        // The viewer must be refreshed synchronously so that the loaded children don't get garbage collected.
        // Set the selection again to trigger, e.g., a History page update.
        ViewerUtil.refresh(viewer, originalObject, false, true);

        synchronized (LOADING_OBJECTS)
        {
          LOADING_OBJECTS.remove(originalObject);
        }

        return Status.OK_STATUS;
      }
    }.schedule();

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
            List<Object> result = new ArrayList<Object>();
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
          ITreeItemContentProvider provider = (ITreeItemContentProvider)adapterFactory.adapt(object,
              ITreeItemContentProvider.class);
          if (provider instanceof ItemProviderAdapter)
          {
            try
            {
              determineChildRevisions(cdoObject, revision, (ItemProviderAdapter)provider, loadedRevisions, missingIDs);
            }
            catch (Exception ex)
            {
              //$FALL-THROUGH$
            }

            if (missingIDs.isEmpty())
            {
              // All revisions are cached. Just return the objects without server round-trips.
              return super.getChildren(object);
            }
          }
        }
      }
    }

    return null;
  }

  @Override
  public Object[] getElements(Object object)
  {
    return getChildren(object);
  }

  @Override
  public Object getParent(Object object)
  {
    if (object == input)
    {
      return null;
    }

    if (object instanceof CDOCheckout)
    {
      return input;
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
      EObject eObject = (EObject)object;

      {
        Adapter adapter = EcoreUtil.getAdapter(eObject.eAdapters(), CDOCheckout.class);
        if (adapter instanceof CDOCheckout)
        {
          return adapter;
        }
      }
    }

    return super.getParent(object);
  }

  @Override
  public void notifyChanged(Notification notification)
  {
    Object notifier = notification.getNotifier();
    if (notifier instanceof EObject)
    {
      EObject eObject = (EObject)notifier;

      Object feature = notification.getFeature();
      if (feature instanceof EReference)
      {
        EReference reference = (EReference)feature;
        if (reference.isContainment())
        {
          Adapter adapter = EcoreUtil.getAdapter(eObject.eAdapters(), CDOCheckout.class);
          if (adapter instanceof CDOCheckout)
          {
            CDOCheckout checkout = (CDOCheckout)adapter;
            if (checkout.isOpen())
            {
              notification = new ViewerNotification(notification, checkout, true, true);
            }
          }
        }
      }
    }

    super.notifyChanged(notification);
  }

  public void selectObject(final Object object)
  {
    final Control control = viewer.getControl();
    if (!control.isDisposed())
    {
      final long end = System.currentTimeMillis() + 5000L;
      final Display display = control.getDisplay();
      display.asyncExec(new Runnable()
      {
        public void run()
        {
          if (control.isDisposed())
          {
            return;
          }

          LinkedList<Object> path = new LinkedList<Object>();
          CDOCheckout checkout = CDOExplorerUtil.walkUp(object, path);
          if (checkout != null)
          {
            viewer.setExpandedState(checkout, true);

            path.removeFirst();
            path.removeLast();

            for (Object object : path)
            {
              viewer.setExpandedState(object, true);
            }

            viewer.setSelection(new StructuredSelection(object), true);
            if (viewer.getSelection().isEmpty())
            {
              if (isObjectLoading(object) || System.currentTimeMillis() < end)
              {
                display.timerExec(50, this);
              }
            }
          }
        }
      });
    }
  }

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
          CDOCheckout checkout = (CDOCheckout)element;
          if (checkout.getState() == State.Closed)
          {
            // Mark this checkout as loading.
            openingCheckouts.put(checkout, checkout);

            // Trigger hasChildren().
            ViewerUtil.refresh(viewer, checkout);

            // Trigger getChildren().
            ViewerUtil.expand(viewer, checkout, true);
          }
        }
        else if (element instanceof CDOResourceFolder)
        {
          // Do nothing special.
        }
        else if (element instanceof EObject)
        {
          EObject eObject = (EObject)element;

          if (!editObject(eObject, adapterFactory, getWorkbenchPage().getWorkbenchWindow().getShell()))
          {
            OpenWithActionProvider.openEditor(getWorkbenchPage(), eObject, null);
          }
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
    if (eObject instanceof InternalCDOObject)
    {
      return (InternalCDOObject)eObject;
    }

    Adapter adapter = FSMUtil.getLegacyAdapter(eObject);
    if (adapter != null)
    {
      return (InternalCDOObject)adapter;
    }

    return null;
  }

  private static boolean hasChildren(InternalCDOObject cdoObject, InternalCDORevision revision,
      ItemProviderAdapter provider) throws Exception
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

  private static void determineChildRevisions(InternalCDOObject cdoObject, InternalCDORevision revision,
      ItemProviderAdapter provider, List<CDORevision> loadedRevisions, List<CDOID> missingIDs) throws Exception
  {
    InternalCDOView view = cdoObject.cdoView();
    InternalCDORevisionCache revisionCache = view.getSession().getRevisionManager().getCache();

    for (EStructuralFeature feature : getChildrenFeatures(cdoObject, provider))
    {
      if (feature.isMany())
      {
        CDOList list = revision.getList(feature);
        for (Object object : list)
        {
          determineChildRevision(loadedRevisions, missingIDs, view, revisionCache, object);
        }
      }
      else
      {
        Object value = revision.getValue(feature);
        determineChildRevision(loadedRevisions, missingIDs, view, revisionCache, value);
      }
    }
  }

  private static void determineChildRevision(List<CDORevision> loadedRevisions, List<CDOID> missingIDs,
      InternalCDOView view, InternalCDORevisionCache cache, Object object)
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
  private static Collection<? extends EStructuralFeature> getChildrenFeatures(InternalCDOObject cdoObject,
      ItemProviderAdapter provider) throws Exception
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

  private static boolean isObjectLoading(Object object)
  {
    synchronized (LOADING_OBJECTS)
    {
      return LOADING_OBJECTS.contains(object);
    }
  }

  public static boolean editObject(EObject eObject, AdapterFactory adapterFactory, Shell shell)
  {
    boolean edited = false;
  
    if (!(eObject instanceof CDOResourceNode))
    {
      CDOCheckout checkout = CDOExplorerUtil.getCheckout(eObject);
      if (checkout != null)
      {
        EditingDomain editingDomain = new AdapterFactoryEditingDomain(adapterFactory, new BasicCommandStack());
        ResourceSet resourceSet = editingDomain.getResourceSet();
        CDOTransaction transaction = checkout.openTransaction(resourceSet);
  
        try
        {
          EObject txObject = transaction.getObject(eObject);
  
          int result = new EditObjectDialog(shell, txObject).open();
          edited = true;
  
          if (result == EditObjectDialog.OK)
          {
            transaction.commit();
          }
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
        }
        finally
        {
          transaction.close();
        }
      }
    }
  
    return edited;
  }

  public static final CDOCheckoutContentProvider getInstance()
  {
    return instance;
  }
}
