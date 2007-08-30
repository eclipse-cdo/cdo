/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.util.concurrent;

import org.eclipse.net4j.util.concurrent.IWorkSerializer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletionService;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;

/**
 * @author Eike Stepper
 */
public class CompletionWorkSerializer implements IWorkSerializer
{
  private CompletionService completionService;

  public CompletionWorkSerializer(CompletionService completionService)
  {
    this.completionService = completionService;
  }

  public CompletionWorkSerializer(Executor executor, BlockingQueue completionQueue)
  {
    this(new ExecutorCompletionService(executor, completionQueue));
  }

  public CompletionWorkSerializer(Executor executor)
  {
    this(new ExecutorCompletionService(executor));
  }

  public CompletionWorkSerializer()
  {
    this(new OnePendingExecutor());
  }

  public CompletionService getCompletionService()
  {
    return completionService;
  }

  public void dispose()
  {
  }

  public void addWork(Runnable work)
  {
    completionService.submit(work, true);
  }
}
