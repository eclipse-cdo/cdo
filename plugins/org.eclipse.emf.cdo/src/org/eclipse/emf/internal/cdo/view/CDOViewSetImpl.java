/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.internal.cdo.view;

import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFactory;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.emf.common.notify.impl.NotifierImpl;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.Resource.Factory.Registry;
import org.eclipse.emf.spi.cdo.InternalCDOView;
import org.eclipse.emf.spi.cdo.InternalCDOViewSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class CDOViewSetImpl extends NotifierImpl implements InternalCDOViewSet
{
  private Set<InternalCDOView> views = new HashSet<InternalCDOView>();

  private Map<String, InternalCDOView> mapOfViews = new HashMap<String, InternalCDOView>();

  private CDOResourceFactory resourceFactory = CDOResourceFactory.eINSTANCE;

  private CDOViewSetPackageRegistryImpl packageRegistry;

  private ResourceSet resourceSet;

  public CDOViewSetImpl()
  {
  }

  public ResourceSet getResourceSet()
  {
    return resourceSet;
  }

  public EPackage.Registry getPackageRegistry()
  {
    return packageRegistry;
  }

  public CDOResourceFactory getResourceFactory()
  {
    return resourceFactory;
  }

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
  public InternalCDOView resolveView(String repositoryUUID)
  {
    InternalCDOView view = null;
    synchronized (views)
    {
      view = mapOfViews.get(repositoryUUID);
      if (view == null)
      {
        if (repositoryUUID != null)
        {
          throw new IllegalArgumentException("Cannot find associated CDOView for repository " + repositoryUUID);
        }

        if (mapOfViews.size() == 1)
        {
          return views.iterator().next();
        }

        if (mapOfViews.size() == 0)
        {
          return null;
        }

        throw new IllegalStateException("Don't know which CDOView to take since no authority has been specified");
      }
    }

    return view;
  }

  public InternalCDOView getView(String repositoryUUID)
  {
    synchronized (views)
    {
      return mapOfViews.get(repositoryUUID);
    }
  }

  public void add(InternalCDOView view)
  {
    String repositoryUUID = view.getSession().repository().getUUID();
    synchronized (views)
    {
      CDOView lookupView = mapOfViews.get(repositoryUUID);
      if (lookupView != null)
      {
        throw new RuntimeException("Only one view per repository can be open for the same resource set");
      }

      views.add(view);
      mapOfViews.put(repositoryUUID, view);
    }

    if (eNotificationRequired())
    {
      NotificationImpl notification = new NotificationImpl(NotificationImpl.ADD, null, view);
      eNotify(notification);
    }
  }

  public void remove(InternalCDOView view)
  {
    String repositoryUUID = view.getSession().repository().getUUID();
    List<Resource> resToRemove = new ArrayList<Resource>();
    synchronized (views)
    {
      // It is important to remove view from the list first. It is the way we can differentiate close and detach.
      views.remove(view);
      mapOfViews.remove(repositoryUUID);

      for (Resource resource : getResourceSet().getResources())
      {
        if (resource instanceof CDOResource)
        {
          CDOResource cdoRes = (CDOResource)resource;
          if (cdoRes.cdoView() == view)
          {
            resToRemove.add(resource);
          }
        }
      }
    }

    getResourceSet().getResources().removeAll(resToRemove);
    if (eNotificationRequired())
    {
      NotificationImpl notification = new NotificationImpl(NotificationImpl.REMOVE, view, null);
      eNotify(notification);
    }
  }

  public Notifier getTarget()
  {
    return resourceSet;
  }

  public void setTarget(Notifier newTarget)
  {
    if (!isAdapterForType(newTarget))
    {
      throw new IllegalArgumentException("Unsupported target: " + newTarget);
    }

    if (resourceSet != null)
    {
      throw new IllegalStateException("Cannot associate more than 1 resource set with this view set");
    }

    resourceSet = (ResourceSet)newTarget;
    EPackage.Registry oldPackageRegistry = resourceSet.getPackageRegistry();
    packageRegistry = new CDOViewSetPackageRegistryImpl(this, oldPackageRegistry);
    resourceSet.setPackageRegistry(packageRegistry);

    Registry registry = resourceSet.getResourceFactoryRegistry();
    Map<String, Object> map = registry.getProtocolToFactoryMap();
    map.put(CDOProtocolConstants.PROTOCOL_NAME, getResourceFactory());
  }

  public boolean isAdapterForType(Object type)
  {
    return type instanceof ResourceSet;
  }

  public void notifyChanged(Notification notification)
  {
  }
}
