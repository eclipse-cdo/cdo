/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.registry.delegator;

import org.eclipse.net4j.util.registry.IRegistry;
import org.eclipse.net4j.util.registry.IRegistryElement.Descriptor;
import org.eclipse.net4j.util.registry.IRegistryListener.EventType;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * TODO Check if all methods of {@link DelegatingRegistry} still do what they
 * should. TODO Remove {@link DelegatingRegistry}?
 * 
 * @author Eike Stepper
 */
public abstract class AbstractCachingRegistry<ID, E extends IRegistryElement<ID>> extends
    DelegatingRegistry<ID, E>
{
  public AbstractCachingRegistry(IRegistry<ID, E> delegate)
  {
    this(delegate, DEFAULT_RESOLVING);
  }

  public AbstractCachingRegistry(IRegistry<ID, E> delegate, boolean resolving)
  {
    super(delegate, resolving);
  }

  @Override
  public synchronized void register(E element)
  {
    ID id = element.getID();
    E delegatedElement = super.lookup(id, false);
    E oldElement = getCache().put(id, element);

    if (oldElement == null)
    {
      if (delegatedElement != null)
      {
        // Unhidden delegated element now becomes hidden
        fireElementDeregistering(delegatedElement);
      }
    }
    else
    {
      fireElementDeregistering(oldElement);
    }

    fireElementRegistered(element);
  }

  @Override
  public synchronized void deregister(ID id)
  {
    E delegatedElement = super.lookup(id, false);
    E element = getCache().remove(id);

    if (element != null)
    {
      fireElementDeregistering(element);

      if (delegatedElement != null)
      {
        // Hidden delegated element now becomes unhidden
        fireElementRegistered(delegatedElement);
      }
    }
  }

  /**
   * Synchronized to support {@link #resolveElement(IRegistryElement)}
   */
  @Override
  public synchronized E lookup(ID id, boolean resolve)
  {
    E element = getCache().get(id);
    if (element == null)
    {
      if (resolve)
      {
        return resolveDelegatedElement(id);
      }
      else
      {
        return super.lookup(id, false);
      }
    }

    if (resolve)
    {
      element = resolveElement(element);
    }

    return element;
  }

  @Override
  public Set<ID> getElementIDs()
  {
    final Set<ID> delegateIDs = super.getElementIDs();
    final Set<ID> cacheIDs = getCache().keySet();
    final Set<ID> ids = new HashSet(delegateIDs.size() + cacheIDs.size());

    ids.addAll(delegateIDs);
    ids.addAll(cacheIDs);
    return ids;
  }

  @Override
  public synchronized void dispose()
  {
    for (E element : getCache().values())
    {
      fireElementDeregistering(element);
    }

    getCache().clear();
    super.dispose();
  }

  @Override
  protected void replaceElement(ID id, E element)
  {
    getCache().put(id, element);
  }

  @Override
  protected synchronized void handleDelegateEvent(EventType eventType, E element)
  {
    if (!getCache().containsKey(element.getID()))
    {
      fireRegistryEvent(eventType, element);
    }
  }

  protected E resolveDelegatedElement(ID id)
  {
    synchronized (getDelegate())
    {
      boolean wasUnresolved = super.lookup(id, false) instanceof Descriptor;
      E e = super.lookup(id, true);
      if (wasUnresolved)
      {
        fireElementResolved(e);
      }

      return e;
    }
  }

  protected abstract Map<ID, E> getCache();
}
