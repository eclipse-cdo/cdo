/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui;

import org.eclipse.emf.cdo.CDOElement;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionCache;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.lifecycle.LifecycleException;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.views.ItemProvider;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.TreeItem;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Eike Stepper
 */
public abstract class CDOContentProvider<CONTEXT> implements ITreeContentProvider
{
  private static final Set<Object> LOADING_OBJECTS = new HashSet<>();

  private static final Method GET_CHILDREN_FEATURES_METHOD = getMethod(ItemProviderAdapter.class, "getChildrenFeatures", Object.class);

  private static final Method FIND_ITEM_METHOD = getMethod(StructuredViewer.class, "findItem", Object.class);

  private final Map<Object, Object[]> childrenCache = new ConcurrentHashMap<>();

  private TreeViewer viewer;

  private Object input;

  private boolean hideObjects;

  public CDOContentProvider()
  {
  }

  public final TreeViewer getViewer()
  {
    return viewer;
  }

  public final boolean isHideObjects()
  {
    return hideObjects;
  }

  public final void setHideObjects(boolean hideObjects)
  {
    boolean oldValue = this.hideObjects;
    if (hideObjects != oldValue)
    {
      this.hideObjects = hideObjects;

      if (viewer != null)
      {
        UIUtil.asyncExec(viewer.getControl().getDisplay(), () -> UIUtil.refreshViewer(viewer));
      }
    }
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
        unhookViewer(viewer);
      }

      viewer = newTreeViewer;

