/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.internal.cdo.util;

import org.eclipse.net4j.util.container.Container;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public abstract class AbstractRegistry<T, R> extends Container<R>
{
  public static final int NOT_REGISTERED = 0;

  private final Map<Integer, R> registrationsByID = new HashMap<>();

  private final Map<T, R> registrationsByElement = new HashMap<>();

  private int lastID;

  public AbstractRegistry()
  {
  }

  public T[] getRegisteredElements()
  {
    R[] registrations = getElements();
    T[] registeredElements = newArray(registrations.length);
  
    for (int i = 0; i < registeredElements.length; i++)
    {
      registeredElements[i] = getRegisteredElement(registrations[i]);
    }
  
    return registeredElements;
  }

  @Override
  public synchronized R[] getElements()
  {
    int size = registrationsByID.size();
    R[] array = newRegistrationArray(size);
    return registrationsByID.values().toArray(array);
  }

  public synchronized T getElement(int id)
  {
    R registration = registrationsByID.get(id);
    if (registration != null)
    {
      return getRegisteredElement(registration);
    }
  
    return null;
  }

  public synchronized int[] getIDs()
  {
    int[] result = new int[registrationsByID.size()];

    int i = 0;
    for (Integer id : registrationsByID.keySet())
    {
      result[i++] = id;
    }

    return result;
  }

  public synchronized int getID(T element)
  {
    R registration = registrationsByElement.get(element);
    if (registration != null)
    {
      return getRegisteredID(registration);
    }

    return NOT_REGISTERED;
  }

  protected final void registerElement(T element)
  {
    R registration;
    synchronized (this)
    {
      if (registrationsByElement.containsKey(element))
      {
        return;
      }

      int id = ++lastID;
      registration = newRegistration(id, element);

      registrationsByID.put(id, registration);
      registrationsByElement.put(element, registration);
    }

    fireElementAddedEvent(registration);
  }

  protected final void deregisterElement(T element)
  {
    R registration;
    synchronized (this)
    {
      registration = registrationsByElement.remove(element);
      if (registration != null)
      {
        int id = getRegisteredID(registration);
        registrationsByID.remove(id);
      }
    }

    fireElementRemovedEvent(registration);
  }

  protected abstract T[] newArray(int size);

  protected abstract R[] newRegistrationArray(int size);

  protected abstract R newRegistration(int id, T element);

  protected abstract int getRegisteredID(R registration);

  protected abstract T getRegisteredElement(R registration);
}
