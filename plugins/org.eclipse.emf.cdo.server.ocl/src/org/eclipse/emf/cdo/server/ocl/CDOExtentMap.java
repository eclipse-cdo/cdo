/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.ocl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Eike Stepper
 */
public class CDOExtentMap implements Map<EClass, Set<? extends EObject>>
{
  private final Map<EClass, Set<? extends EObject>> delegate = new java.util.HashMap<EClass, Set<? extends EObject>>();

  private final OCLExtentCreator extentCreator;

  private AtomicBoolean canceled = new AtomicBoolean(false);

  public CDOExtentMap(OCLExtentCreator extentCreator)
  {
    this.extentCreator = extentCreator;
  }

  public void cancel()
  {
    canceled.set(true);
  }

  public Set<? extends EObject> get(Object key)
  {
    if (key instanceof EClass)
    {
      EClass cls = (EClass)key;

      // TODO: Optimize by parsing ahead of time to find all EClasses that we will query
      Set<? extends EObject> result = delegate.get(cls);
      if (result == null)
      {
        result = extentCreator.createExtent(cls, canceled);
        delegate.put(cls, result);
      }

      return result;
    }

    return null;
  }

  //
  // Strictly delegating methods
  //

  public void clear()
  {
    delegate.clear();
  }

  public boolean containsKey(Object key)
  {
    return delegate.containsKey(key);
  }

  public boolean containsValue(Object value)
  {
    return delegate.containsValue(value);
  }

  public Set<Map.Entry<EClass, Set<? extends EObject>>> entrySet()
  {
    return delegate.entrySet();
  }

  @Override
  public boolean equals(Object obj)
  {
    return delegate.equals(obj);
  }

  @Override
  public int hashCode()
  {
    return delegate.hashCode();
  }

  public boolean isEmpty()
  {
    return delegate.isEmpty();
  }

  public Set<EClass> keySet()
  {
    return delegate.keySet();
  }

  public Set<? extends EObject> put(EClass key, Set<? extends EObject> value)
  {
    return delegate.put(key, value);
  }

  public void putAll(Map<? extends EClass, ? extends Set<? extends EObject>> t)
  {
    delegate.putAll(t);
  }

  public Set<? extends EObject> remove(Object key)
  {
    return delegate.remove(key);
  }

  public int size()
  {
    return delegate.size();
  }

  @Override
  public String toString()
  {
    return delegate.toString();
  }

  public Collection<Set<? extends EObject>> values()
  {
    return delegate.values();
  }
}
