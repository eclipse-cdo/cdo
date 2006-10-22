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

import org.eclipse.net4j.util.registry.IRegistry.Listener.EventType;

import java.util.Set;

/**
 * @author Eike Stepper
 */
public class DelegatingRegistry<ID, E extends IRegistryElement<ID>> extends AbstractRegistry<ID, E>
{
  private IRegistry<ID, E> delegate;

  private Listener<ID, E> delegateListener = new Listener<ID, E>()
  {
    public void notifyRegistryEvent(IRegistry<ID, E> registry, EventType eventType, E element)
    {
      handleDelegateEvent(eventType, element);
    }
  };

  public DelegatingRegistry(IRegistry<ID, E> delegate)
  {
    this(delegate, DEFAULT_RESOLVING);
  }

  public DelegatingRegistry(IRegistry<ID, E> delegate, boolean resolving)
  {
    super(resolving);
    this.delegate = delegate;
    delegate.addListener(delegateListener);
  }

  public IRegistry<ID, E> getDelegate()
  {
    return delegate;
  }

  public void register(E element)
  {
    delegatedRegister(element);
  }

  public void deregister(ID id)
  {
    delegatedDeregister(id);
  }

  public E lookup(ID id, boolean resolve)
  {
    return delegatedLookup(id, resolve);
  }

  public Set<ID> getElementIDs()
  {
    return delegatedGetElementIDs();
  }

  @Override
  public void dispose()
  {
    delegate.removeListener(delegateListener);
  }

  protected void delegatedRegister(E element)
  {
    delegate.register(element);
  }

  protected void delegatedDeregister(ID id)
  {
    delegate.deregister(id);
  }

  protected E delegatedLookup(ID id, boolean resolve)
  {
    return delegate.lookup(id, resolve);
  }

  protected Set<ID> delegatedGetElementIDs()
  {
    return delegate.getElementIDs();
  }

  protected int delegatedSize()
  {
    return delegate.size();
  }

  protected void delegatedDispose()
  {
    delegate.dispose();
  }

  protected void handleDelegateEvent(EventType eventType, E element)
  {
    fireRegistryEvent(eventType, element);
  }

  @Override
  protected void replaceElement(ID id, E element)
  {
    // Do nothing
  }
}
