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

import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public abstract class AbstractMappingRegistry<ID, E extends IRegistryElement<ID>> extends
    AbstractRegistry<ID, E>
{
  public AbstractMappingRegistry()
  {
    this(DEFAULT_RESOLVING);
  }

  public AbstractMappingRegistry(boolean resolving)
  {
    super(resolving);
  }

  public synchronized void register(E element)
  {
    E oldElement = getMap().put(element.getID(), element);
    if (oldElement != null)
    {
      fireElementDeregistering(oldElement);
      oldElement.dispose();
    }

    fireElementRegistered(element);
  }

  public synchronized void deregister(ID id)
  {
    E element = getMap().remove(id);
    if (element != null)
    {
      fireElementDeregistering(element);
      element.dispose();
    }
  }

  /**
   * Synchronized to support {@link #resolveElement(IRegistryElement)}
   */
  public synchronized E lookup(ID id, boolean resolve)
  {
    E element = getMap().get(id);
    if (resolve)
    {
      element = resolveElement(element);
    }

    return element;
  }

  public Set<ID> getElementIDs()
  {
    return getMap().keySet();
  }

  @Override
  public synchronized void dispose()
  {
    for (E element : getMap().values())
    {
      element.dispose();
    }

    getMap().clear();
  }

  @Override
  protected void replaceElement(ID id, E element)
  {
    getMap().put(id, element);
  }

  protected abstract Map<ID, E> getMap();
}
