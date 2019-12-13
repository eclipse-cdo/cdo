/*
 * Copyright (c) 2007, 2008, 2010-2012, 2017, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.internal.net4j.buffer;

import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.Notifier;

/**
 * @author Eike Stepper
 */
public abstract class BufferProvider extends Notifier implements IBufferProvider.Introspection, IBufferProvider.Notification
{
  private short bufferCapacity;

  private long providedBuffers;

  private long retainedBuffers;

  public BufferProvider(short bufferCapacity)
  {
    this.bufferCapacity = bufferCapacity;
  }

  @Override
  public final long getProvidedBuffers()
  {
    return providedBuffers;
  }

  @Override
  public final long getRetainedBuffers()
  {
    return retainedBuffers;
  }

  @Override
  public final short getBufferCapacity()
  {
    return bufferCapacity;
  }

  @Override
  public final IBuffer provideBuffer()
  {
    IBuffer buffer = doProvideBuffer();
    ++providedBuffers;

    IListener[] listeners = getListeners();
    if (listeners != null)
    {
      fireEvent(new BufferProviderEvent.Provided(this, buffer), listeners);
    }

    return buffer;
  }

  @Override
  public final void retainBuffer(IBuffer buffer)
  {
    IListener[] listeners = getListeners();
    if (listeners != null)
    {
      fireEvent(new BufferProviderEvent.Retaining(this, buffer), listeners);
    }

    doRetainBuffer(buffer);
    ++retainedBuffers;
  }

  @Override
  public String toString()
  {
    return "BufferProvider[capacity=" + bufferCapacity + "]"; //$NON-NLS-1$ //$NON-NLS-2$
  }

  protected abstract IBuffer doProvideBuffer();

  protected abstract void doRetainBuffer(IBuffer buffer);

  /**
   * @author Eike Stepper
   */
  protected static abstract class BufferProviderEvent extends Event
  {
    private static final long serialVersionUID = 1L;

    private final IBuffer buffer;

    public BufferProviderEvent(IBufferProvider.Notification bufferProvider, IBuffer buffer)
    {
      super(bufferProvider);
      this.buffer = buffer;
    }

    @Override
    public IBufferProvider.Notification getSource()
    {
      return (IBufferProvider.Notification)super.getSource();
    }

    public IBuffer getBuffer()
    {
      return buffer;
    }

    @Override
    protected String formatAdditionalParameters()
    {
      return "buffer=" + buffer;
    }

    /**
     * @author Eike Stepper
     */
    private static final class Provided extends BufferProviderEvent implements BufferProvidedEvent
    {
      private static final long serialVersionUID = 1L;

      public Provided(IBufferProvider.Notification bufferProvider, IBuffer buffer)
      {
        super(bufferProvider, buffer);
      }

      @Override
      protected String formatEventName()
      {
        return "BufferProvidedEvent";
      }
    }

    /**
     * @author Eike Stepper
     */
    private static final class Retaining extends BufferProviderEvent implements BufferRetainingEvent
    {
      private static final long serialVersionUID = 1L;

      public Retaining(IBufferProvider.Notification bufferProvider, IBuffer buffer)
      {
        super(bufferProvider, buffer);
      }

      @Override
      protected String formatEventName()
      {
        return "BufferRetainingEvent";
      }
    }
  }
}
