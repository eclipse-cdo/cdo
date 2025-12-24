/*
 * Copyright (c) 2009, 2011, 2012, 2015, 2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.concurrent;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.event.EventUtil;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.factory.Factory;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleException;
import org.eclipse.net4j.util.lifecycle.LifecycleState;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;

import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class ExecutorServiceFactory extends Factory
{
  public static final String DEFAULT_THREAD_GROUP_NAME = "net4j"; //$NON-NLS-1$

  public static final String PRODUCT_GROUP = "org.eclipse.net4j.executorServices"; //$NON-NLS-1$

  /**
   * @deprecated As of 4.8 the value of the system property "org.eclipse.net4j.util.concurrent.ExecutorServiceFactory.type" is used.
   */
  @Deprecated
  public static final String TYPE = "default"; //$NON-NLS-1$

  private static final String DEFAULT_TYPE = OMPlatform.INSTANCE.getProperty("org.eclipse.net4j.util.concurrent.ExecutorServiceFactory.type", "default"); //$NON-NLS-1$

  private static final String DEFAULT_DESCRIPTION = OMPlatform.INSTANCE.getProperty("org.eclipse.net4j.util.concurrent.ExecutorServiceFactory.description");

  public ExecutorServiceFactory()
  {
    super(PRODUCT_GROUP, DEFAULT_TYPE);
  }

  @Override
  public ExecutorService create(String description)
  {
    final ExecutorService executorService = ThreadPool.create(description);

    return LifecycleUtil.delegateLifecycle(getClass().getClassLoader(), executorService, ExecutorService.class, new ILifecycle()
    {
      private boolean active;

      @Override
      public void activate() throws LifecycleException
      {
        active = true;
      }

      @Override
      public Exception deactivate()
      {
        try
        {
          executorService.shutdown();
          active = false;
          return null;
        }
        catch (Exception ex)
        {
          return ex;
        }
      }

      @Override
      public LifecycleState getLifecycleState()
      {
        return active ? LifecycleState.ACTIVE : LifecycleState.INACTIVE;
      }

      @Override
      public boolean isActive()
      {
        return active;
      }

      @Override
      public void addListener(IListener listener)
      {
        // Do nothing
      }

      @Override
      public void removeListener(IListener listener)
      {
        // Do nothing
      }

      @Override
      public IListener[] getListeners()
      {
        return EventUtil.NO_LISTENERS;
      }

      @Override
      public boolean hasListeners()
      {
        return false;
      }

      @Override
      public String toString()
      {
        return "CachedThreadPool";
      }
    });
  }

  public static ExecutorService get(IManagedContainer container)
  {
    return (ExecutorService)container.getElement(PRODUCT_GROUP, DEFAULT_TYPE, DEFAULT_DESCRIPTION);
  }
}
