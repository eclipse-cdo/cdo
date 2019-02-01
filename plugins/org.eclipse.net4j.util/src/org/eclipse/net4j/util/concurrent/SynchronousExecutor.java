/*
 * Copyright (c) 2004-2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.concurrent;

import java.util.concurrent.Executor;

/**
 * @author Eike Stepper
 * @since 3.9
 */
public final class SynchronousExecutor implements Executor
{
  public static final Executor INSTANCE = new SynchronousExecutor();

  private static final String NAME = SynchronousExecutor.class.getSimpleName() + ".INSTANCE";

  private SynchronousExecutor()
  {
  }

  public void execute(Runnable work)
  {
    work.run();
  }

  @Override
  public String toString()
  {
    return NAME;
  }
}
