/*
 * Copyright (c) 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.lifecycle;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A {@link Lifecycle lifecycle} entity with a reference count that can be {@link #activate() activated} multiple times.
 *
 * @author Eike Stepper
 * @since 3.3
 */
public class ShareableLifecycle extends Lifecycle
{
  AtomicInteger refCount;

  public ShareableLifecycle()
  {
    this(true);
  }

  public ShareableLifecycle(boolean shared)
  {
    if (shared)
    {
      refCount = new AtomicInteger();
    }
  }

  @Override
  void internalActivate() throws LifecycleException
  {
    if (refCount == null || refCount.getAndIncrement() == 0)
    {
      super.internalActivate();
    }
  }

  @Override
  Exception internalDeactivate()
  {
    if (refCount == null || refCount.decrementAndGet() == 0)
    {
      return super.internalDeactivate();
    }

    return null;
  }
}
