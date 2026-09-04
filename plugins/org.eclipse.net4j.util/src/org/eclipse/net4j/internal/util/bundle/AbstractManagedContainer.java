/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.internal.util.bundle;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.lifecycle.Lifecycle;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public abstract class AbstractManagedContainer extends Lifecycle implements IManagedContainer
{
  private final transient Set<AbstractBundle> preparedBundles = Collections.newSetFromMap(new IdentityHashMap<>());

  private final transient Map<AbstractBundle, Thread> preparingBundles = new IdentityHashMap<>();

  public AbstractManagedContainer()
  {
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    try
    {
      super.doDeactivate();
    }
    finally
    {
      synchronized (this)
      {
        preparedBundles.clear();
        preparingBundles.clear();
        notifyAll();
      }
    }
  }

  /**
   * Enters preparation of a bundle for this container.
   *
   * @param bundle the bundle being prepared
   * @return {@code true} if preparation should be performed by the caller
   */
  synchronized boolean beginBundlePreparation(AbstractBundle bundle)
  {
    Thread currentThread = Thread.currentThread();

    for (;;)
    {
      if (preparedBundles.contains(bundle))
      {
        return false;
      }

      Thread preparingThread = preparingBundles.get(bundle);
      if (preparingThread == null)
      {
        preparingBundles.put(bundle, currentThread);
        return true;
      }

      if (preparingThread == currentThread)
      {
        return false;
      }

      try
      {
        wait();
      }
      catch (InterruptedException ex)
      {
        Thread.currentThread().interrupt();
        throw new IllegalStateException("Interrupted while preparing bundle", ex); //$NON-NLS-1$
      }
    }
  }

  /**
   * Leaves preparation of a bundle for this container.
   *
   * @param bundle the bundle being prepared
   * @param successful whether preparation completed successfully
   */
  synchronized void endBundlePreparation(AbstractBundle bundle, boolean successful)
  {
    if (successful)
    {
      preparedBundles.add(bundle);
    }

    preparingBundles.remove(bundle);
    notifyAll();
  }
}
