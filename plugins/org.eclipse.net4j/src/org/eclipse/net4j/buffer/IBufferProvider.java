/*
 * Copyright (c) 2007, 2011, 2012, 2015, 2017, 2019, 2025 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.INotifier;

/**
 * Provides {@link IBuffer} storage to clients and accepts buffers whose current ownership has been relinquished.
 * <p>
 * A call to {@link #provideBuffer()} transfers one buffer to the caller, which must eventually call
 * {@link IBuffer#release()}. A provider may then reuse, pool, or dispose of the buffer in
 * {@link #retainBuffer(IBuffer)}. These methods describe a storage ownership cycle; they do not imply that every buffer
 * passed to {@code retainBuffer} was originally created by this provider.
 *
 * @author Eike Stepper
 */
public interface IBufferProvider
{
  /**
   * Returns the capacity, in bytes, of buffers returned by {@link #provideBuffer()}.
   */
  public short getBufferCapacity();

  /**
   * Provides a buffer and transfers its current ownership to the caller.
   */
  public IBuffer provideBuffer();

  /**
   * Accepts a buffer whose caller has relinquished ownership. Implementations may keep it for reuse or release its
   * storage; callers must not use the buffer after handing it over.
   */
  public void retainBuffer(IBuffer buffer);

  /**
   * Offers additional introspection features for {@link IBufferProvider buffer providers}.
   *
   * @author Eike Stepper
   */
  public interface Introspection extends IBufferProvider
  {
    /**
     * Returns the number of buffers that have already been provided by this <code>BufferProvider</code>.
     */
    public long getProvidedBuffers();

    /**
     * Returns the number of buffers that have already been retained to this <code>BufferProvider</code>.
     */
    public long getRetainedBuffers();
  }

  /**
   * Offers additional notification features for {@link IBufferProvider buffer providers}.
   *
   * @author Eike Stepper
   * @since 4.6
   */
  public interface Notification extends IBufferProvider, INotifier
  {
    /**
     * An {@link IEvent event} fired from a {@link IBufferProvider buffer provider} to indicate that a buffer has been provided.
     *
     * @author Eike Stepper
     * @noextend This interface is not intended to be extended by clients.
     * @noimplement This interface is not intended to be implemented by clients.
     */
    public interface BufferProvidedEvent extends IEvent
    {
      @Override
      public IBufferProvider.Notification getSource();

      public IBuffer getBuffer();
    }

    /**
     * An {@link IEvent event} fired from a {@link IBufferProvider buffer provider} to indicate that a buffer is about to be retained.
     *
     * @author Eike Stepper
     * @noextend This interface is not intended to be extended by clients.
     * @noimplement This interface is not intended to be implemented by clients.
     */
    public interface BufferRetainingEvent extends IEvent
    {
      @Override
      public IBufferProvider.Notification getSource();

      public IBuffer getBuffer();
    }
  }
}
