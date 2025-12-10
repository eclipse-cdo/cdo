/*
 * Copyright (c) 2008, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletionService;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

/**
 * @author Eike Stepper
 * @deprecated As of 3.6 use {@link ExecutorWorkSerializer}.
 */
@Deprecated
public class CompletionWorkSerializer implements IWorkSerializer
{
  private CompletionService<Object> completionService;

  @Deprecated
  public CompletionWorkSerializer(CompletionService<Object> completionService)
  {
    this.completionService = completionService;
  }

  @Deprecated
  public CompletionWorkSerializer(Executor executor, BlockingQueue<Future<Object>> completionQueue)
  {
    this(new ExecutorCompletionService<>(executor, completionQueue));
  }

  @Deprecated
  public CompletionWorkSerializer(Executor executor)
  {
    this(new ExecutorCompletionService<>(executor));
  }

  @Deprecated
  public CompletionWorkSerializer()
  {
    this(new OnePendingExecutor());
  }

  @Deprecated
  public CompletionService<Object> getCompletionService()
  {
    return completionService;
  }

  @Deprecated
  @Override
  public void dispose()
  {
  }

  @Deprecated
  @Override
  public boolean addWork(Runnable work)
  {
    completionService.submit(work, true);
    return true;
  }

  @Deprecated
  @Override
  public String toString()
  {
    return CompletionWorkSerializer.class.getSimpleName();
  }
}
