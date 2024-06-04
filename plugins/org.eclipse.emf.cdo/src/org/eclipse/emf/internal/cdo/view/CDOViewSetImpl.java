/*
 * Copyright (c) 2009-2013, 2015, 2018-2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 *    Victor Roldan Betancort - bug 338921
 *    Christian W. Damus (CEA) - bug 399279: support removal from resource set adapters
 */
package org.eclipse.emf.internal.cdo.view;

import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFactory;
import org.eclipse.emf.cdo.util.CDOURIUtil;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewProvider;
import org.eclipse.emf.cdo.view.CDOViewProvider.CDOViewProvider2;
import org.eclipse.emf.cdo.view.CDOViewSet;

import org.eclipse.emf.internal.cdo.messages.Messages;

import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.container.SetContainer;
import org.eclipse.net4j.util.container.SingleDeltaContainerEvent;
import org.eclipse.net4j.util.event.IListener;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.emf.common.notify.impl.NotifierImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOView;
import org.eclipse.emf.spi.cdo.InternalCDOViewSet;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class CDOViewSetImpl extends NotifierImpl implements InternalCDOViewSet
{
  public static final String KEY_VIEW_URI = "org.eclipse.emf.cdo.viewURI";

  public static final SetContainer<CDOViewSet> REGISTRY = new SetContainer<CDOViewSet>(CDOViewSet.class, new LinkedHashSet<>())
  {
    @Override
    public void setPersistence(Persistence<CDOViewSet> persistence)
    {
      throw new UnsupportedOperationException();
    }
  };

  private static int lastID;

  private final int id;

  @ExcludeFromDump
  private final org.eclipse.net4j.util.event.Notifier notifier = new org.eclipse.net4j.util.event.Notifier();

  private final Set<InternalCDOView> views = new LinkedHashSet<>();

  private final Map<URI, InternalCDOView> mapOfViews = new HashMap<>();

  private final ThreadLocal<Boolean> ignoreNotifications = new InheritableThreadLocal<>();

  private CDOResourceFactory resourceFactory;

  private CDOViewSetPackageRegistryImpl packageRegistry;

  private ResourceSet resourceSet;

  private CDOAdapterPolicy defaultClearAdapterPolicy;

  public CDOViewSetImpl()
  {
    synchronized (REGISTRY)
    {
      id = ++lastID;
    }
  }

  @Override
  public int getID()
  {
    return id;
  }

  @Override
  public ResourceSet getResourceSet()
  {
    return resourceSet;
  }

  @Override
  public EPackage.Registry getPackageRegistry()
  {
    return packageRegistry;
  }

  @Override
  public CDOResourceFactory getResourceFactory()
  {
    return resourceFactory;
  }

  @Override
  public CDOAdapterPolicy getDefaultClearAdapterPolicy()
  {
    return defaultClearAdapterPolicy;
  }

  @Override
  public void setDefaultClearAdapterPolicy(CDOAdapterPolicy defaultClearAdapterPolicy)
  {
    this.defaultClearAdapterPolicy = defaultClearAdapterPolicy;
  }

  @Override
  public ExecutorService getExecutorService()
  {
    synchronized (views)
    {
      for (InternalCDOView view : views)
      {
        ExecutorService executorService = view.getExecutorService();
        if (executorService != null)
        {
          return executorService;
        }
      }
    }

    return null;
  }

  @Override
  public void addListener(IListener listener)
  {
    notifier.addListener(listener);
  }

  @Override
  public void removeListener(IListener listener)
  {
    notifier.removeListener(listener);
  }

  @Override
  public boolean hasListeners()
  {
    return notifier.hasListeners();
  }

  @Override
  public IListener[] getListeners()
  {
    return notifier.getListeners();
  }

  @Override
  public boolean isEmpty()
  {
    synchronized (views)
    {
      return views.isEmpty();
    }
  }

  @Override
  public CDOView[] getElements()
  {
    return getViews();
  }

  @Override
  public CDOView[] getViews()
  {
    synchronized (views)
    {
      return views.toArray(new CDOView[views.size()]);
    }
  }

  /**
   * @throws IllegalArgumentException
   *           if repositoryUUID doesn't match any CDOView.
   */
  @Override
  @Deprecated
  public InternalCDOView resolveView(String repositoryUUID)
  {
    return resolveView(CDOURIUtil.PROTOCOL_NAME + "://" + repositoryUUID);
  }

  @Override
  public InternalCDOView resolveView(URI viewURI)
  {
    InternalCDOView view = null;
    synchronized (views)
    {
      view = mapOfViews.get(viewURI);
      if (view == null)
      {
        if (viewURI != null)
        {
          throw new CDOViewSetException(MessageFormat.format(Messages.getString("CDOViewSetImpl.0"), viewURI)); //$NON-NLS-1$
        }

        if (mapOfViews.size() == 1)
        {
          return views.iterator().next();
        }

        if (mapOfViews.size() == 0)
        {
          return null;
        }

        throw new CDOViewSetException(Messages.getString("CDOViewSetImpl.1")); //$NON-NLS-1$
      }
    }

    return view;
  }

  @Override
  public void add(InternalCDOView view)
  {
    URI viewURI = getViewURI(view);
    boolean first;

    synchronized (views)
    {
      CDOView lookupView = mapOfViews.get(viewURI);
      if (lookupView != null)
      {
        throw new CDOViewSetException(MessageFormat.format(Messages.getString("CDOViewSetImpl.2"), viewURI, lookupView)); //$NON-NLS-1$
      }

      first = views.isEmpty();

      views.add(view);
      mapOfViews.put(viewURI, view);
    }

    if (first)
    {
      synchronized (REGISTRY)
      {
        REGISTRY.addElement(this);
      }
    }

    notifier.fireEvent(new SingleDeltaContainerEvent<>(this, view, IContainerDelta.Kind.ADDED));

    if (eNotificationRequired())
    {
      eNotify(new NotificationImpl(Notification.ADD, null, view));
    }
  }

  @Override
  public void remove(InternalCDOView view)
  {
    List<Resource> resToRemove = new ArrayList<>();
    boolean last = false;

    synchronized (views)
    {
      // It is important to remove view from the list first. It is the way we can differentiate close and detach.
      if (views.remove(view))
      {
        URI viewURI = getViewURI(view);
        mapOfViews.remove(viewURI);

        last = views.isEmpty();

        for (Resource resource : getResourceSet().getResources())
        {
          if (resource instanceof CDOResource && ((CDOResource)resource).cdoView() == view)
          {
            resToRemove.add(resource);
          }
        }
      }
    }

    getResourceSet().getResources().removeAll(resToRemove);

    if (last)
    {
      synchronized (REGISTRY)
      {
        REGISTRY.removeElement(this);
      }
    }

    notifier.fireEvent(new SingleDeltaContainerEvent<>(this, view, IContainerDelta.Kind.REMOVED));

    if (eNotificationRequired())
    {
      eNotify(new NotificationImpl(Notification.REMOVE, view, null));
    }
  }

  @Override
  public void remapView(InternalCDOView view)
  {
    synchronized (views)
    {
      for (Iterator<Map.Entry<URI, InternalCDOView>> it = mapOfViews.entrySet().iterator(); it.hasNext();)
      {
        Map.Entry<URI, InternalCDOView> entry = it.next();
        if (entry.getValue() == view)
        {
          it.remove();
          break;
        }
      }

      URI viewURI = getViewURI(view);
      mapOfViews.put(viewURI, view);
    }
  }

  @Override
  public Notifier getTarget()
  {
    return resourceSet;
  }

  @Override
  public void setTarget(Notifier newTarget)
  {
    if (newTarget == resourceSet)
    {
      return;
    }

    if (newTarget == null)
    {
      // UNINSTALL
      if (hasActiveView())
      {
        resourceSet.eAdapters().add(this); // Add me back to the resource set's list of adapters.
        return;
      }
    }
    else
    {
      // INSTALL
      if (!isAdapterForType(newTarget))
      {
        throw new CDOViewSetException(MessageFormat.format(Messages.getString("CDOViewSetImpl.3"), newTarget)); //$NON-NLS-1$
      }

      if (resourceSet != null)
      {
        throw new CDOViewSetException(Messages.getString("CDOViewSetImpl.4")); //$NON-NLS-1$
      }
    }

    resourceSet = (ResourceSet)newTarget;

    if (resourceSet != null)
    {
      EPackage.Registry oldPackageRegistry = resourceSet.getPackageRegistry();
      packageRegistry = new CDOViewSetPackageRegistryImpl(this, oldPackageRegistry);
      resourceSet.setPackageRegistry(packageRegistry);

      Map<String, Object> map = resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap();
      Resource.Factory resourceFactory = (Resource.Factory)map.get(CDOProtocolConstants.PROTOCOL_NAME);
      if (resourceFactory instanceof CDOResourceFactory)
      {
        this.resourceFactory = (CDOResourceFactory)resourceFactory;
      }
      else if (resourceFactory == null)
      {
        this.resourceFactory = CDOResourceFactory.INSTANCE;
        map.put(CDOProtocolConstants.PROTOCOL_NAME, this.resourceFactory);
      }
    }
  }

  @Override
  public boolean isAdapterForType(Object type)
  {
    return type instanceof ResourceSet;
  }

  @Override
  public synchronized <V> V executeWithoutNotificationHandling(Callable<V> callable)
  {
    Boolean wasIgnore = ignoreNotifications.get();

    try
    {
      ignoreNotifications.set(true);
      return callable.call();
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
    finally
    {
      if (wasIgnore == null)
      {
        ignoreNotifications.remove();
      }
    }
  }

  @Override
  public void notifyChanged(Notification notification)
  {
    // The resource <-> view association is done in CDOResourceImpl.basicSetResourceSet()

    if (ignoreNotifications.get() == null)
    {
      // We need to deregister CDOResources from CDOView if removed from the ResourceSet, see bug 338921
      switch (notification.getEventType())
      {
      case Notification.REMOVE_MANY:
        deregisterResources((List<?>)notification.getOldValue());
        break;

      case Notification.REMOVE:
        deregisterResources(Collections.singleton(notification.getOldValue()));
        break;
      }
    }
  }

  @Override
  public String toString()
  {
    return "View Set " + id;
  }

  private void deregisterResources(Collection<?> potentialResources)
  {
    List<CDOResource> allDirtyResources = new ArrayList<>();

    try
    {
      Map<CDOView, List<CDOResource>> resourcesPerView = getResourcesPerView(potentialResources);

      for (Map.Entry<CDOView, List<CDOResource>> entry : resourcesPerView.entrySet())
      {
        InternalCDOView view = (InternalCDOView)entry.getKey();
        List<CDOResource> resources = entry.getValue();

        if (view.isDirty())
        {
          List<CDOResource> dirtyResources = getDirtyResources(resources);
          if (!dirtyResources.isEmpty())
          {
            allDirtyResources.addAll(dirtyResources);
            resourceSet.getResources().addAll(resources);
            continue;
          }
        }

        for (CDOResource resource : resources)
        {
          InternalCDOObject internalResource = (InternalCDOObject)resource;
          view.deregisterObject(internalResource);
          internalResource.cdoInternalSetState(CDOState.INVALID);
        }
      }
    }
    finally
    {
      int size = allDirtyResources.size();
      if (size == 1)
      {
        throw new CDOViewSetException("Attempt to remove a dirty resource from a resource set: " + allDirtyResources.get(0));
      }
      else if (size > 1)
      {
        throw new CDOViewSetException("Attempt to remove dirty resources from a resource set: " + allDirtyResources);
      }
    }
  }

  private Map<CDOView, List<CDOResource>> getResourcesPerView(Collection<?> potentialResources)
  {
    Map<CDOView, List<CDOResource>> resourcesPerView = new HashMap<>();

    for (Object potentialResource : potentialResources)
    {
      if (potentialResource instanceof CDOResource)
      {
        CDOResource resource = (CDOResource)potentialResource;
        CDOView view = resource.cdoView();

        if (views.contains(view))
        {
          List<CDOResource> resources = resourcesPerView.get(view);
          if (resources == null)
          {
            resources = new ArrayList<>();
            resourcesPerView.put(view, resources);
          }

          resources.add(resource);
        }
      }
    }

    return resourcesPerView;
  }

  private boolean hasActiveView()
  {
    for (CDOView view : getViews())
    {
      if (!view.isClosed())
      {
        return true;
      }
    }

    return false;
  }

  private static List<CDOResource> getDirtyResources(List<CDOResource> resources)
  {
    List<CDOResource> dirtyResources = new ArrayList<>();
    for (CDOResource resource : resources)
    {
      switch (resource.cdoState())
      {
      case NEW:
      case DIRTY:
      case CONFLICT:
      case INVALID_CONFLICT:
        dirtyResources.addAll(resources);
      }
    }

    return dirtyResources;
  }

  private static URI getViewURI(InternalCDOView view)
  {
    Object value = view.properties().get(KEY_VIEW_URI);
    if (value instanceof String)
    {
      return URI.createURI((String)value);
    }

    CDOViewProvider provider = view.getProvider();
    if (provider instanceof CDOViewProvider2)
    {
      URI viewURI = ((CDOViewProvider2)provider).getViewURI(view);
      if (viewURI != null)
      {
        return viewURI;
      }
    }

    return PluginContainerViewProvider.INSTANCE.getViewURI(view);
  }

  public static CDOViewSetImpl[] getViewSets()
  {
    return null;
  }
}
