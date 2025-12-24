/*
 * Copyright (c) 2019 Eike Stepper (Loehne, Germany) and others.
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

  @Override
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
