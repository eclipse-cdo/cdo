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

import java.util.Collection;
import java.util.Set;

/**
 * Implementation note: {@link Object#equals(Object)} and
 * {@link Object#hashCode()} are based on pointer equality.
 * <p>
 * 
 * @author Eike Stepper
 */
public interface IRegistry<ID, E extends IRegistryElement<ID>>
{
  public void setResolving(boolean resolving);

  public boolean isResolving();

  public boolean isResolved(ID id);

  public boolean isRegistered(ID id);

  public int size();

  public void register(E element);

  public void deregister(ID id);

  public E lookup(ID id);

  public E lookup(ID id, boolean resolve);

  public Set<ID> getElementIDs();

  public Collection<E> getElements();

  public Collection<E> getElements(boolean resolve);

  public void addListener(Listener<ID, E> listener);

  public void removeListener(Listener<ID, E> listener);

  public void dispose();

  /**
   * @author Eike Stepper
   */
  public interface Listener<ID, E extends IRegistryElement<ID>>
  {
    public void notifyRegistryEvent(IRegistry<ID, E> registry, EventType eventType, E element);

    /**
     * @author Eike Stepper
     */
    public enum EventType
    {
      REGISTERED, DEREGISTERING, RESOLVED
    }
  }
}
