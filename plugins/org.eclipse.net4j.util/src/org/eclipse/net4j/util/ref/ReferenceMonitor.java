/*
 * Copyright (c) 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ref;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/**
 * @author Eike Stepper
 * @since 3.5
 */
public abstract class ReferenceMonitor<T> extends ReferenceQueueWorker<T>
{
  public ReferenceMonitor()
  {
  }

  public void monitor(T object)
  {
    ReferenceQueue<T> queue = getQueue();
    new WeakReference<>(object, queue);
  }

  @Override
  protected void work(Reference<? extends T> reference)
  {
    T object = reference.get();
    if (object != null)
    {
      work(object);
    }
  }

  protected abstract void work(T object);
}
