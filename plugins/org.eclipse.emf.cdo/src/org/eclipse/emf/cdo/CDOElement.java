/*
 * Copyright (c) 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.AdapterUtil;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.spi.cdo.FSMUtil;
import org.eclipse.emf.spi.cdo.InternalCDOObject;

import org.eclipse.core.runtime.IAdaptable;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

/**
 * An {@link Adapter adapter} for {@link EObject EObjects} that is able to
 * impose an alternative contents tree structure.
 *
 * @author Eike Stepper
 * @since 4.4
 */
public class CDOElement extends AdapterImpl implements IAdaptable
{
  private static final Class<CDOElement> TYPE = CDOElement.class;

  private final EObject delegate;

  private final List<Object> children = new ArrayList<>();

  public CDOElement(EObject delegate)
  {
    this.delegate = (EObject)getInstance(delegate);
  }

  public Object getDelegate()
  {
    return delegate;
  }

  public Object getParent()
  {
    return CDOElement.getParentOf(delegate);
  }

  public Object[] getChildren()
  {
    return children.toArray();
  }

  public boolean hasChildren()
  {
    return !children.isEmpty();
  }

  public void addChild(Object child)
  {
    child = getInstance(child);

    EList<Adapter> adapters = removeFrom(child);
    if (adapters != null)
    {
      synchronized (TYPE)
      {
        adapters.add(this);
      }
    }

    children.add(child);
  }

  public void reset()
  {
    children.clear();
  }

  @Override
  public boolean isAdapterForType(Object type)
  {
    return type == TYPE;
  }

  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public Object getAdapter(Class adapter)
  {
    if (adapter == EObject.class)
    {
      return delegate;
    }

    return AdapterUtil.adapt(this, adapter, false);
  }

  public String toString(Object child)
  {
    return child.toString();
  }

  @Override
  public String toString()
  {
    return delegate.toString();
  }

  public static EObject getParentOf(EObject eObject)
  {
    if (eObject == null)
    {
      return null;
    }

    CDOObject cdoObject = CDOUtil.getCDOObject(eObject, false);
    if (cdoObject != null && (FSMUtil.isInvalid(cdoObject) || cdoObject.cdoView() == null || cdoObject.cdoView().isClosed()))
    {
      return null;
    }

    EObject container = eObject.eContainer();
    if (container != null)
    {
      return container;
    }

    if (eObject instanceof CDOResource)
    {
      CDOResource resource = (CDOResource)eObject;
      if (resource.isRoot())
      {
        return null;
      }
    }

    Resource resource = eObject.eResource();
    if (resource instanceof CDOResource)
    {
      return (CDOResource)resource;
    }

    if (eObject instanceof CDOResourceNode)
    {
      CDOView view = ((CDOResourceNode)eObject).cdoView();
      if (view != null)
      {
        return view.getRootResource();
      }
    }

    return null;
  }

  public static CDOElement getFor(Object object)
  {
    if (object instanceof Notifier)
    {
      Notifier notifier = (Notifier)object;
      return (CDOElement)EcoreUtil.getExistingAdapter(notifier, TYPE);
    }

    return null;
  }

  public static EList<Adapter> removeFrom(Object object)
  {
    if (object instanceof EObject)
    {
      EObject eObject = (EObject)object;
      EList<Adapter> adapters = eObject.eAdapters();

      synchronized (TYPE)
      {
        removeSafe(adapters);
      }

      return adapters;
    }

    return null;
  }

  private static void removeSafe(EList<Adapter> adapters)
  {
    try
    {
      for (Iterator<Adapter> it = adapters.iterator(); it.hasNext();)
      {
        Adapter adapter = it.next();
        if (adapter.isAdapterForType(TYPE))
        {
          it.remove();
        }
      }
    }
    catch (ConcurrentModificationException ex)
    {
      removeSafe(adapters);
    }
  }

  private static Object getInstance(Object object)
  {
    if (object instanceof InternalCDOObject)
    {
      InternalCDOObject cdoObject = (InternalCDOObject)object;
      object = cdoObject.cdoInternalInstance();
    }

    return object;
  }

  /**
   * Provides the consumer with the {@link CDOState states} of objects such as {@link CDOObject CDOObjects}.
   *
   * @author Eike Stepper
   * @since 4.4
   */
  public interface StateProvider
  {
    public CDOState getState(Object object);
  }
}
