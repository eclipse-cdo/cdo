/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.event;

import org.eclipse.net4j.util.concurrent.IExecutorServiceProvider;

import java.util.concurrent.ExecutorService;

/**
 * Deprecated.
 *
 * @author Eike Stepper
 * @since 2.0
 */
@Deprecated
public class ExecutorServiceNotifier extends Notifier implements IExecutorServiceProvider
{
  private ExecutorService notificationExecutorService;

  public ExecutorServiceNotifier()
  {
  }

  /**
   * @since 3.6
   */
  @Override
  public ExecutorService getExecutorService()
  {
    return notificationExecutorService;
  }

  @Override
  public ExecutorService getNotificationService()
  {
    return notificationExecutorService;
  }

  public void setNotificationExecutorService(ExecutorService notificationExecutorService)
  {
    this.notificationExecutorService = notificationExecutorService;
  }

  /**
   * Deprecated.
   *
   * @author Eike Stepper
   */
  @Deprecated
  public static class ThreadPool extends ExecutorServiceNotifier
  {
  }
}
