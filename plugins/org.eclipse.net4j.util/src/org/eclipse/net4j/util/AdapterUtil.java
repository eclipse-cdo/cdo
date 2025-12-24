/*
 * Copyright (c) 2008, 2009, 2011-2013, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util;

/**
 * Provides a single static {@link #adapt(Object, Class) adapt()} method that conveniently and safely wraps the
 * Platform's adaptation framework.
 *
 * @author Eike Stepper
 */
public final class AdapterUtil
{
  private AdapterUtil()
  {
  }

  /**
   * @since 3.5
   */
  public static <TYPE> boolean adapts(Object object, Class<TYPE> type)
  {
    return adapt(object, type, true) != null;
  }

  public static <TYPE> TYPE adapt(Object object, Class<TYPE> type)
  {
    return adapt(object, type, true);
  }

  /**
   * @since 3.5
   */
  public static <TYPE> TYPE adapt(Object object, Class<TYPE> type, boolean consultObject)
  {
    if (object == null)
    {
      return null;
    }

    Object adapter = null;
    if (type.isInstance(object))
    {
      adapter = object;
    }
    else
    {
      try
      {
        if (consultObject)
        {
          adapter = AdaptableHelper.adapt(object, type);
        }

        if (adapter == null)
        {
          adapter = AdapterManagerHelper.adapt(object, type);
        }
      }
      catch (StackOverflowError ex)
      {
        if (consultObject)
        {
          try
          {
            return adapt(object, type, false);
          }
          catch (Throwable ignore)
          {
            //$FALL-THROUGH$
          }
        }
      }
      catch (Throwable ignore)
      {
        //$FALL-THROUGH$
      }
    }

    return type.cast(adapter);
  }

  /**
   * Nested class to factor out dependencies on org.eclipse.core.runtime
   *
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
   * Nested class to factor out dependencies on org.eclipse.core.runtime
   *
   * @author Eike Stepper
   */
  private static final class AdapterManagerHelper
  {
    private static org.eclipse.core.runtime.IAdapterManager adapterManager = org.eclipse.core.runtime.Platform.getAdapterManager();

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
