/*
 * Copyright (c) 2007, 2008, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buffer;

/**
 * Provides clients with the ability to obtain and retain pooled {@link IBuffer}s.
 *
 * @author Eike Stepper
 */
public interface IBufferPool extends IBufferProvider
{
  /**
   * Tries to remove a single buffer from this <code>BufferPool</code> and {@link IBuffer#release() release} it.
   *
   * @return <code>true</code> if a buffer could be evicted, <code>false</code> otherwise.
   */
  public boolean evictOne();

  /**
   * Tries to remove as many buffers from this <code>BufferPool</code> and {@link IBuffer#release() release} them as are
   * needed to let a given maximum number of buffers survive in the pool.
   *
   * @return The number of buffers that could be evicted.
   */
  public int evict(int survivors);

  /**
   * Offers additional introspection features for {@link IBufferPool}s.
   *
   * @author Eike Stepper
   */
  public interface Introspection extends IBufferPool, IBufferProvider.Introspection
  {
    /**
     * Returns the number of buffers that are currently pooled in this <code>BufferPool</code>.
     */
    public int getPooledBuffers();
  }
}
