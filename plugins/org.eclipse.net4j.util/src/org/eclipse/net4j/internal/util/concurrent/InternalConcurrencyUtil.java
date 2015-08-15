/*
 * Copyright (c) 2007-2009, 2011-2013, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.util.concurrent;

import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.container.IManagedContainer;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 */
public final class InternalConcurrencyUtil
{
  private InternalConcurrencyUtil()
  {
  }

  /**
   * @since 3.5
   */
  public static ExecutorService getExecutorService(Object object)
  {
    if (object != null)
    {
      if (object instanceof IExecutorServiceProvider)
      {
        try
        {
          return ((IExecutorServiceProvider)object).getExecutorService();
        }
        catch (Exception ex)
        {
          //$FALL-THROUGH$
        }
      }

      if (object instanceof IManagedContainer)
      {
        try
        {
          return ConcurrencyUtil.getExecutorService((IManagedContainer)object);
        }
        catch (Exception ex)
        {
          //$FALL-THROUGH$
        }
      }

      try
      {
        Method method = ReflectUtil.getMethod(object.getClass(), "getExecutorService");
        Object result = ReflectUtil.invokeMethod(method, object);
        if (result instanceof ExecutorService)
        {
          return (ExecutorService)result;
        }
      }
      catch (Throwable ex)
      {
        //$FALL-THROUGH$
      }
    }

    return null;
  }
}
