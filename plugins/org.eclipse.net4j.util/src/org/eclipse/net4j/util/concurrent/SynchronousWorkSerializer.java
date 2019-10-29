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

/**
 * @author Eike Stepper
 * @deprecated As of 3.9 use an executor such as {@link SynchronousExecutor}.
 */
@Deprecated
public class SynchronousWorkSerializer implements IWorkSerializer
{
  public SynchronousWorkSerializer()
  {
  }

  public boolean addWork(Runnable work)
  {
    work.run();
    return true;
  }

  public void dispose()
  {
  }

  @Override
  public String toString()
  {
    return SynchronousWorkSerializer.class.getSimpleName();
  }
}
