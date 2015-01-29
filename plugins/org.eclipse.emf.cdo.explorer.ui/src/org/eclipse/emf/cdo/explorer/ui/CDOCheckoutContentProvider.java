/*
 * Copyright (c) 2009-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.explorer.ui;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.explorer.CDOCheckout;
import org.eclipse.emf.cdo.explorer.CDOCheckoutManager;
import org.eclipse.emf.cdo.explorer.CDOCheckoutManager.CheckoutOpenEvent;
import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionCache;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.EMFEditPlugin;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.ViewerNotification;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.spi.cdo.FSMUtil;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.widgets.TreeItem;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Eike Stepper
 */
public class CDOCheckoutContentProvider extends AdapterFactoryContentProvider implements IOpenListener, TreeListener
{
  private static final Method GET_CHILDREN_FEATURES_METHOD = getMethod(ItemProviderAdapter.class,
      "getChildrenFeatures", Object.class);

  private static final Method FIND_ITEM_METHOD = getMethod(StructuredViewer.class, "findItem", Object.class);

  private static final CDOCheckoutManager CHECKOUT_MANAGER = CDOExplorerUtil.getCheckoutManager();

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
      }
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

    ComposedAdapterFactory.Descriptor.Registry registry = EMFEditPlugin.getComposedAdapterFactoryDescriptorRegistry();
    adapterFactory = new ComposedAdapterFactory(registry);
    adapterFactory.addAdapterFactory(new ResourceItemProviderAdapterFactory());
    adapterFactory.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());
    setAdapterFactory(adapterFactory);

    CHECKOUT_MANAGER.addListener(checkoutManagerListener);
  }

  @Override
  public void dispose()
  {
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
        viewer.getTree().removeTreeListener(this);
      }

      viewer = newTreeViewer;

      if (viewer != null)
      {
        viewer.getTree().addTreeListener(this);
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
        return children;
      }
    }

    final Object finalObject = object;
    final CDOCheckout finalOpeningCheckout = openingCheckout;

    new Job("Load " + finalObject)
    {
      @Override
      protected IStatus run(IProgressMonitor monitor)
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
        childrenCache.put(originalObject, children);

        // The viewer must be refreshed synchronously so that the loaded children don't get garbage collected.
        ViewerUtil.refresh(viewer, originalObject, false);
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
            Object[] result = new Object[childCount];
            for (int i = 0; i < childCount; i++)
            {
              TreeItem childItem = childItems[i];
              result[i] = childItem.getData();
            }

            return result;
          }
        }
      }
      catch (Exception ex)
      {
        //$FALL-THROUGH$
      }
    }

    return new Object[] { new ViewerUtil.Pending(originalObject) };
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

  public void treeCollapsed(TreeEvent e)
  {
    // TODO Check if this optimization is still needed!
    int xxx;

    // if (e.data instanceof EObject)
    // {
    // // Make sure that invisible children can be garbage collected.
    // // In getChildren() is a special check for collapsed parents that removes of the TreeItems from the Tree.
    // viewer.refresh(e.data, false);
    // }
  }

  public void treeExpanded(TreeEvent e)
  {
    // Do nothing.
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
          if (!checkout.isOpen())
          {
            // Mark this checkout as loading.
            openingCheckouts.put(checkout, checkout);

            // Trigger hasChildren().
            ViewerUtil.refresh(viewer, checkout);

            // Trigger getChildren().
            ViewerUtil.expand(viewer, checkout, true);
          }
        }
      }
    }
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
}
