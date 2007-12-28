/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo.util;

import org.eclipse.emf.internal.cdo.CDOViewImpl;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public final class ProxyResolverURIResourceMap implements Map<URI, Resource>
{
  private Map<URI, Resource> delegate;

  private Resource proxyResolverResource;

  public ProxyResolverURIResourceMap(CDOViewImpl view, Map<URI, Resource> delegate)
  {
    if (delegate == null)
    {
      delegate = new HashMap<URI, Resource>(); // TODO Cleanup of this lookup
      // cache?
    }

    this.delegate = delegate;
    proxyResolverResource = new ProxyResolverResource(view);
  }

  public Resource get(Object key)
  {
    if (key instanceof URI)
    {
      URI uri = (URI)key;
      String scheme = uri.scheme();
      if ("cdo".equals(scheme))
      {
        String opaquePart = uri.opaquePart();
        if ("proxy".equals(opaquePart))
        {
          return proxyResolverResource;
        }
      }
    }

    return delegate.get(key);
  }

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

  public Set<Entry<URI, Resource>> entrySet()
  {
    return delegate.entrySet();
  }

  @Override
  public boolean equals(Object o)
  {
    return delegate.equals(o);
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

  public Set<URI> keySet()
  {
    return delegate.keySet();
  }

  public Resource put(URI key, Resource value)
  {
    return delegate.put(key, value);
  }

  public void putAll(Map<? extends URI, ? extends Resource> t)
  {
    delegate.putAll(t);
  }

  public Resource remove(Object key)
  {
    return delegate.remove(key);
  }

  public int size()
  {
    return delegate.size();
  }

  public Collection<Resource> values()
  {
    return delegate.values();
  }
}