      if (viewer != null)
      {
        hookViewer(viewer);
      }
    }

    input = newInput;
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
      if (object instanceof ViewerUtil.Pending)
      {
        return false;
      }

      if (hideObjects && object instanceof CDOResource)
      {
        CDOResource resource = (CDOResource)object;
        if (!resource.isRoot())
        {
          return false;
        }
      }

      if (isContext(object))
      {
        @SuppressWarnings("unchecked")
        CONTEXT context = (CONTEXT)object;

        switch (getContextState(context))
        {
        case Closed:
          return false;

        case Opening:
          // This must be the ViewerUtil.Pending element.
          return true;

        case Open:
          object = getRootObject(context);
          break;
        }
      }

      if (object instanceof CDOElement)
      {
        CDOElement element = (CDOElement)object;
        return element.hasChildren();
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
              ITreeItemContentProvider provider = (ITreeItemContentProvider)adapt(object, ITreeItemContentProvider.class);
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

      ITreeContentProvider contentProvider = getContentProvider(object);
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
      if (object instanceof ViewerUtil.Pending)
      {
        return ViewerUtil.NO_CHILDREN;
      }

      if (hideObjects && object instanceof CDOResource)
      {
        CDOResource resource = (CDOResource)object;
        if (!resource.isRoot())
        {
          return ViewerUtil.NO_CHILDREN;
        }
      }

      if (object instanceof CDOElement)
      {
        CDOElement element = (CDOElement)object;
        return element.getChildren();
      }

      Object originalObject = object;
      Object[] children = childrenCache.remove(originalObject);
      if (children != null)
      {
        return children;
      }

      CONTEXT openingContext = null;
      if (isContext(object))
      {
        @SuppressWarnings("unchecked")
        CONTEXT context = (CONTEXT)object;

        switch (getContextState(context))
        {
        case Closed:
          return ViewerUtil.NO_CHILDREN;

        case Opening:
          openingContext = context;
          break;

        case Open:
          object = getRootObject(context);
          break;
        }
      }

      Object finalObject = object;
      CONTEXT finalOpeningContext = openingContext;

      ITreeContentProvider contentProvider = getContentProvider(finalObject);
      if (contentProvider == null)
      {
        return ItemProvider.NO_ELEMENTS;
      }

      List<CDORevision> loadedRevisions = new ArrayList<>();
      List<CDOID> missingIDs = new ArrayList<>();

      if (finalOpeningContext == null)
      {
        children = determineChildRevisions(object, loadedRevisions, missingIDs);
        if (children != null)
        {
          return modifyChildren(object, children);
        }
      }

      boolean firstLoad;
      synchronized (LOADING_OBJECTS)
      {
        firstLoad = LOADING_OBJECTS.add(originalObject);
      }

      if (firstLoad || finalOpeningContext == null)
      {
        Job job = new Job("Load " + finalObject)
        {
          @Override
          protected IStatus run(IProgressMonitor monitor)
          {
            CDOView view = null;

            try
            {
              if (finalOpeningContext != null)
              {
                openContext(finalOpeningContext);
                determineChildRevisions(finalObject, loadedRevisions, missingIDs);
              }

              if (!missingIDs.isEmpty())
              {
                CDOObject cdoObject = getCDOObject((EObject)finalObject);
                view = cdoObject.cdoView();
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

              children = modifyChildren(finalObject, children);
              childrenCache.put(originalObject, children);
            }
            catch (Exception ex)
            {
              childrenCache.remove(originalObject);

              if (finalOpeningContext != null)
              {
                closeContext(finalOpeningContext);
              }

              if (view == null)
              {
                CDOObject cdoObject = getCDOObject((EObject)finalObject);
                view = cdoObject.cdoView();
              }

              if (view == null || !view.isClosed())
              {
                OM.LOG.error(ex);
              }

              // final Control control = viewer.getControl();
              // if (!control.isDisposed())
              // {
              // UIUtil.getDisplay().asyncExec(new Runnable()
              // {
              // @Override
              // public void run()
              // {
              // try
              // {
              // if (!control.isDisposed())
              // {
              // Shell shell = control.getShell();
              // String title = (finalOpeningContext != null ? "Open" : "Load") + " Error";
              // MessageDialog.openError(shell, title, ex.getMessage());
              // }
              // }
              // catch (Exception ex)
              // {
              // OM.LOG.error(ex);
              // }
              // }
              // });
              // }
            }

            RunnableViewerRefresh viewerRefresh = getViewerRefresh();
            viewerRefresh.addNotification(originalObject, true, true, () -> {
              synchronized (LOADING_OBJECTS)
              {
                LOADING_OBJECTS.remove(originalObject);
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
      if (finalOpeningContext != null)
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

  @Override
  public Object getParent(Object object)
  {
    try
    {
      if (object instanceof ViewerUtil.Pending)
      {
        return ((ViewerUtil.Pending)object).getParent();
      }

      if (object instanceof CDOElement)
      {
        CDOElement element = (CDOElement)object;
        return element.getParent();
      }

      if (object instanceof EObject)
      {
        EObject eObject = CDOUtil.getEObject((EObject)object);

        CDOElement element = CDOElement.getFor(eObject);
        if (element != null)
        {
          return element;
        }

        ITreeContentProvider contentProvider = getContentProvider(object);
        if (contentProvider != null)
        {
          return contentProvider.getParent(object);
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

  protected void hookViewer(TreeViewer viewer)
  {
  }

  protected void unhookViewer(TreeViewer viewer)
  {
  }

  protected abstract Object adapt(Object target, Object type);

  protected abstract Object[] modifyChildren(Object parent, Object[] children);

  protected abstract ITreeContentProvider getContentProvider(Object object);

  protected abstract RunnableViewerRefresh getViewerRefresh();

  protected abstract boolean isContext(Object object);

  protected abstract ContextState getContextState(CONTEXT context);

  protected abstract void openContext(CONTEXT context);

  protected abstract void closeContext(CONTEXT context);

  protected abstract Object getRootObject(CONTEXT context);

  private Object[] determineChildRevisions(Object object, List<CDORevision> loadedRevisions, List<CDOID> missingIDs)
  {
    if (object instanceof EObject)
    {
      if (GET_CHILDREN_FEATURES_METHOD != null)
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
              ITreeItemContentProvider provider = (ITreeItemContentProvider)adapt(object, ITreeItemContentProvider.class);
              if (provider instanceof ItemProviderAdapter)
              {
                determineChildRevisions(cdoObject, revision, (ItemProviderAdapter)provider, loadedRevisions, missingIDs);

                if (missingIDs.isEmpty())
                {
                  // All revisions are cached. Just return the objects without server round-trips.
                  ITreeContentProvider contentProvider = getContentProvider(object);
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
    }
    else if (object instanceof ResourceSet)
    {
      EList<Resource> resources = ((ResourceSet)object).getResources();
      return resources.toArray(new Resource[resources.size()]);
    }

    return null;
  }

  protected static boolean isObjectLoading(Object... objects)
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

  /**
   * @author Eike Stepper
   */
  public static enum ContextState
  {
    Opening, Open, Closed
  }
}
