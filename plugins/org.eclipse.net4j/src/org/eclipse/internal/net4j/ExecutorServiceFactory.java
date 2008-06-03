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
package org.eclipse.internal.net4j;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.Factory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author Eike Stepper
 */
public class ExecutorServiceFactory extends Factory
{
  public static final String PRODUCT_GROUP = "org.eclipse.net4j.executorServices";

  public static final String TYPE = "default";

  public static final String DEFAULT_THREAD_GROUP_NAME = "net4j";

  public ExecutorServiceFactory()
  {
    super(PRODUCT_GROUP, TYPE);
  }

  public ExecutorService create(String threadGroupName)
  {
    if (threadGroupName == null)
    {
      threadGroupName = DEFAULT_THREAD_GROUP_NAME;
    }

    final ThreadGroup threadGroup = new ThreadGroup(threadGroupName);
    return Executors.newCachedThreadPool(new ThreadFactory()
    {
      public Thread newThread(Runnable r)
      {
        Thread thread = new Thread(threadGroup, r);
        thread.setDaemon(true);
        return thread;
      }
    });
  }

  public static ExecutorService get(IManagedContainer container)
  {
    return (ExecutorService)container.getElement(PRODUCT_GROUP, TYPE, null);
  }
}
