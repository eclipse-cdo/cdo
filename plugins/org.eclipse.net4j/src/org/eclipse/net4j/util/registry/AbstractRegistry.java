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
package org.eclipse.net4j.util.registry;

import org.eclipse.net4j.util.om.ContextTracer;
import org.eclipse.net4j.util.registry.IRegistry.Listener.EventType;
import org.eclipse.net4j.util.registry.IRegistryElement.Descriptor;

import org.eclipse.internal.net4j.bundle.Net4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Eike Stepper
 */
public abstract class AbstractRegistry<ID, E extends IRegistryElement<ID>> implements
    IRegistry<ID, E>
{
  public static final boolean DEFAULT_RESOLVING = true;

  private static final ContextTracer TRACER = new ContextTracer(Net4j.DEBUG_REGISTRY,
      AbstractRegistry.class);

  private boolean resolving;

  /**
   * Don't initialize lazily to circumvent synchronization!
   */
  private Queue<Listener> listeners = new ConcurrentLinkedQueue();

  protected AbstractRegistry()
  {
    this(DEFAULT_RESOLVING);
  }

  public AbstractRegistry(boolean resolving)
  {
    setResolving(resolving);
  }

  public void setResolving(boolean resolving)
  {
    this.resolving = resolving;
  }

  public final boolean isResolving()
  {
    return resolving;
  }

  public final boolean isResolved(ID id)
  {
    E element = lookup(id, false);
    return element != null && !(element instanceof Descriptor);
  }

  public final boolean isRegistered(ID id)
  {
    return lookup(id, false) != null;
  }

  public int size()
  {
    return getElementIDs().size();
  }

  public final E lookup(ID id)
  {
    return lookup(id, isResolving());
  }

  public final Collection<E> getElements()
  {
    return getElements(isResolving());
  }

  /**
   * Synchronized to support {@link #resolveElement(IRegistryElement)}
   */
  public final synchronized Collection<E> getElements(boolean resolve)
  {
    Set<ID> elementKeys = getElementIDs();
    List<E> elements = new ArrayList(elementKeys.size());
    for (ID id : elementKeys)
    {
      elements.add(lookup(id, resolve));
    }

    return elements;
  }

  public void addListener(IRegistry.Listener<ID, E> listener)
  {
    listeners.add(listener);
  }

  public void removeListener(IRegistry.Listener<ID, E> listener)
  {
    listeners.remove(listener);
  }

  public void dispose()
  {
    listeners.clear();
  }

  protected void fireRegistryEvent(EventType eventType, E element)
  {
    for (Listener listener : listeners)
    {
      try
      {
        listener.notifyRegistryEvent(this, eventType, element);
      }
      catch (Exception ex)
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace(ex);
        }
      }
    }
  }

  protected void fireElementRegistered(E element)
  {
    fireRegistryEvent(EventType.REGISTERED, element);
  }

  protected void fireElementDeregistering(E element)
  {
    fireRegistryEvent(EventType.DEREGISTERING, element);
  }

  protected void fireElementResolved(E element)
  {
    fireRegistryEvent(EventType.RESOLVED, element);
  }

  /**
   * Calling thread must already synchronize on this {@link IRegistry}!
   */
  protected E resolveElement(E element)
  {
    if (element instanceof Descriptor)
    {
      element = (E)((Descriptor)element).resolve();
      replaceElement(element.getID(), element);
      fireElementResolved(element);
    }

    return element;
  }

  /**
   * Calling thread must already synchronize on this {@link IRegistry}!
   */
  protected abstract void replaceElement(ID id, E element);
}
