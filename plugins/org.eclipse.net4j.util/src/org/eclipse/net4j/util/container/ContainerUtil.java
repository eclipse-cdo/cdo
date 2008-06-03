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
package org.eclipse.net4j.util.container;

import org.eclipse.net4j.util.event.IListener;

/**
 * @author Eike Stepper
 */
public final class ContainerUtil
{
  private static final Object[] NO_ELEMENTS = {};

  private static final IContainer<Object> EMPTY = new IContainer<Object>()
  {
    public Object[] getElements()
    {
      return NO_ELEMENTS;
    }

    public boolean isEmpty()
    {
      return true;
    }

    public void addListener(IListener listener)
    {
    }

    public void removeListener(IListener listener)
    {
    }

    @Override
    public String toString()
    {
      return "EMPTY_CONTAINER";
    }
  };

  private ContainerUtil()
  {
  }

  public static IContainer<Object> emptyContainer()
  {
    return EMPTY;
  }

  public static IManagedContainer createContainer()
  {
    return new ManagedContainer();
  }

  public static boolean isEmpty(Object container)
  {
    if (container instanceof IContainer)
    {
      return ((IContainer<?>)container).isEmpty();
    }

    return true;
  }

  public static Object[] getElements(Object container)
  {
    if (container instanceof IContainer)
    {
      return ((IContainer<?>)container).getElements();
    }

    return NO_ELEMENTS;
  }
}
