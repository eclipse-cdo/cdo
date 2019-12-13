/*
 * Copyright (c) 2007, 2011, 2012, 2015, 2017, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buffer;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.INotifier;

/**
 * Provides clients with the ability to obtain and retain {@link IBuffer}s.
 *
 * @author Eike Stepper
 */
public interface IBufferProvider
{
  /**
   * Returns the capacity of the buffers provided by {@link #provideBuffer()} .
   */
  public short getBufferCapacity();

  /**
   * Provides a buffer from this <code>BufferProvider</code>.
   */
  public IBuffer provideBuffer();

  /**
   * Retains a buffer to this <code>BufferProvider</code>.
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
