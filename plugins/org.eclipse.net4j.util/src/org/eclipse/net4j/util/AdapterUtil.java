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
package org.eclipse.net4j.util;

/**
 * @author Eike Stepper
 */
public final class AdapterUtil
{
  private AdapterUtil()
  {
  }

  @SuppressWarnings("unchecked")
  public static <TYPE> TYPE adapt(Object object, Class<TYPE> type)
  {
    Object adapter = null;
    if (type.isInstance(object))
    {
      adapter = object;
    }
    else
    {
      try
      {
        adapter = AdaptableHelper.adapt(object, type);
        if (adapter == null)
        {
          adapter = AdapterManagerHelper.adapt(object, type);
        }
      }
      catch (Throwable ignore)
      {
      }
    }

    return (TYPE)adapter;
  }

  /**
   * @author Eike Stepper
   */
  private static final class AdaptableHelper
  {
    public static Object adapt(Object object, Class<?> type)
    {
      if (object instanceof org.eclipse.core.runtime.IAdaptable)
      {
        return ((org.eclipse.core.runtime.IAdaptable)object).getAdapter(type);
      }

      return null;
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class AdapterManagerHelper
  {
    private static org.eclipse.core.runtime.IAdapterManager adapterManager = org.eclipse.core.runtime.Platform
        .getAdapterManager();

    public static Object adapt(Object object, Class<?> type)
    {
      if (adapterManager != null)
      {
        return adapterManager.getAdapter(object, type);
      }

      return null;
    }
  }
}
